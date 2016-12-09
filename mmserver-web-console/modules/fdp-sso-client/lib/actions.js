var redis = require('./data-proxy');
var fdpUtils = require('fdp-utils');
var logger = require('fdp-logger');
var fs = require('fs');
var path = require('path');
var oConf= fdpUtils.getJsonProp('/fdp-config.js', 'fdp-sso-client');
/*当浏览器发起的会话同步动作完成之后的响应界面*/
var sSessAsyHtml = new Buffer(fs.readFileSync(path.join(__dirname, 'sessionsyn.view')));
/*记录意向链接在session中的key*/
var sIntendedPathKey = '__intended-path-when-kick-out-by-fdp-security__';

exports.fnGetView = function(req, res, next){
  /*1. 检查用户是否登录, 如果用户已经登录则直接进入页面*/
  if (req.session 
      && req.session['__sso-session-key__'] 
      && req.session['userdata']) {
    logger.info('fdp-sso-client|用户已经登录');
    /*执行重定向*/
    fnLoginSuccRedirect(req, res);
    return;
  }

  /*2. 如果用户尚未登录，则引导用户去登录*/
  res.redirect(oConf['fdp-sso-server-uri'] + '?system=' + oConf['system-name']);
};

exports.fnGetUser = function(req, res, next){
  var result = req.session && req.session['userdata'];
  result = result || {};
  res.setHeader('Content-Type', 'application/json');
  res.end(JSON.stringify(result));
};

exports.fnDoLogout = function(req, res, next){
  if (req.session) {
    delete req.session.userdata;
    // delete req.session['__sso-session-key__'];
  }
  res.redirect(oConf['fdp-sso-server-uri'] + '/logout?system=' + 
    oConf['system-name'] + '&callbackuri=' + oConf['logout-successfuly-uri']);
};

exports.fnDoLoginVerify = function(req, res, next){
  fnVerifyToken(req, function(err){
    if (err) {
      logger.error('fdp-sso-client|fnDoLoginVerify|' + JSON.stringify(err));
      res.redirect(oConf['mount-path']);
      return;
    }
    /*执行重定向*/
    fnLoginSuccRedirect(req, res);
  });
};

exports.fnSessionAck = function(req, res, next){
  var result = {isSessAsy: !!(req.session && req.session['__sso-session-key__'])};
  res.setHeader('Content-Type', 'text/plain');
  res.end(JSON.stringify(result));
};

exports.fnDoLogoutVerify = function(req, res, next){

};

exports.fnSessionSyc = function(req, res, next){
  fnVerifyToken(req, function(err){
    if (err) {
      logger.error('fdp-sso-client|fnSessionSyc|' + JSON.stringify(err));
      res.redirect(oConf['mount-path']);
      return;
    }
    res.setHeader('Content-Type', 'text/html');
    res.end(sSessAsyHtml);
  });
};

/**
 * 到会话中心验证token，验证成功后记录会话中心的session key并删除token。
 * @author 康永胜
 * @date   2016-09-02T10:10:19+0800
 * @param  {Object}      req        [请求对象]
 * @param  {Function}    fnCallback [回调函数]
 * @return {undefined}              []
 */
function fnVerifyToken(req, fnCallback){
  /*获取token*/
  var sToken = req.query.token || req.body.token;

  /*如果没有获取到token，则直接执行回调*/
  if (!sToken) {
    logger.error('fdp-sso-client|获取token失败');
    fnCallback && fnCallback({message: '获取token失败'});
    return;
  }

  /**
   * 获取token成功后执行以下操作：
   * 1. 从会话中心获取token对应的session key，并存储在本地session中
   * 2. 删除会话中心的token
   */
  redis.get(sToken, function(err, sSessionKey){
    if (!sSessionKey) {
      /*如果没有获取到session key，则抛出异常*/
      logger.error('fdp-sso-client|使用token[' + sToken + ']获取会话中心的session key失败');
      fnCallback && fnCallback(new Error('fdp-sso-client|使用token[' + sToken + ']获取会话中心的session key失败'));
    }
    req.session['__sso-session-key__'] = sSessionKey;
    redis.del(sToken, function(err, reply){
      if (err) {
        logger.error('fdp-sso-client|删除token[' + sToken + ']失败|error: ' + JSON.stringify(err));
        return;
      }
      logger.info('fdp-sso-client|删除token[' + sToken + ']成功|reply: ' + reply);
    });
    fnCallback && fnCallback();
  });
}

/**
 * [完成登录成功后的跳转功能]
 * @author 康永胜
 * @date   2016-09-12T17:50:21+0800
 * @param  {Object}         req [请求对象]
 * @param  {Object}         res [相应对象]
 * @return {undefined}          []
 */
function fnLoginSuccRedirect(req, res){
  var oSession = req.session;
  var sRedirectPath;
  if(!oSession){
    res.redirect(oConf['mount-path']);
    return;
  }

  if (oSession[sIntendedPathKey]) {
    sRedirectPath = oSession[sIntendedPathKey];
    delete oSession[sIntendedPathKey];
    oSession.save && oSession.save(function(err){
      if (err) {
        res.redirect(oConf['login-successfuly-uri']);
      }
      res.redirect(sRedirectPath);
    });
  }else{
    res.redirect(oConf['login-successfuly-uri']);
  }
}
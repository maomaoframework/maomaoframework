var uuid = require('uuid');
var redisProxy = require('./data-proxy');
var EventProxy = require('eventproxy');
var fdpUtils = require('fdp-utils');
var logger = require('fdp-logger');
var url = require('url');
var oConf= fdpUtils.getJsonProp('/fdp-config.js', 'fdp-sso-server');
fnPreproccessConf(oConf);/*对配置中的信息进行预处理*/

/**
 * 获取登录页面，如果已经登录，则直接跳转到指定路径并颁发token
 * @author 康永胜
 * @date   2016-09-01T09:15:28+0800
 * @param  {Object}           req  [请求对象]
 * @param  {Object}           res  [响应对象]
 * @param  {Function}         next [执行下一个中间件的方法]
 * @return {undefined}             []
 */
exports.fnGetLoginPage = function(req, res, next){
  /*1. 获取重定向地址*/
  var sRedirectUrl = fnGetRedirectUrl(req);

  /*2. 如果用户已经登录，则直接执行回调*/
  if (req.session && req.session.userdata) {
    sRedirectUrl = fnDoCallback(req, res, sRedirectUrl);
    res.redirect(sRedirectUrl);
    return;
  }

  /*3. 如果用户没有登录， 则进行登录*/
  res.render(oConf['login-view-path'], {system: fnGetSystemName(req)});
};

/**
 * 处理登录请求，根据配置跳转页面并颁发token
 * @author 康永胜
 * @date   2016-09-01T09:17:39+0800
 * @param  {Object}           req  [请求对象]
 * @param  {Object}           res  [响应对象]
 * @param  {Function}         next [执行下一个中间件的方法]
 * @return {undefined}             []
 */
exports.fnDoLogin = function(req, res, next){
  /*1. 获取重定向地址*/
  var sRedirectUrl = fnGetRedirectUrl(req);

  /*2. 如果用户已经登录，则直接跳转*/
  if(req.session && req.session.userdata){
    res.redirect(sRedirectUrl);
    return;
  }

  var ep = new EventProxy();
  var options = {
    path : '/servlet/loginServlet',
    'j_username' : req.body.username,
    'j_password' : req.body.password,
    complete : function(data) {
      /*如果登陆成功, data就是一个空字符串*/
      if (data) {
        res.render(oConf['login-view-path'], {message: '登陆失败'});
        logger.error('fdp-sso-server|登陆失败|' + JSON.stringify(data));
        return;
      };
      ep.emit('success');
    }
  };
  dataproxy.post(req, res, options, 'text');

  /*登陆成功后获取用户*/
  ep.once('success', function(data) {
    dataproxy.get(req, res, {
      service : 'app.service.commonService',
      method : 'getUser',
      data : {},
      complete : function(data) {
        /*如果data中没有指定信息，则认为登陆失败*/
        if (!data || (!data.YHM && !data.username)) {
          res.render(oConf['login-view-path'], {message: '获取用户信息失败'});
          logger.error('fdp-sso-server|获取用户信息失败|' + JSON.stringify(data));
          return;
        }

        /*对于返回信息验证通过后记录到session中*/
        data.username = data.username || data.YHM;
        data.userid = data.userid || data.USERBM;

        req.session.userdata = data;
        req.session.save(function(){
          sRedirectUrl = fnDoCallback(req, res, sRedirectUrl);
          res.redirect(sRedirectUrl);
        });
      }
    });
  });
};

/**
 * 处理退出登录请求
 * @author 康永胜
 * @date   2016-09-01T09:17:39+0800
 * @param  {Object}           req  [请求对象]
 * @param  {Object}           res  [响应对象]
 * @param  {Function}         next [执行下一个中间件的方法]
 * @return {undefined}             []
 */
exports.fnDoLogout = function(req, res, next){
  /*重定向地址*/
  var sRedirectUrl = fnGetRedirectUrl(req);
  var sCallbackUri = req.query.callbackuri || req.body.callbackuri || '/';
  var options = {
    path : '/servlet/loginOutServlet',
    complete : function(data) {
      delete req.session['userdata'];
      req.session.save(function(){
        res.redirect(url.resolve(sRedirectUrl, sCallbackUri));  
      });
    }
  };
  dataproxy.post(req, res, options, 'text');
};

/**
 * 处理验证token请求
 * @author 康永胜
 * @date   2016-09-01T09:17:39+0800
 * @param  {Object}           req  [请求对象]
 * @param  {Object}           res  [响应对象]
 * @param  {Function}         next [执行下一个中间件的方法]
 * @return {undefined}             []
 */
exports.fnVerifyToken = function(req, res, next){

};

exports.fnSessionSyc = function(req, res, next){
  /*1. 获取重定向地址*/
  var sRedirectUrl = fnGetRedirectUrl(req);
  sRedirectUrl = sRedirectUrl.replace('loginNotice', 'sessionsyc');

  /*2. 执行回调*/
  sRedirectUrl = fnDoCallback(req, res, sRedirectUrl);
  res.redirect(sRedirectUrl);
};

/**
 * [fnDoCallback description]
 * @param  {[type]} sRedirectUrl [description]
 * @return {[type]}              [description]
 */
function fnDoCallback(req, res, sRedirectUrl){
  /*如果是重定向到别的站点则生成token*/
  if (sRedirectUrl.indexOf('http://') == 0 || sRedirectUrl.indexOf('https://') == 0 ) {
    var sToken = uuid.v1();
    var sHyphen = sRedirectUrl.indexOf('?') != -1 ? '&' : '?';
    sRedirectUrl = sRedirectUrl + sHyphen + 'token=' + sToken;
    redisProxy.set(sToken, 'fdp-' + req.sessionID);
    redisProxy.expire(sToken, 60);/*令牌的过期时间，需要配置到文件*/
  }
  return sRedirectUrl;
}

function fnGetRedirectUrl(req){
  var sSystem = fnGetSystemName(req);
  /*如果开启了'use-system-mapping', 则返回system-mapping中配置的值, 否则直接返回system的值*/
  if (oConf['use-system-mapping'] || sSystem == 'self') {
    return oConf['system-mapping'][sSystem];
  }
  return sSystem.replace(/\/$/, '') + '/loginNotice';
}

function fnGetSystemName(req){
  return req.query.system || req.body.system || 'self';
}

/**
 * 对于配置中的信息进行预处理
 * @author 康永胜
 * @date   2016-09-01T11:28:47+0800
 * @param  {[type]}                 oConf [description]
 * @return {[type]}                       [description]
 */
function fnPreproccessConf(oConf){
  var oSysMap = oConf['system-mapping'];

  for (var key in oSysMap) {
    if (key === 'self') {
      continue;
    }
    oSysMap[key] = oSysMap[key].replace(/\/$/, '') + '/loginNotice';
  }
}
var redis = require('./lib/data-proxy');
var fdpUtils = require('fdp-utils');
var oConf= fdpUtils.getJsonProp('/fdp-config.js', 'fdp-sso-client');

module.exports = function(oApp){
  /*1. 添加路由*/
  var sMountPath = oConf['mount-path'] || '/login';
  oApp.use(sMountPath, require('./lib/routes'));
  /*2. 添加中间件函数*/
  oApp.use(fnSSOMiddleWare);
}

/**
 * 处理单点登录的中间件
 * @author 康永胜
 * @date   2016-09-01T09:43:20+0800
 * @param  {Object}           req  [请求对象]
 * @param  {Object}           res  [响应对象]
 * @param  {Function}         next [执行下一个中间件的方法]
 * @return {undefined}             []
 */
function fnSSOMiddleWare(req, res, next){
  /*会话中心的session key*/
  var sSSOSessionKey = req.session['__sso-session-key__'];

  /*如果没有保存会话中心的session key，则删除当前session存储的用户信息*/
  if(!sSSOSessionKey){
    delete req.session['userdata'];
    next();
    return;
  }

  /*如果当前session保留有会话中心的session key，则通过redis获取会话用户信息*/
  redis.get(sSSOSessionKey, function(err, reply){
    /*会话中心的用户数据*/
    var oSsoSessiondata = JSON.parse(reply);
    /*本地session的用户数据*/
    var oLocalUserData;

    /* 
     * 如果发生错误或没有获取到session信息，
     * 则删除当前session中的session key信息和用户信息
    */
    if (err || !oSsoSessiondata) {
      delete req.session['__sso-session-key__'];
      delete req.session['userdata'];
      next(err);
      return;
    }
    if (!oSsoSessiondata['userdata']) {
      delete req.session['userdata'];
      next(err);
      return;
    }

    /*
     * 如果从会话中心获取信息成功
     * 1. 如果本地没有用户数据，则将获取到的用户数据存入本地session
     * 2. 如果本地存在用户数据，则将获取的数据和本地的数据进行对比
    */
    oSsoSessiondata = oSsoSessiondata && oSsoSessiondata['userdata']
    oLocalUserData = req.session['userdata'];
    
    if (!oLocalUserData || (oLocalUserData.userid != oSsoSessiondata.userid)) {
      req.session['userdata'] = oSsoSessiondata;
    }
    next();
  });
}
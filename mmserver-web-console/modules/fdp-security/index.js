var minimatch = require("minimatch");
var fdpUtils = require('fdp-utils');
var logger = require('fdp-logger');
var RoleListManager = require('./lib/RoleListManager'); /*角色列表管理器*/
var roleListManager = null;
var securityArray = null;/*配置信息*/
var sNotLoginUri = null; /*没有登录时的跳转链接*/
var sPermissionDeniedUri = null; /*权限不足时的跳转链接*/
var sPathPatternIsRoot = false; /*判断path-pattern的值是否是root*/

/**
 * 负责进行权限过滤的中间件工厂方法
 * @author 康永胜
 * @date   2016-08-31T11:48:58+0800
 * @param  {String}       file     [配置信息所在的文件]
 * @param  {String}       keysList [规则列表所在的属性路径]
 * @return {Function}              [进行权限过滤的中间件]
 */
module.exports = function(file, keysList){
  if(!securityArray){
    file = file || '/fdp-config.js';
    keysList = keysList || 'fdp-security';

    securityArray = fdpUtils.getJsonProp(file, keysList + '.validate-rules') || [];
    sNotLoginUri = fdpUtils.getJsonProp(file, keysList + '.not-login-uri') || '/login';
    sPermissionDeniedUri = fdpUtils.getJsonProp(file, keysList + '.permission-denied-uri') || '/';

    if(fdpUtils.getJsonProp(file, keysList + '.path-pattern') == 'root'){
      sPathPatternIsRoot = true;
    }
    /*角色管理器*/
    roleListManager = new RoleListManager(securityArray);
  }

  return function(req, res, next){
    var path;
    var userdata = req.session && req.session.userdata;
    var roleList = null;/*角色列表*/

    /*根据配置获取所要使用的路径*/
    if(sPathPatternIsRoot){
      path = req.baseUrl + req.path; /*全路径*/
    }else{
      path = req.path; /*以挂载点为根的路径*/
    }

    if(userdata){
      roleList = userdata.roleList || [];
      /*每个用户默认拥有一个default角色*/
      if(roleList[0] != 'default'){
        roleList.unshift('default');
      }
    }

    /*判断用户是否有权访问当前路径*/
    var isAuthed = roleListManager.isAuthed(path, roleList);

    /*如果有权访问当前路径，则进入下一个中间件*/
    if(isAuthed){
      next();
      return;
    }

    /*将刚才应用想要访问的路径记录下来，以便登录成功后恢复路径*/
    if(req && req.session){
      req.session['__intended-path-when-kick-out-by-fdp-security__'] = req.originalUrl;
    }

    /*
     *无权访问当前路径，则重定向到配置页面
     *如果已经登录，则跳转到sPermissionDeniedUri指定的路径
     *如果尚未登录，则跳转到sNotLoginUri指定的路径
    */
    if (userdata) {
      res.redirect(sPermissionDeniedUri);
    }else{
      res.redirect(sNotLoginUri);
    }
  };
}
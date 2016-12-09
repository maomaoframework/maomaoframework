var fdpUtils = require('fdp-utils');
var logger = require('fdp-logger');
var DataHolder = require('./DataHolder');
var minimatch = require("minimatch");
var util = require("util");
var oPatternDataHolder = new DataHolder();
var pathDataHolder = new DataHolder();
var PatternList = [];/*规则列表*/

/**
 * [角色列表管理器的构造器]
 * @author 康永胜
 * @date   2016-08-31T12:02:37+0800
 * @param  {Array}         aSecurityRules [权限规则数组]
 * @return {Object}                       [新构建的角色列表对象]
 */
module.exports = function(aSecurityRules){
  if (!util.isArray(aSecurityRules)) {
    logger.error('获取权限配置信息错误。');
    throw new Error('获取权限配置信息错误。');
  };

  var len = aSecurityRules.length;
  var tmpMap = null;
  var key;
  var value;

  /*遍历每一条配置*/
  for (var i = 0; i < len; i++) {
    tmpMap = aSecurityRules[i];

    /*遍历每一个匹配模式*/
    for (var j = 0; j < tmpMap['patternList'].length; j++) {
      key = tmpMap['patternList'][j]
      value = '';

      /*遍历所有的角色列表*/
      for (var h = 0; h < tmpMap['roleList'].length; h++) {
        value += ('-->' + tmpMap['roleList'][h]);
      };
      oPatternDataHolder.set(key, value);
    };
  };

  this.isAuthed = function(path, roleList){
    roleList = roleList || [];

    /*如果没有配置规则信息，则直接返回true*/
    if(!aSecurityRules.length){
      return true;
    }

    var requiredRoleList = pathDataHolder.get(path);
    var result = false;
    if (!requiredRoleList) {
      requiredRoleList = fnBuildMapOfPathAndRoleList(path, oPatternDataHolder);
      pathDataHolder.set(path, requiredRoleList);
    }
    
    /*如果权限列表中有no，直接返回true*/
    if(requiredRoleList.indexOf('-->no') >= 0){
      return true;
    }

    /*开始比对当前用户的角色是否可以访问当前路径*/
    for (var i = 0; i < roleList.length; i++) {
      if(requiredRoleList.indexOf(roleList[i]) >= 0){
        result = true;
        break;
      }
    }

    return result;
  };
};

/**
 * [fnBuildMapOfPathAndRoleList description]
 * @author 康永胜
 * @date   2016-08-31T13:50:52+0800
 * @param  {String}     path                [可访问的系统资源路径]
 * @param  {Object}     oPatternDataHolder  [规则缓存]
 * @return {String}                         [允许访问path的角色列表]
 */
function fnBuildMapOfPathAndRoleList(path, oPatternDataHolder){
  var all = oPatternDataHolder.getAll();
  var roleList = '';

  for (var key in all) {
    if (!all.hasOwnProperty(key)){
      continue;
    }

    if (minimatch(path, key)) {
      /*所有角色叠加*/
      // roleList += all[key]
      
      /*后续规则覆盖前方规则*/
      roleList = all[key]
    };
  };

  return roleList;
}
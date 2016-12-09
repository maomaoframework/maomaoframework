var fdpUtils = require('fdp-utils');
var oConf= fdpUtils.getJsonProp('/fdp-config.js', 'fdp-sso-server');

/**
 * fdp-sso-server的入口方法
 * @author 康永胜
 * @date   2016-09-01T09:10:53+0800
 * @param  {Object}         oApp [应用句柄]
 * @return {undefined}           []
 */
module.exports = function(oApp){
  /*将中间件挂载到对应路径*/
  var sMountPath = oConf['mount-path'] || '/login';
  oApp.use(sMountPath, require('./lib/routes'))
};
var fdpUtils = require('fdp-utils');
var logger = require('fdp-logger');
var Source = require('./Source');
var SourceRouter = require('./SourceRouter');
/*所有的数据源*/
var oSources = {};

/**
 * [SourceManager的构造函数]
 * @author 康永胜
 * @date   2016-09-09T14:24:34+0800
 * @return {Object}                 [新创建的对象]
 */
module.exports = function(){
  /*配置信息*/
  var oConf = fdpUtils.getJsonProp('/fdp-config.js','fdp-data-proxy');
  if (!oConf) {
    logger.error('fdp-data-proxy|获取配置失败');
    throw new Error('fdp-data-proxy|获取配置失败');
  }
  var oDataSourcesConf = oConf['data-sources'];

  /*数据源路由对象*/
  var oRouter = new SourceRouter(oDataSourcesConf);


  /*根据参数获取一个可用的服务器*/
  this.getServerByParam = function(oParam){
    /*1. 根据参数从服务路由器中获取数据源名称*/
    var sSourceName = oRouter.getSourceName(oParam);
    /*2. 根据数据源名称获取或新建可用数据源*/
    var oSource = oSources[sSourceName];
    if(!oSource){
      oDataSourcesConf[sSourceName]['name'] = sSourceName;
      oSource = oSources[sSourceName] = fnInitSource(oDataSourcesConf[sSourceName], oConf['http-agent']);
    }

    /*3.从数据源中获取可用的服务器并返回*/
    if (!oSource) {
      logger.error('fdp-data-proxy|获取或创建数据源[' + sSourceName + ']失败');
      throw new Error('fdp-data-proxy|获取或创建数据源[' + sSourceName + ']失败');
    }

    return oSource.getServerManager().getAliveServer();
  };
};

/**
 * 初始化一个数据源
 * @author 康永胜
 * @date   2016-09-06T17:15:20+0800
 * @param  {Object}       oConf [description]
 * @return {Object}             [数据源对象]
 */
function fnInitSource(oConf, oAgentConf){
  var sName = oConf['name'];

  if (oSources[sName]) {
    return oSources[sName];
  }

 return Source.createSource(oConf, oAgentConf);
}


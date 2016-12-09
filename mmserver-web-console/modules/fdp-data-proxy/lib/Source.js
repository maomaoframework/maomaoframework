var urlParser = require('url');
var logger = require('fdp-logger');
var ServerManager = require('./ServerManager');
var ParamMapper = require('./ParamMapper');

/**
 * [构造数据源对象的工厂函数]
 * @author 康永胜
 * @date   2016-09-05T21:18:42+0800
 * @param  {Object}    oBaseConf  [数据源的基础配置描述对象, 配置位于fdp-config.js与fdp-config-local.js中]
 * @param  {Object}    oAgentConf [TCP连接池的配置信息, 配置位于fdp-config.js与fdp-config-local.js中]
 * @return {Object}               [新构建的数据源对象]
 */
exports.createSource = function(oBaseConf, oAgentConf){
  /*1. 创建数据源对象*/
  var oSource = new ConCreateSource(oBaseConf, oAgentConf);
  /*
   * 2. 
   * 为数据源创建ServerManager对象, 
   * 因为ServerManager的创建需要依赖数据源中的信息, 
   * 所以必须首先创建数据源，再创建ServerManager。
  */
  var oServerManager = new ServerManager(oSource);

  /*3. 将ServerManager设置给数据源对象*/
  oSource.setServerManager(oServerManager);

  /*4. 返回创建好的数据源对象*/
  return oSource;
};

/**
 * [数据源对象的构造函数]
 * @author 康永胜
 * @date   2016-09-07T11:15:18+0800
 * @param  {Object}    oBaseConf  [数据源的基础配置描述对象, 配置位于fdp-config.js与fdp-config-local.js中]
 * @param  {Object}    oAgentConf [TCP连接池的配置信息, 配置位于fdp-config.js与fdp-config-local.js中]
 * @return {Object}               [新构建的数据源对象]
 */
function ConCreateSource(oBaseConf, oAgentConf){
  /*1. 基础配置属性*/
  var sName = oBaseConf['name'] || 'default';
  var sDescription = oBaseConf['description'] || '无描述';
  var aUrls = oBaseConf['urls'] || [];
  var sDefaultPaht = oBaseConf['default-path'] || '/';
  var sDefaultContentType = oBaseConf['default-content-type'] || 'application/x-www-form-urlencoded';
  var sRouteRuleParamName = oBaseConf['route-rule-param-name'] || 'service';
  var sRouteRules = oBaseConf['route-rules'] || ['**'];
  var sOutOfDateStrategy = oBaseConf['out-of-date-strategy'] || 'pure-pulse';
  var iPulseInterval = oBaseConf['pulse-interval'] || 300;
  var sPulsePath = oBaseConf['pulse-path'] || '/';
  var vTimesToDie = oBaseConf['times-to-die'] || false;
  var iTimeout = oBaseConf['timeout'] || 3000;
  var bResendAfterFailing = oBaseConf['resend-after-failing'] || true;
  var iResendTimes = oBaseConf['resend-times'] || 2;
  var sPropsMapper = oBaseConf['props-map'] || [];
  var oHttpAgent = oBaseConf['http-agent'] || oAgentConf || {};

  /*确保默认路径以反斜杠开始*/
  if(sDefaultPaht[0] != '/'){
    sDefaultPaht = '/' + sDefaultPaht;
  }

  /*将秒转换为毫秒*/
  iPulseInterval *= 1000; 

  // if (vTimesToDie === undefined) {
  //   vTimesToDie = 3;
  // }

  /*为连接池赋予默认值*/
  oHttpAgent['keepAlive '] = oHttpAgent['keepAlive '] || true;
  oHttpAgent['keepAliveTimeout'] = oHttpAgent['keepAliveTimeout'] || 600000;
  oHttpAgent['timeout'] = oHttpAgent['timeout'] || 1200000;
  oHttpAgent['maxSockets'] = oHttpAgent['maxSockets'] || 50;
  oHttpAgent['maxFreeSockets'] = oHttpAgent['maxFreeSockets'] || 50;
  

  /*判断失效策略*/
  if(sOutOfDateStrategy !== 'pure-pulse' && sOutOfDateStrategy !== 'incidental'){
    logger.error('数据源[' + sName + ']过期策略[' + sOutOfDateStrategy 
      + ']配置错误, 采用默认策略[pure-pulse]');
    sOutOfDateStrategy = 'pure-pulse';
  }

  /*2. 管理属性*/
  /*服务器管理器, 在工厂方法中初始化*/
  var oServerManager = null;
  /*属性映射器*/
  var oParamMapper = new ParamMapper(sPropsMapper);

  /**
   * 获取数据源名称
   * @author kangys
   * @return {String} 返回数据源名称
   */
  this.getName = function(){
    return sName;
  };

  this.getUrls =function(){
    return aUrls;
  };

  /**
   * 获取默认路径
   * @author kangys
   * @return {String} 数据源的默认路径
   */
  this.getDefaultPath = function(){
    return sDefaultPaht;
  };

  this.getDefaultContentType = function(){
    return sDefaultContentType;
  };
  /**
   * 获取心跳监听的路径
   * @author kangys
   * @return {String} 心跳监听的路径
   */
  this.getPulsePath = function(){
    return sPulsePath;
  };

  this.getPulseInterval = function(){
    return iPulseInterval;
  };

  /**
   * 获取请求的超时时间
   * @author kangys
   * @return {Integer}  请求的超时时间
   */
  this.getTimeout = function(){
    return iTimeout;
  };

  /**
   * 获取node认为数据源失效的、连续产生拒绝服务的次数
   * @return {Integer} node认为数据源失效的、连续产生拒绝服务的次数
   */
  this.fnGetTimesToDie = function(){
    return vTimesToDie;
  };

  this.getServerManager = function(){
    return oServerManager;
  };

  this.setServerManager = function(oSM){
    oServerManager = oSM;
  };

  this.getAgentConf = function(){
    return oHttpAgent;
  };

  this.getParamMapper = function(){
    return oParamMapper;
  };

  this.ifResendAfterFailing = function(){
    return bResendAfterFailing;
  };

  this.getResendTimes = function(){
    return iResendTimes;
  };
}
var logger = require('fdp-logger');
var util = require('util');
var Server = require('./Server');
var Listener = require('./Listener');

/**
 * [ServerManager的构造函数]
 * @author 康永胜
 * @date   2016-09-09T14:22:12+0800
 * @param  {Object}       oSource [当前ServerManager所属的数据源对象]
 * @return {Object}               [新创建的对象]
 */
module.exports = function(oSource){
  var aUrls = oSource.getUrls();
  if(!aUrls || !util.isArray(aUrls)){
    logger.error('接口配置中的urls属性必须是数组。');
    throw new Error('接口配置中的urls属性必须是数组。');
  }

  var aAliveServers = []; /*正常的数据源列表*/
  var aDeadServers = []; /*停止服务的数据源列表*/
  var oListener = new Listener(oSource.getName(), aAliveServers, aDeadServers);

  /*初始化所有服务器*/
  var iLen = aUrls.length;
  for(var i = 0; i < iLen; i++){
    /*创建一个server*/
    aAliveServers.push(Server.createServer(aUrls[i], oSource, i));
  }

  /*开始监听*/
  oListener.start(oSource.getPulseInterval());

  /**
   * [获取一个正常服务的服务器句柄]
   * @author 康永胜
   * @date   2016-09-09T14:23:37+0800
   * @return {Object}                 [一个可正常服务的服务器]
   */
  this.getAliveServer = function(){
    /*
     * 获取数据源，如果获取的数据源不处于活动状态则循环获取, 
     * 直到获取一个正常数据源或者直到aAliveServers数组为空。
    */
    var tmpServer = aAliveServers.shift();
    while(tmpServer && !tmpServer.isAlive()){
      aDeadServers.push(tmpServer);
      logger.error('数据源[' + tmpServer.getName() + '|' + tmpServer.getUrl() + ']已经失效，将其移入失效队列。');
      tmpServer = aAliveServers.shift();
    }

    if(!tmpServer){
      /*如果是空的*/
      logger.error('fdp-data-proxy|数据源[' + oSource.getName() + ']中所有服务器都处于停止服务状态。');
      return null;
    }

    aAliveServers.push(tmpServer);

    return tmpServer;
  };

  this.getDeadServer = function(){
    return aDeadServers.shift() || null;
  };

  this.putToAliveSet = function(oServer){
    if (oServer) {
      aAliveServers.push(oServer);
    }
    return this;
  };

  this.putToDeadSet = function(oServer){
    if (oServer) {
      aDeadServers.push(oServer);
    }
    return this;
  };
};
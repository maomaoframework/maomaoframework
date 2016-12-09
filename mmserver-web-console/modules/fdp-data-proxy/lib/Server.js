var urlParser = require('url');
var logger = require('fdp-logger');
var Agent = require('agentkeepalive');

/**
 * [Server的构造函数]
 * @author 康永胜
 * @date   2016-09-09T14:19:20+0800
 * @param  {String}     sUrl     [Server对应的url值]
 * @param  {Object}     oSource  [Server所属的数据源对象]
 * @param  {String}     sId      [用于在当前数据源范围内标识该服务器的标识]
 * @return {Object}              [新创建的对象]
 */
exports.createServer = function(sUrl, oSource, sId){
  sUrl = (sUrl || '').trim();
  /*去除url末尾的反斜杠*/
  sUrl = sUrl.replace(/\/$/, '');

  if(!sUrl){
    logger.error('fdp-data-proxy|Server.js|server url必须指定');
    throw new Error('fdp-data-proxy|Server.js|server url必须指定');
  }

  /*以数据源对象为原型创建server对象*/
  var oServer = Object.create(oSource);
  /*用于管理server的属性*/
  var bIsAlive = true;
  var iTimesOfDie = 0;
  var oAgent = new Agent(oSource.getAgentConf());
  var iTotalRequests = 0;/*发送到该服务器的请求数*/
  sId = oSource.getName() + '-' + sId; /*当前服务器的id*/ 

  /**
   * 检查数据源是否有效
   * @author kangys
   * @return {Boolean} true代表数据源有效，false代表数据源无效
   */
  oServer.isAlive = function(){
    return bIsAlive;
  };

  /**
   * 获取url，不带默认路径
   * @author kangys
   * @return {Sting} 数据源的url
   */
  oServer.getUrl = function(){
    return sUrl;
  };

  /**
   * 获取解析后的url
   * @author kangys
   * @return {Object} 解析后的url对象
   */
  oServer.getUrlJson = function(){
    return urlParser.parse(sUrl);
  };

  /**
   * 获取默认
   * @author kangys
   * @return {[type]} [description]
   */
  oServer.getDefaultFullUrl = function(){
    return sUrl + this.getDefaultPath();
  };

  /**
   * 让数据源复活
   * @author kangys
   * @return {undefined}
   */
  oServer.live = function(){
    iTimesOfDie = 0;
    bIsAlive = true;
  };

  /**
   * 让数据源失效
   * @author kangys
   * @return {undefined}
   */
  oServer.die = function(){
    if(!this.fnGetTimesToDie()){
      return;
    }

    iTimesOfDie ++;
    if(iTimesOfDie < this.fnGetTimesToDie()){
      return;
    }
    logger.error('数据源[' + this.getName() + '|' + sUrl + ']被置为失效。');
    bIsAlive = false;
  };

  /**
   * [获取当前server的长连接客户端]
   * @author 康永胜
   * @date   2016-09-07T11:58:27+0800
   * @return {Object}                 [长连接池对象]
   */
  oServer.getAgent = function(){
    return oAgent;
  };

  oServer.getId = function(){
    return sId;
  };
  
  /**
   * 获取用于心跳监听的完整url
   * @author kangys
   * @return {String} 返回用于心跳监听的完整url
   */
  oServer.getPulseFullUrl = function(){
    return sUrl + this.getPulsePath();
  };

  oServer.addTotalReqs = function(){
    iTotalRequests++;
  };

  oServer.getTotalReqs = function(){
    return iTotalRequests;
  }
  
  /*返回新创建的对象*/
  return oServer;
};
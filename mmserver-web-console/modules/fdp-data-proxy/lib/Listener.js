var oClientCookie = require('fdp-client-cookie');
var EventProxy = require('eventproxy');
var http = require('http');
var logger = require('fdp-logger');
var fdpUtils = require('fdp-utils');
var request = require('request');
var oCookies = {};/*服务器端要求设置的cookie*/

module.exports = function(sProxyName, aAiveSources, aDieSources){
  this.start = function(iInterval){
    iInterval = iInterval || 60000;

    /*开始定时监听*/
    setInterval(function(){
      fnDoListen(sProxyName, aAiveSources, aDieSources);
    }, iInterval);

    /*程序启动后先进行一次监听*/
    fnDoListen(sProxyName, aAiveSources, aDieSources);
  }
};

function fnDoListen(sProxyName, aAiveSources, aDieSources){
  var iDieLen = aDieSources.length;
  var iAliveLen = aAiveSources.length;

  logger.info('数据代理[' + sProxyName + ']开始新一轮脉搏诊断，需要确认[' + (iDieLen + iAliveLen) + 
    ']个数据源|有效队列数据源[' + iAliveLen + ']|失效队列数据源[' + iDieLen + ']');

  /*检测失效的数据源*/
  fnCheckDie(aAiveSources, aDieSources, function(){
    /*检测正在使用中的数据源*/
    fnCheckAlive(aAiveSources, aDieSources);
  });
}

/**
 * 测试数据源的心跳
 * @param  {Object}   server   数据源对象
 * @param  {Function} callback 侦听结构和的回调
 * @return {undefined}
 */
function fnFeelPulse(server, callback){
  var sId = server.getId();
  var sUrl = server.getUrl();

  logger.info('诊断[' + sId + '][' + sUrl + ']开始');

  var sFullurl = server.getPulseFullUrl();
  var oOptions = {
    url: sFullurl,
    headers: {},
    timeout: server.getTimeout(),
    agent: server.getAgent()
  };
  /*如果存在cookie, 则设置cookie*/
  var sServerSideCookie;
  if (oCookies[sId]) {
    /*暂时：此处还需要对路径等做进一步的解析*/
    sServerSideCookie = oClientCookie.fnGetCookies(oCookies[sId]);
    logger.info('fdp-data-proxy|Lisenter|为服务器[' + sId + ']设置cookie[' + sServerSideCookie +']');
    oOptions.headers['Cookie'] = sServerSideCookie;
  }
  var oReq2Server = request.get(oOptions);
  /*监听response事件*/
  oReq2Server.on('response', function(res){
    logger.info('诊断[' + sId + '][' + sUrl + ']成功');
    /*记录session*/
    if(res.headers['set-cookie']){
      /*暂时：此处还需要对cookie做进一步的解析*/
      logger.info('fdp-data-proxy|Lisenter|服务器[' + sId + ']要求设置cookie: ' + res.headers['set-cookie']);
      oCookies[sId] = oCookies[sId]|| {};
      /*存储cookie*/
      for (var i = 0; i < res.headers['set-cookie'].length; i++) {
        oClientCookie.fnSetCookie(oCookies[sId], res.headers['set-cookie'][i]);
      };
    }
    
    if (!server.isAlive()) {
      server.live();
    }
    callback && callback(server, true);
  });
  /*监听error事件*/
  oReq2Server.on('error', function(res){
    logger.error('诊断[' + sId + '][' + sUrl + ']失败。');
    if (server.isAlive()) {
      server.die();
    }
    callback && callback(server, false);
  });
}

/**
 * 检查失效队列中的数据源是否已经恢复
 * @param  {Array}     aAiveSources 正常使用的数据源列表
 * @param  {Array}     aDieSources  已经失效的数据源列表
 * @param  {Function}  fnCallback   执行完监听之后的回调
 * @return {undefined}
 */
function fnCheckDie(aAiveSources, aDieSources, fnCallback){
  var ep = new EventProxy();
  var iDieLen = aDieSources.length;

  ep.after('done', iDieLen, function (list) {
    var len = list.length;
    for (var i = 0; i < len; i++) {
      if (list[i].isAlive()) {
        aAiveSources.push(list[i]);
        logger.info('数据源['+ list[i].getName() + '|' + list[i].getUrl() + ']已恢复，将其移入正常队列。');
        continue;
      };
      aDieSources.push(list[i]);
    };
    fnCallback && fnCallback();
  });

  for (var i = 0; i < iDieLen; i++) {
    fnFeelPulse(aDieSources.shift(), function(source, result){
      if (result) {
        source.live();
      };
      ep.emit('done', source);
    });
  };
}

/**
 * 检查正常队列中的数据源是否已经恢复
 * @param  {Array}     aAiveSources 正常使用的数据源列表
 * @param  {Array}     aDieSources  已经失效的数据源列表
 * @param  {Function}  fnCallback   执行完监听之后的回调
 * @return {undefined}
 */
function fnCheckAlive(aAiveSources, aDieSources, fnCallback){
  var ep = new EventProxy();
  var iAliveLen = aAiveSources.length;
  var oCurrentSource; /*当前操作的数据源*/

  ep.after('done', iAliveLen, function () {
    iAliveLen = aAiveSources.length;

    for (var i = 0; i < iAliveLen; i++) {
      oCurrentSource = aAiveSources.shift();

      if(oCurrentSource.isAlive()){
        aAiveSources.push(oCurrentSource);
      }else{
        aDieSources.push(oCurrentSource);
        logger.error('数据源[' + oCurrentSource.getName() + '|' + oCurrentSource.getUrl() + ']已经失效，将其移入失效队列。');
      }
    }

    fnCallback && fnCallback();
  });

  /*对每个数据源执行一次监听*/
  for (var i = 0; i < iAliveLen; i++) {
    oCurrentSource = aAiveSources.shift();
    logger.warn('fdp-data-proxy|服务器[' + oCurrentSource.getId() + 
      ']的连接池状态: ' + JSON.stringify(oCurrentSource.getAgent().getCurrentStatus()) + '');
    fnFeelPulse(oCurrentSource, function(source, result){
      if (!result && source.fnGetTimesToDie()) {
        source.die();
        fnListenSourceForTimes(source, source.fnGetTimesToDie() - 1, function(){
          ep.emit('done');
        });
      }else{
        source.live();
        ep.emit('done');
      }
    });
    aAiveSources.push(oCurrentSource);
  }
}

/**
 * [fnListenSourceForTimes description]
 * @param  {[type]} oSource    [description]
 * @param  {[type]} iTimes     [description]
 * @param  {[type]} fnCallback [description]
 * @return {undefined}
 */
function fnListenSourceForTimes(oSource, iTimes, fnCallback){
  var ep = new EventProxy();

  ep.on('done', function(list){
    fnFeelPulse(oSource, fnCheckDone);
  });

  fnFeelPulse(oSource, fnCheckDone);

  function fnCheckDone(source, result){
    iTimes --;

    if(result){
      source.live();
    }else{
      source.die();
    }

    if (iTimes > 0) {
      ep.emit('done');
    }else{
      fnCallback && fnCallback();
    }
  }
}
var request = require('request');
var util = require('util');
var uuid = require('uuid');
var logger = require('fdp-logger');
var oClientCookie = require('fdp-client-cookie');
var fdpCode = require('fdp-result-code');
var mime = require('mime');
var fs = require('fs');
var qs = require('querystring');
var iconv = require('iconv-lite');
var fdpUtils = require('fdp-utils');

/**
 * [向服务器发送请求]
 * @author 康永胜
 * @date   2016-09-08T16:22:41+0800
 * @param  {Object}     req      [原始请求对象]
 * @param  {Object}     res      [原始响应对象]
 * @param  {Object}     oParam   [需要传递给后台的参数对象]
 * @param  {Object}     oConf    [用于发送请求和处理响应的配置对象]
 * @return {undefined}           []
 */
exports.send = function(req, res, oParam, oConf){
  /*1. 定义变量*/
  var server = oConf.server;/*请求的目标服务器*/
  var sServerId = server.getId();
  var bHasBody; /*记录请求是否包含body*/ 
  var vParam; /*记录发送到服务器的参数*/
  var sUri; /*请求的目标uri*/
  var oOptions = { /*发送请求时的选项*/
    timeout: server.getTimeout(),
    headers: {},
    agent: server.getAgent()
  };
  var sContentType = oParam.contentType || server.getDefaultContentType();
  var iStartTime = 0;/*请求开始时间*/
  var oReq2Server = null; /*发送到server端的请求*/
  /*服务器端的session key*/
  var sSessionName = 'server-side-cookie-' + server.getId();
  var oParamMapper = server.getParamMapper();/*参数映射管理器*/
  var oSession = (req && req.session) || {};

  /*2. 准备请求参数, 完善配置信息*/
  /*给请求一个唯一标识*/
  oConf['__id__'] = oConf['__id__'] || uuid.v1();
  oConf['method'] = oConf['method'] || 'post';

  /*将服务端的cookie回传*/
  var sServerSideCookie;
  if(oSession[sSessionName]){
    /*暂时：此处还需要对路径等做进一步的解析*/
    sServerSideCookie = oClientCookie.fnGetCookies(req.session[sSessionName]);
    logger.info(oConf['__id__'] + '|为服务器[' + sServerId + ']设置cookie[' + sServerSideCookie +']');
    oOptions.headers['Cookie'] = sServerSideCookie;
  }
  /*为无状态应用传递用户编码和token*/
  if (oSession.userdata) {
    oParam.statelessuserid = oSession.userdata.userid;
    oParam.statelessusername = oSession.userdata.username;
    oParam.statelesstoken = '74d7d636-bfc4-4659-ae59-5763c13b965f';
  }

  /*拼接uri*/
  sUri = oConf.path ? (server.getUrl() + oConf.path) : server.getDefaultFullUrl();

  /*对参数中的属性名称进行映射转换*/
  oParamMapper.transName(oParam);

  /*对将要发送的数据进行编码*/
  if(oConf.formData){
    oOptions.formData = oParam;
  }else{
    vParam = oParamMapper.encodeByContentType(oParam, sContentType);
    bHasBody = oConf.method == 'post' || oConf.method == 'put';
    if(!bHasBody && vParam){
        sUri += ('?' + vParam);
    }
    oOptions.headers['Content-Type'] = sContentType + '; charset=UTF-8';
  }

  /*将uri赋予请求配置对象*/
  oOptions.url = sUri;

  logger.info('request|' + oConf.method + '|param: ' + vParam 
            + '|headers:' + JSON.stringify(oOptions.headers));

  /*记录开始时间*/
  iStartTime = Date.now();

  /*3. 创建请求对象并发送请求*/
  oReq2Server = request[oConf.method](oOptions, function(err){
    /*如果没有发生错误则直接返回*/
    if(!err){
      return;
    }
    logger.error(oConf['__id__'] + '|请求错误|' + err + '|' + vParam + 
                 '|headers: ' + JSON.stringify(oOptions.headers) + 
                 '|耗时[' + (Date.now() - iStartTime) + 'ms]');
    /*终止请求*/
    oReq2Server.abort();
    /*通知server，其失效了一次*/
    server.die();
    /*如果配置了请求重发，则再次发送请求*/
    if (server.ifResendAfterFailing()) {
      oConf.resendTimes = oConf.resendTimes || server.getResendTimes();
      oConf.resendedTimes = oConf.resendedTimes || 0;
      if (oConf.resendedTimes < oConf.resendTimes) {
        oConf.resendedTimes ++;
        logger.info('fdp-data-proxy|第[' + oConf.resendedTimes + 
          ']次向服务器[' + sServerId + ']重发请求[' + oConf['__id__'] + ']');
        oConf.oEventProxy.emit('fail', err);
        return;
      }
    }
    /*如果配置了请求不重发，则直接执行回调*/
    oConf.complete && oConf.complete(err);
  });

  /*4. 处理响应*/
  oReq2Server.on('response', function(oResFromServer){
    logger.info(oConf['__id__'] + '|数据代理[' + sServerId + ']响应|statusCode[' + oResFromServer.statusCode + 
      ']|耗时[' + (Date.now() - iStartTime) + 'ms]');

    /*标记数据源是有效的*/
    server.live();

    /*记录session*/
    if(oResFromServer.headers['set-cookie']){
      /*暂时：此处还需要对cookie做进一步的解析*/
      logger.info(oConf['__id__'] + '|服务器[' + sServerId + ']要求设置cookie: ' + oResFromServer.headers['set-cookie']);
      /*存储cookie*/
      if(req && req.session){
        req.session[sSessionName] = req.session[sSessionName] || {};
        for (var i = 0; i < oResFromServer.headers['set-cookie'].length; i++) {
          oClientCookie.fnSetCookie(req.session[sSessionName], oResFromServer.headers['set-cookie'][i]);
        };
      }
    }

    /*解析请求*/
    fnParseResponse(res, oResFromServer, oConf);

    /*记录请求完成日志*/
    oResFromServer.on('end', function(){
      logger.info(oConf['__id__'] + '|从服务器[' + sServerId + ']请求完成|耗时[' + (Date.now() - iStartTime) + 'ms]');
    });

    /*发生错误时执行回调*/
    oResFromServer.on('error', function(err){
      logger.info(oConf['__id__'] + '|服务器[' + sServerId + ']响应过程错误|' + err);
      oConf.complete && oConf.complete(err);
    });
  });
  
  oReq2Server.on('socket', function(){

  });

  oReq2Server.on('connect', function(){

  });

  /*如果有请求体则发送请求体*/
  if(bHasBody){
    vParam && oReq2Server.write(vParam);
    oReq2Server.end();
  }
}

/**
 * [根据content-type解析请求]
 * @author 康永胜
 * @date   2016-09-08T16:26:25+0800
 * @param  {Object}     res2Client     [即将返回给客户端的响应对象]
 * @param  {Object}     resFromServer  [从服务器端来的响应对象]
 * @param  {Object}     oConf          [向服务器发送请求时传递的配置对象]
 * @return {undefined}                 []
 */
function fnParseResponse(res2Client, resFromServer, oConf){
  /*如果响应没有头信息，则告诉调用者异常发生*/
  if (!resFromServer.headers) {
    logger.error('fdp-data-proxy|没有header，无法完成数据解析');
    oConf.complete && oConf.complete({
      'RtnCode': fdpCode['PARAMS_ERROR']['code'], 
      'RtnMesage': '没有header，无法完成数据解析'
    });
    return;
  }

  /*获取content的值*/
  var sContentType =  resFromServer.headers['content-type'] || resFromServer.headers['Content-Type'];
  // var sCharset = 'utf-8';
  if (sContentType) {
    res2Client.setHeader('Content-Type', sContentType);
  } else {
    sContentType = 'none';
    logger.error('fdp-data-proxy|没有content-type');
  }

  var aContentType = sContentType.split(';');
  var oHeaders = {
    'content-type': (aContentType[0] || 'application/json').trim(),
    'charset': (aContentType[1] || '').trim().split('=')[1]
  };
 
  /*根据响应头处理响应*/
  switch(oHeaders['content-type']){
    case 'application/json':
    case 'text/json':
      fnParseJson(resFromServer, oConf.complete, oHeaders);
      break;
    case 'text/html':
    case 'application/xhtml+xml':
    case 'text/xml':
      fnParseHtml(resFromServer, oConf.complete, oHeaders);
      break;
    case 'text/plain':
    case 'text/javascript':
      fnParseText(resFromServer, oConf.complete, oHeaders);
      break;
    case 'application/x-www-form-urlencoded':
      fnParseFormUrlEncoded(resFromServer, oConf.complete, oHeaders);
      break;
    case 'multipart/form-data':
      fnParseMultipart(resFromServer, oConf.complete, oHeaders);
      break;
    case 'none':
      fnParseRaw(resFromServer, oConf.complete, oHeaders);
      break;
    default :
      fnParseFileStream(resFromServer, oConf, oHeaders);
  }
}

/**
 * [解析json格式的数据]
 * @author 康永胜
 * @date   2016-09-09T14:03:33+0800
 * @param  {Object}       res        [服务器响应对象]
 * @param  {Function}     fnCallback [函数调用时候的complete回调函数]
 * @param  {Object}       oHeaders   [主要是content type和charset]
 * @return {undefined}               []
 */
function fnParseJson(res, fnCallback, oHeaders){
  fnReceiveTextLikeRes(res, function(sStr){
    var result;
    /*解码*/
    try{
      result = JSON.parse(sStr);
    }catch(e){
      logger.error('响应结果转码错误: ' + err + '|' + sStr);
      result = {
        'RtnCode': fdpCode['FORMAT_PARSE_ERROR']['code'], 
        'RtnMesage': fdpCode['FORMAT_PARSE_ERROR']['message']
      };
    }
    /*执行回调*/
    fnCallback && fnCallback(result);
  }, oHeaders);
}

/**
 * [解析html/xhtml/xml格式的数据]
 * @author 康永胜
 * @date   2016-09-09T14:03:33+0800
 * @param  {Object}       res        [服务器响应对象]
 * @param  {Function}     fnCallback [函数调用时候的complete回调函数]
 * @param  {Object}       oHeaders   [主要是content type和charset]
 * @return {undefined}               []
 */
function fnParseHtml(res, fnCallback, oHeaders){
  fnReceiveTextLikeRes(res, fnCallback, oHeaders);
}

/**
 * [解析普通文本格式的数据]
 * @author 康永胜
 * @date   2016-09-09T14:03:33+0800
 * @param  {Object}       res        [服务器响应对象]
 * @param  {Function}     fnCallback [函数调用时候的complete回调函数]
 * @param  {Object}       oHeaders   [主要是content type和charset]
 * @return {undefined}               []
 */
function fnParseText(res, fnCallback, oHeaders){
  fnReceiveTextLikeRes(res, fnCallback, oHeaders);
}

/**
 * [解析原始数据格式的数据]
 * @author 康永胜
 * @date   2016-09-09T14:03:33+0800
 * @param  {Object}       res        [服务器响应对象]
 * @param  {Function}     fnCallback [函数调用时候的complete回调函数]
 * @param  {Object}       oHeaders   [主要是content type和charset]
 * @return {undefined}               []
 */
function fnParseRaw(res, fnCallback, oHeaders){
  fnReceiveRawRes(res, fnCallback, oHeaders);
}

/**
 * [解析'x-www-form-urlencoded'格式的数据]
 * @author 康永胜
 * @date   2016-09-09T14:03:33+0800
 * @param  {Object}       res        [服务器响应对象]
 * @param  {Function}     fnCallback [函数调用时候的complete回调函数]
 * @param  {Object}       oHeaders   [主要是content type和charset]
 * @return {undefined}               []
 */
function fnParseFormUrlEncoded(res, fnCallback, oHeaders){
  fnReceiveTextLikeRes(res, function(sStr){
    /*解码*/
    var result;
    try{
      result = qs.parse(sStr);
    }catch(e){
      logger.error('响应结果转码错误: ' + err + '|' + sStr);
      result = {
        'RtnCode': fdpCode['FORMAT_PARSE_ERROR']['code'], 
        'RtnMesage': fdpCode['FORMAT_PARSE_ERROR']['message']
      };
    }

    /*执行回调*/
    fnCallback && fnCallback(result);
  }, oHeaders);
}

/**
 * [解析文件传输的响应数据]
 * @author 康永胜
 * @date   2016-09-09T14:03:33+0800
 * @param  {Object}       res        [服务器响应对象]
 * @param  {Object}       oConf      [原始的配置对象]
 * @param  {Object}       oHeaders   [主要是content type和charset]
 * @return {undefined}               []
 */
function fnParseFileStream(res, oConf, oHeaders){
  var stream = oConf.stream;
  var filename;/*如果没有stream的时候系统产生的文件名*/
  if (stream && stream.setHeader) {
    for(var headKey in res.headers){
      if (!res.headers.hasOwnProperty(headKey))continue;
      if (headKey == 'set-cookie')continue;
      stream.setHeader(headKey, res.headers[headKey]);
    }
  }
  if(!stream){
    if(!fdpUtils.createDir(oConf.directory)){
      oConf.complete && oConf.complete({
        'RtnCode': fdpCode['FILE_SYSTEM_ERROR']['code'], 
        'RtnMesage': '创建目录失败: ' + directory
      });
      return;
    }
    var sContDis = (res.headers && res.headers['content-disposition']) || '';
    filename = (sContDis.match(/filename=(.*)(;|$)/) || [])[1];
    filename = filename || (uuid.v1() + '.' + mime.extension(oHeaders['content-type']));
    filename = path.join(directory, filename);
    stream = fs.createWriteStream(filename);
  }
  res.pipe(stream);

  /*响应*/
  var result = {'RtnCode': '00'};
  /*如果是写到文件中，则以filepath为属性返回给调用方*/
  if (oConf.filepath || filename) {
    result.filepath = oConf.filepath || filename;
  }
  oConf.complete && oConf.complete(result);
}

/**
 * [解析multipart个数的响应数据，暂不支持]
 * @author 康永胜
 * @date   2016-09-09T14:03:33+0800
 * @param  {Object}       res        [服务器响应对象]
 * @param  {Function}     fnCallback [函数调用时候的complete回调函数]
 * @param  {Object}       oHeaders   [主要是content type和charset]
 * @return {undefined}               []
 */
function fnParseMultipart(res, fnCallback, oHeaders){
  /*暂不支持*/
  logger.error('fdp-data-proxy|暂不支持 multipart/form-data 类型的响应');
  fnCallback && fnCallback({
    'RtnCode': fdpCode['FORMAT_PARSE_ERROR']['code'], 
    'RtnMesage': '暂不支持 multipart/form-data 类型的响应'
  });
}

/**
 * [接收响应内容可以被转换为字符串的响应]
 * @author 康永胜
 * @date   2016-09-08T08:37:43+0800
 * @param  {Object}     res         [响应对象]
 * @param  {Function}   fnCallback  [回调函数]
 * @param  {Object}     oHeaders    [解析后的头信息]
 * @return {undefined}              []
 */
function fnReceiveTextLikeRes(res, fnCallback, oHeaders){
    fnReceiveRawRes(res, function(result){
      if (!util.isString(result)) {
        oHeaders.charset = oHeaders.charset || 'UTF-8';
        result = iconv.decode(result, oHeaders.charset);
      }
      /*执行回调*/
      fnCallback && fnCallback(result);
    });
}

/**
 * [接收原始的相应数据，根据数据格式转换为string或者保留为Buffer]
 * @author 康永胜
 * @date   2016-09-09T14:16:00+0800
 * @param  {Object}     res        [服务器端的响应对象]
 * @param  {Function}   fnCallback [请求接收完成后的回调]
 * @return {undefined}             []
 */
function fnReceiveRawRes(res, fnCallback){
  var result = [];
  var size = 0;

  res.on('data', function(chunk){
    result.push(chunk);
    size += chunk.length;
  });

  res.on('end', function(){
    if(size == 0){
      result = '';
    }else{
      /*如果接到的是字符串, 则直接拼接. 如果是buffer, 则进行连接并转码。*/
      if(util.isString(result[0])){
        result = result.join('');
      }else{
        result = Buffer.concat(result, size);
      }
    }

    /*执行回调*/
    fnCallback && fnCallback(result);
  });
}
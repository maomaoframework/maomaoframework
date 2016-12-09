var transmitter = require('./lib/transmitter');
var EventProxy = require('eventproxy');
var logger = require('fdp-logger');
var fdpUtils = require('fdp-utils');
var fdpCode = require('fdp-result-code');
var SourceManager = require('./lib/SourceManager')
var oSourceManager = new SourceManager();

exports.get = function(req, res, oParam){
  fnCommon(req, res, oParam, {
    method: 'get'
  });
};

exports.post = exports.ajax = function(req, res, oParam){
  fnCommon(req, res, oParam, {
    method: 'post'
  });
};

exports.put = function(req, res, oParam){
  fnCommon(req, res, oParam, {
    method: 'put'
  });
};

exports.delete = function(req, res, oParam){
  fnCommon(req, res, oParam, {
    method: 'delete'
  });
};

exports.uploadFile = function(req, res, oParam){
  fnCommon(req, res, oParam, {
    method: 'post',
    formData: true
  })
};

exports.downloadFile = function(req, res, oParam){
  /*配置对象*/
  var oConf = {
    method: 'get'
  };

  /*对该方法中特有的几个参数(filepath, directory, stream)进行处理*/
  /*1. 获取特有参数*/
  var filepath = oParam.filepath;
  var directory = oParam.directory;
  var stream = oParam.stream;

  /*2. 从参数中删除特有属性*/
  delete oParam.filepath;
  delete oParam.directory;
  delete oParam.stream;

  if(!directory && !filepath && !stream){
    logger.error('文件下载错误，directory/filepath/stream三个参数必须指定一个。');
    param.complete && param.complete({
      'RtnCode': fdpCode['PARAMS_ERROR']['code'], 
      'RtnMesage': '文件下载错误，directory/filepath/stream三个参数必须指定一个。'
    });
    return;
  }

  /*3. 往配置对象中添加信息*/
  if(stream){
    oConf.stream = stream
  }else if(filepath){
    if(!fdpUtils.createDir(path.dirname(filepath))){
      param.complete && param.complete({
        'RtnCode': fdpCode['FILE_SYSTEM_ERROR']['code'], 
        'RtnMesage': '创建目录失败: ' + path.dirname(filepath)
      });
      return;
    }
    oConf.filepath = filepath;
    oConf.stream = fs.createWriteStream(filepath);
  }

  /*发送请求*/
  fnCommon(req, res, oParam, oConf);
};

/**
 * [发送请求的共用方法]
 * @author 康永胜
 * @date   2016-09-07T15:35:48+0800
 * @param  {Object}     req           [请求对象]
 * @param  {Object}     res           [响应对象]
 * @param  {Object}     oParam        [调用参数]
 * @param  {[type]}     oConf         [发送请求和接收响应所必须的信息]
 * @return {undefined}                []
 */
function fnCommon(req, res, oParam, oConf){
  /*获取服务器*/
  oConf['server'] = oSourceManager.getServerByParam(oParam);
  oConf.oEventProxy = new EventProxy();

  /*将参数对象oParam中的complete、path属性移动到oConf中*/
  if ('complete' in oParam) {
    oConf.complete = oParam.complete;
    delete oParam.complete;
  }

  if ('path' in oParam) {
    oConf.path = oParam.path;
    delete oParam.path;

    /*保证参数以'/'开始*/
    if(oConf.path && oConf.path[0] != '/'){
      oConf.path = '/' + oConf.path;
    }
  }

  if(!oConf['server']){
    logger.error('fdp-data-proxy|获取数据源失败。');
    oConf.complete && oConf.complete({
      'RtnCode': fdpCode['NONE_DATA_SOURCE_ERROR']['code'], 
      'RtnMesage': fdpCode['NONE_DATA_SOURCE_ERROR']['message']
    });
    return;
  }

  /*发送请求*/
  transmitter.send(req, res, oParam, oConf);
  /*如果失败，则重发*/
  oConf.oEventProxy.on('fail', function(){
    oConf['server'] = oConf['server'].getServerManager().getAliveServer();
    if(!oConf['server']){
      logger.error('fdp-data-proxy|获取数据源失败。');
      oConf.complete && oConf.complete({
        'RtnCode': fdpCode['NONE_DATA_SOURCE_ERROR']['code'], 
        'RtnMesage': fdpCode['NONE_DATA_SOURCE_ERROR']['message']
      });
      return;
    }    
    transmitter.send(req, res, oParam, oConf);
  });
}
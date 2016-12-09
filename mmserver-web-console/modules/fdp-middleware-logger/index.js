var logger = require('morgan');
var jsonData = require('fdp-get-json-properties');
var FileStreamRotator = require('file-stream-rotator')
var path = require('path');
var fs = require('fs');
var mkdirp = require('mkdirp');

module.exports = getLogger();

function getLogger(){
  /*应用根路径*/
  var rootDir = process.env.NODE_FDP_ROOT || __dirname;
  /*服务器运行状态*/
  var appEnv = (process.env.NODE_ENV === 'production') ? 'production' : 'development';
  /*开发模式下的日志格式*/
  var levelDev;
  /*生产模式下的日志格式*/
  var levelPro;
  /*日志存放路径*/
  var logsPath;
  /*在生产模式下是否记录日志*/
  var recodeLogUnderPro = true;
  /*配置文件*/
  var configFile = path.join(rootDir, "fdp-config.js");

  /*判断配置文件是否存在*/
  jsonData = jsonData(configFile);
  if(typeof jsonData == 'object'){
    levelDev = jsonData.getPropertySync('logger.morgan-development');
    levelPro = jsonData.getPropertySync('logger.morgan-production');
    logsPath = jsonData.getPropertySync('logger.logs-path');
    recodeLogUnderPro = jsonData.getPropertySync('logger.morgan-under-production');
  }else{
    console.log(rootDir, '目录下不存在名为fdp-config.js的配置文件，请求日志选项采用默认方式处理。');
  }

  if(appEnv === 'production' && recodeLogUnderPro === false){
    console.log('请求日志记录处于关闭状态。');
    return function(){};
  }
  /*设置缺省状态下的默认日志级别以及存放路径*/
  levelDev = levelDev || 'dev';
  levelPro = levelPro || 'combined';
  logsPath = logsPath || "logs";

  /*根据运行环境选择日志级别*/
  var logLevel = (appEnv === 'development') ? levelDev : levelPro;
  /*确定日志存放位置*/
  var logsRealPath = path.join(rootDir, logsPath);

  /*创建日志输出目录*/
  if(!fs.existsSync(logsRealPath)){
    if(!mkdirp.sync(logsRealPath)){
      logsRealPath = rootDir;
      console.log("创建日志输出目录失败，日志将存储在应用根目录下。");
    }
  }

  /*日志输出目标*/
  var accessLogStream = null;

  if(appEnv === 'development'){
     accessLogStream = process.stdout
  }else{
    accessLogStream = FileStreamRotator.getStream({
      date_format: 'YYYY-MM-DD',
      filename: path.join(logsRealPath, 'request.' + logLevel + '.%DATE%.log'),
      frequency: 'daily',
      verbose: false
    })
  }

  console.log("请求日志级别：%s", logLevel);
  console.log("请求日志文件存放路径：%s", logsRealPath);

  return logger(logLevel, {stream: accessLogStream});
}
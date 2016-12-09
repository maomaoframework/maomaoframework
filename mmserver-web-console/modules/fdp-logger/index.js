var winston = require('winston');
var rotate = require('winston-daily-rotate-file');
var jsonData = require('fdp-get-json-properties');
var path = require('path');
var fs = require('fs');
var mkdirp = require('mkdirp');

/*日志级别映射*/
var logLevels = { error: 0, warn: 1, info: 2, verbose: 3, debug: 4, silly: 5 };
/*应用根路径*/
var rootDir = process.env.NODE_FDP_ROOT || __dirname;
/*服务器运行状态*/
var appEnv = (process.env.NODE_ENV === 'production') ? 'production' : 'development';
/*开发模式下的日志级别*/
var levelDev;
/*生产模式下的日志级别*/
var levelPro;
/*日志存放路径*/
var logsPath;
/*配置文件*/
var configFile = path.join(rootDir, "fdp-config.js");

/*判断配置文件是否存在*/
jsonData = jsonData(configFile);
if(typeof jsonData == 'object'){
  levelDev = jsonData.getPropertySync('fdp-logger.level-development');
  levelPro = jsonData.getPropertySync('fdp-logger.level-production');
  logsPath = jsonData.getPropertySync('fdp-logger.logs-path');
}else{
  console.log(rootDir, '目录下不存在名为fdp-config.js的配置文件，业务日志选项采用默认方式处理。');
}

/*设置缺省状态下的默认日志级别以及存放路径*/
levelDev = (levelDev in logLevels) ? levelDev : 'debug';
levelPro = (levelPro in logLevels) ? levelPro : 'warn';
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
var transports = [
  new (rotate)({/*任何情况下都出现的日志记录*/
    filename: path.join(logsRealPath, 'business.' + logLevel),
    datePattern: 'yyyy-MM-dd',
    level: logLevel
  })
];

/*如果是debug级别，则增加新的日志输出目标*/
if(logLevel === 'debug' || logLevel === 'silly'){
  transports.push(new (winston.transports.Console)({
    level: 'debug'
  }));
}

var logger = new winston.Logger({
  transports: transports
});

console.log("业务日志级别：%s", logLevel);
console.log("业务日志文件存放路径：%s", logsRealPath);

module.exports = logger;
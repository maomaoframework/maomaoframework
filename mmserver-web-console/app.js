/*设置应用根目录*/
process.env.NODE_FDP_ROOT = __dirname;
/*获取应用运行模式*/
var runPattern = process.env.NODE_ENV;
if(runPattern !== 'production'){
  runPattern = 'development';
}

console.log('\b进程[', process.pid, ']运行环境为: ', runPattern);

/*加载应用所需的模块*/
var sConfFile = '/fdp-config.js';
var express = require('express');
var path = require('path');
var logger = require('fdp-logger');
var fdpUtils = require('fdp-utils');
var oMiddlewareSwitch = fdpUtils.getJsonProp(sConfFile,'middleware-switch') || {};

/*应用服务器实例*/
var app = express();

/*模板引擎配置*/
app.set('views', path.join(__dirname, 'views'));
/*开发环境下的静态文件服务配置*/
if(runPattern == 'development'){
  app.set('view cache', false);
}else{
  app.set('view cache', true);
}
app.set('view engine', 'tpl');
app.engine('tpl', require('hbs').__express);

/*站点logo服务*/
if (fnIfUseMiddleware('serve-favicon')) {
  app.use(require('serve-favicon')(path.join(__dirname, 
    fdpUtils.getJsonProp(sConfFile, 'serve-favicon.icon-path'))));
}

/*记录请求日志的中间件*/
if (fnIfUseMiddleware('fdp-middleware-logger')) {
  app.use(require('fdp-middleware-logger'));
}

/*解析请求体的中间件，必须开启*/
var bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: false}));
// app.use(bodyParser.text());//暂时不支持普通文本格式的请求

/*解析cookie的中间件*/
if(fnIfUseMiddleware('cookie-parser')){
  app.use(require('cookie-parser')());
}

/*解析session的中间件*/
if (fnIfUseMiddleware('fdp-session')) {
  app.use(require('fdp-session')());
}

// Here we need init hdp server instance.
global.hdpserver = require('hdp-ice');
global.hdpserver.init(fdpUtils.getJsonProp(sConfFile, 'hdp-server'));

/*加载应用特有的配置*/
require('./app-local.js')(app, runPattern);

/*开发环境下的静态文件服务配置*/
if(runPattern == 'development'){
  app.use('/static',express.static(path.join(__dirname, 'static')));
  app.use('/s_p', express.static(path.join(__dirname, 's_p')));
}

/*开发和生产环境下都存在的静态文件服务器配置*/
app.use(express.static(path.join(__dirname, 'html/web')));
app.use(express.static(path.join(__dirname, 'html')));

// 处理404
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});


// 错误处理
//app.use(function(err, req, res, next){
//  logger.error('捕获express异常|', JSON.stringify(err));
//  err = err ||{};
//  err.status = err.status || 500;
//  res.status(err.status);
//  var oErrorPath = fdpUtils.getJsonProp(sConfFile, 'error-path');
//  if (err.status == 404) {
//    res.redirect(oErrorPath['404'] || '/')
//    return;
//  }
//  res.redirect(oErrorPath['500'] || '/');
//  return;
//});

//未捕获的异常处理
//app.use(function(err, req, res, next){
//  logger.error('未捕获的express异常|', JSON.stringify(err));
//  res.send('服务器发生未知错误，请与管理员联系。');
//});

/*处理进程未捕获的异常*/
//process.on('uncaughtException', function(err){
//  logger.error('未捕获的进程异常|uncaughtException|' + err);
//});

module.exports = app;

function fnIfUseMiddleware(sName){
  return (oMiddlewareSwitch[sName] || (oMiddlewareSwitch[sName] === undefined));
}

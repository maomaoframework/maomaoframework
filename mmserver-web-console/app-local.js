var express = require('express');
/**
 * 请将所有属于本系统的路由等信息放置在该函数中。
 * @author 康永胜
 * @date   2016-08-31T16:42:25+0800
 * @param  {Object}     app        [应用句柄]
 * @param  {String}     runPattern [当前系统的运行模式，'development'或'production']
 * @return {undefined}             []
 */
module.exports = function(app, runPattern){
  /*管理端登录路由文件加载*/
  var apps = require('.' + __uri('./apps/hdp.routes.js'));
  app.use('/apps/hdp',require('.' + __uri('./apps/hdp.routes.js')));
};

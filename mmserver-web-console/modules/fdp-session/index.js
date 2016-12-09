var logger = require('fdp-logger');
var session = require('express-session');
var RedisStore = require('connect-redis')(session);
var fdpUtils = require('fdp-utils');
var path = require('path');

/**
 * [exports description]
 * @author 康永胜
 * @date   2016-08-31T09:47:53+0800
 * @return {[type]}                 [description]
 */
module.exports = function(){
  var options = {
    secret: '0A19EBADD714A1227AE8593BED06D029',
    saveUninitialized: true,
    unset: 'destroy',
    resave: false,
    cookie: {
      httpOnly: true,
      path: '/'
    }
  };
  
  var redisConf;
  var sessionConf = fdpUtils.getJsonProp('/fdp-config.js','fdp-session') || {};
  var ttl = sessionConf.timeout || 900;

  /*设置session存储*/
  if(process.env.NODE_ENV == 'production'){
    redisConf = fdpUtils.getJsonProp('/fdp-config.js','redis');
    if(redisConf){
      console.log('session第三方存储|host: ' + redisConf.host
        + '|port: ' + redisConf.port);
      redisConf.logErrors = function(err){
        logger.error('connect-redis error|' + err);
      };
      redisConf.prefix = redisConf.prefix || 'fdp-';
      redisConf.ttl = ttl;
      redisConf.db = redisConf.db || 0;
      
      options.store = new RedisStore(redisConf);
    }
  } else {
    options.store = new (require('session-file-store')(session))({
      path: path.join(__dirname, 'sessions'),
      ttl: ttl
    });
  }

  /*设置cookie的名称*/
  if(sessionConf.name){
    options.name = sessionConf.name;
  }

  /*这是domain与path*/
  if(sessionConf.domain){
    options.cookie.domain = sessionConf.cookie.domain;
  }
  if(sessionConf.path){
    options.cookie.path = sessionConf.cookie.path;
  }
  if(sessionConf.httpOnly === false){
    options.cookie.httpOnly = false;
  }
  return session(options);
};
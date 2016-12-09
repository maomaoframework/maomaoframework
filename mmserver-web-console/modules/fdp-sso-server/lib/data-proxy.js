var logger = require('fdp-logger');
var redis = require('redis');
var fdpUtils = require('fdp-utils');
var oConf= fdpUtils.getJsonProp('/fdp-config.js', 'redis');
var client = redis.createClient({
  host: oConf['host'],
  port: oConf['port'],
  password: oConf['pass']
});

client.select(oConf['db']);

client.on('error', function(err){
  console.log(err);
  logger.error('fdp-sso-server|redis error|' + err);
});

exports.set = function(sKey, sValue){
  client.set(sKey, sValue);
}
exports.get = function(sKey, fnCallback){
  client.get(sKey, function(err, reply){
    fnCallback(err, reply);
  });
}

exports.expire = function(sKey, sTime, fnCallback){
  client.expire(sKey, sTime, function(err, reply){
    fnCallback && fnCallback(err, reply);
  });
};
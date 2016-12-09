var logger = require('fdp-logger');
var redis = require('redis');
var logger = require('fdp-logger');
var fdpUtils = require('fdp-utils');
var oConf= fdpUtils.getJsonProp('/fdp-config.js', 'fdp-sso-client.session-center-redis');
var iSessionTimeout = oConf['session-renewal-seconds'];

var client = redis.createClient({
  host: oConf['host'],
  port: oConf['port'],
  password: oConf['pass']
});

client.select(oConf['db']);

client.on('error', function(err){
  logger.error('fdp-sso-server|redis error|' + err);
});

exports.set = function(sKey, sValue){
  client.set(sKey, sValue);
}
exports.get = function(sKey, fnCallback){
  client.get(sKey, function(err, reply){
    fnCallback(err, reply);
  });
  client.expire(sKey, iSessionTimeout, function(err, reply){
    if (err) {
      logger.error('fdp-sso-client|为[' + sKey + ']续期失败|error: ' + JSON.stringify(err));
    }else{
      logger.info('fdp-sso-client|为[' + sKey + ']续期成功|reply: ' + reply);
    }
  });
}

exports.del = function(sKey, fnCallback){
  client.del(sKey, function(err, reply){
    fnCallback(err, reply);
  });
}

exports.expire = function(sKey, sTime, fnCallback){
  client.expire(sKey, sTime, function(err, reply){
    fnCallback && fnCallback(err, reply);
  });
};
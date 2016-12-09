/**
 * 神州易泰fdp框架的全量配置信息，对于每个系统的个性化配置，
 * 请不要写入fdp-config-local.js
 * @author 康永胜
 * @date   2016-08-31T09:47:53+0800
 */
var oUserConfig = require('./fdp-config-local.js');
var util = require('util');

/*----------------配置信息开始----------------*/
var oConfig = {
  'fdp-session': {
    'name': 'mgr.connect.sid'
  },

  /*中间件开关，'true'表示开启，'false'表示关闭，不配置默认开启*/
  'middleware-switch': {
    //为站点提供logo的中间件
    'serve-favicon': true,

    //记录请求日志的中间件
    'fdp-middleware-logger': true,

    //解析cookie的中间件
    'cookie-parser': true,

    //处理session的中间件
    'fdp-session': true,

    //单点登录的服务器中间件
    'fdp-sso-server': false,

    //单点登录的客户端中间件
    'fdp-sso-client': false
  },


  /*配置redis服务信息，该服务主要是fdp-session模块在用，业务代码也可使用该服务*/
  'redis': {
    //数据库的索引号，默认为0
    'db': 0, 

    //session在数据库中存储时key的前缀，默认为'fdp-'
    'prefix': 'fdp-' 
  },

  /*站点图标中间件*/
  'serve-favicon': {
    //图标路径，以应用根目录为起始目录。
    'icon-path': '/favicon.ico'
  },

  /*配置开发和生产环境下的日志级别*/
  'fdp-logger': {
    'logs-path': 'logs', /*日志的存放目录，默认为/logs目录*/
    /**
     *开发模式和生产模式下的业务日志级别，级别由高到低为：
     *error, warn, info, verbose, debug, silly
     *详情性查看: https://github.com/winstonjs/winston
     */
    'level-development': 'debug',
    'level-production': 'error',
    /**
     * 请求日志的记录级别，所有级别如下：
     * combined, common, dev, short, tiny
     * 详情请查看: https://www.npmjs.com/package/morgan
     */
    'morgan-development': 'dev',
    'morgan-production': 'combined',
    'morgan-under-production': true /*生产模式下是否记录请求日志*/
  },

  /*配置session*/
  'fdp-session': {
    //session超时时间，默认为900s，即15min
    'timeout': 900,

    //浏览器中存储cookie的名称，默认为'fdp.connect.sid'
    'name': 'fdp.connect.sid',

    //cookie信息
    'cookie': {
      //cookie对应的domain属性值，false表示不带domain属性
      'domain': false,

      //cookie对应的path属性值，默认为根目录'/'
      'path': '/', 

      //httpOnly属性，默认为true
      'httpOnly': true
    }
  }

};
/*----------------配置信息结束----------------*/

/**
 * 合并APP本地配置信息与默认配置信息
 * @author 康永胜
 * @date   2016-08-31T10:01:33+0800
 * @param  {Object}                 oSource [description]
 * @param  {[type]}                 oTarget [description]
 * @return {[type]}                         [description]
 */
+function fnMergConf(oSource, oTarget){
  for(var key in oSource){
    if ((key in oTarget) && util.isObject(oSource[key])) {
      fnMergConf(oSource[key], oTarget[key]);
      continue;
    }
    oTarget[key] = oSource[key];
  }
}(oUserConfig, oConfig);

module.exports = oConfig;

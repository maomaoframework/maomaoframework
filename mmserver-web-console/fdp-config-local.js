var oAppConfig = {
  'server-port': 3000,
    
  'hdp-server' : {
      protocal: 'tcp',
      ip : 'localhost',
      port : 10000
  },

  /*配置redis服务信息，该服务主要是fdp-session模块在用，业务代码也可使用该服务*/
  'redis': {
    //主机域名，默认为127.0.0.1
    'host': '127.0.0.1',

    //端口，默认为6379
    'port': 6379,

    //密码，默认无密码
    // 'pass': '',
  }
};

module.exports = oAppConfig;

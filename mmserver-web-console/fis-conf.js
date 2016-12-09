fis.get('project.ignore').push('**.svn*');
fis.set('cdn-public', 'http://10.126.3.116:3001');
fis.set('cdn-private', 'http://10.126.3.116:3002');

//私有cdn部署
var deployPrivateCdn = [fis.plugin('http-push', {
    receiver : 'http://10.126.3.116:3003/receiver',
    to : '/home/kangys/ws_node/cdn_private/public'
  })];

//公有cdn部署
var deployPublicCdn = [fis.plugin('http-push', {
    receiver : 'http://10.126.3.116:3003/receiver',
    to : '/home/kangys/ws_node/cdn_public/public'
  })];

//web服务器部署
var deployFdpServer = [fis.plugin('http-push', {
    receiver : 'http://10.126.3.116:3003/receiver',
    to : '/home/kangys/ws_node/fdp_server'
  })];

/*所有less文件编译为css*/
fis.match('*.less', {
  parser: fis.plugin('less'),
  rExt: '.css'
});


/*fis-optimizer-uglify-js 插件进行压缩*/
fis.match('*.js', {
  optimizer: fis.plugin('uglify-js')
});
/*对于已经压缩过的js文件不再进行压缩*/
fis.match('*.min.js', {
  optimizer: null
});

/*fis-optimizer-clean-css 插件进行压缩*/
fis.match('*.css', {
  optimizer: fis.plugin('clean-css')
});
/*对于已经压缩过的css文件不再进行压缩*/
fis.match('*.min.css', {
  optimizer: null
});

/*fis-optimizer-png-compressor 插件进行压缩*/
fis.match('*.png', {
  optimizer: fis.plugin('png-compressor')
});

/*对html、htm和tpl文件进行压缩*/
fis.match('*.{tpl,html,htm}', {
  optimizer: fis.plugin('html-compress'),
});

/*静态文件打上md5戳，发布到s_p目录下*/
fis.match('*.{js,css,less,png,jpg,jpeg,gif,ico}',{
  useHash: true,
  useMap: true,
  release: '/s_p/$0',
  domain: fis.get('cdn-private'),
  // deploy: deployPrivateCdn
});

/*modules目录下的文件全部发布到/node_modules*/
fis.match('/modules/(**)', {
  release: "/node_modules/$1",
  optimizer: null,
  useHash: false,
  useMap: false,
  // deploy: deployFdpServer
});

/*static目录下的文件全部发布到static目录下*/
fis.match('/static/(**)', {
  release: "/static/$1",
  domain: fis.get('cdn-public'),
  // deploy: deployPublicCdn
});

/*所有的html文件全部发布到html目录下*/
fis.match('*.{html,htm}', {
  release: '/html/$0',
  url: "$0",
  domain: false,
  // deploy: deployFdpServer
});

/*根目录下的几个文件以及/bin/www发布到同样的目录下*/
fis.match('/*', {
  release: '/$0',
  useHash: false,
  optimizer: null,
  // deploy: deployFdpServer
});
fis.match('/bin/www', {
  // deploy: deployFdpServer
});

/*路由文件和action文件发布到/routes目录下,不进行压缩和md5*/
fis.match('*.{routes,actions,helper}.js', {
  release: '/routes/$0',
  useHash: false,
  useMap:true,
  optimizer: null,
  domain: false,
  // deploy: deployFdpServer
});

/*所有的模板文件全部发布到views目录下*/
fis.match('*.tpl', {
  release: "/views/$0",
  // deploy: deployFdpServer
});

/*让html和tpl文件支持__uri方法*/
fis.match('*.{html,htm,tpl}', {
  parser: fis3ParserHtmlUri
});

/*一些公共组件不加md5戳*/
fis.match('/static/{bootstrap,echarts,jquery}/**', {
  useHash: false
});
fis.match('/static/jquery-plugs/artDialog/**', {
	useHash: false
});

fis.match('/static/mixins/**', {
  parser: null,
  release: false
});

/*static目录下的ueditor/dialogs目录下的图片进行特殊处理，因为ueditor进使用JavaScript对src属性进行了操作*/
fis.match('/static/ueditor/**', {
  release: "/html/$0",
  url: "$0",
  domain: false,
  optimizer: null,
  useHash: false,
  // deploy: deployFdpServer
});

/*平台说明文档不发布*/
/*release.bat不发布*/
/*static_ba目录下的文件不发布*/
/*隐藏文件不发布*/
fis.match(/.*\.md|\/doc.*|\/readme\.*|\/release.bat/ ,{
  release: false
});
fis.match(/^\..*/ ,{
  release: false
});
fis.match('.svn/**',{
  release: false
});

fis.match('::package', {
  //postpackager: fis3MergSsoConfToAppjs
});

fis.media('development')
   .match('**', {
      optimizer: null,
      useHash: false,
      domain: false,
      // deploy: false
   });

fis.media('test').match('*', {
  // optimizer: null
});


fis.media('production');

/**
 * 使得类html文件支持__uri方法
 * @param  {[type]} content [description]
 * @param  {[type]} file    [description]
 * @param  {[type]} opt     [description]
 * @return {[type]}         [description]
 */
function fis3ParserHtmlUri(content, file, opt) {
  // 只对 html 类文件进行处理
  if (!file.isHtmlLike){
    return content;
  }
  var name = opt.name || '__uri';
  var lang = fis.compile.lang;
  return content.replace(new RegExp(name + '\\((.*?)\\)', 'ig'), function(all, value) {
    return lang.uri.wrap(value);
  });
};

/**
 * 打包阶段插件接口
 * @param  {Object} ret      一个包含处理后源码的结构
 * @param  {Object} conf     一般不需要关心，自动打包配置文件
 * @param  {Object} settings 插件配置属性
 * @param  {Object} opt      命令行参数
 * @return {undefined}
 */
function fis3MergSsoConfToAppjs(ret, conf, settings, opt){
  /*获取所需的文件*/
  var oAppjs = ret.ids['static/js/app.js'];
  var oSsoConf = require('./fdp-config');

  /*替换static/js/app.js中的相关数据*/
  var sContent = oAppjs.getContent()
    .replace(/('|")__if-fdp-sso-client-on__\1/, oSsoConf['middleware-switch']['fdp-sso-client'])
    .replace(/__fdp-sso-client-mount-path__/, oSsoConf['fdp-sso-client']['mount-path'])
    .replace(/__fdp-sso-client-server-uri__/, oSsoConf['fdp-sso-client']['fdp-sso-server-uri'])
    .replace(/__fdp-sso-client-system-name__/, oSsoConf['fdp-sso-client']['system-name']);
  oAppjs.setContent(sContent);
}

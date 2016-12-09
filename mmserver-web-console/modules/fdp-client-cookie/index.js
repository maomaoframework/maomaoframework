var logger = require('fdp-logger');

/**
 * 向cookie对象中设置单个cookie
 * @param  {Object} oCookie [存储cookie的对象]
 * @param  {String} sCookie [单个cookie字符串]
 * @return {null}           [无返回]
 */
exports.fnSetCookie = function(oCookie, sCookie){
  if (!oCookie || !sCookie || (typeof oCookie != 'object') || (typeof sCookie != 'string') ) {
    logger.error('fdp-client-cookie|fnSetCookie 错误|oCookie: ' + oCookie + '|sCookie: ' + sCookie);
    return;
  };

  var aCookie = sCookie.split(';');/*key=value形式的属性数组*/
  var sCookieName;/*cookie名称*/
  var vTpm; /*临时变量*/
  vTpm = aCookie[0].split('=');/*cookie主体*/
  sCookieName = vTpm[0];
  oCookie[sCookieName] = oCookie[sCookieName] ||{};
  oCookie[sCookieName]['value'] = vTpm[1];

  // for (var i = 1; i < aCookie.length; i++) {
  //   vTpm = aCookie[i].split('=');
  //   oCookie[sCookieName][vTpm[0]] = vTpm[1];
  // };
};

/**
 * 获取从cookie对象中获取cookie字符串
 * @param  {Object} oCookie [Cookie对象]
 * @return {String}         [发送到服务器端的Cookie字符串]
 */
exports.fnGetCookies = function(oCookie){
  var aCookie = [];

  for (var key in oCookie) {
    if(!oCookie.hasOwnProperty(key))continue;
    aCookie.push(key + '=' + oCookie[key]['value']);
  };

  return aCookie.join(';');
}
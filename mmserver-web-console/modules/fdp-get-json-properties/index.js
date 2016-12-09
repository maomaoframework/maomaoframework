var fs = require('fs');
var json = null;
module.exports = function(file){
  if (!fs.existsSync(file)) {
    return "文件" + file + "不存在";
  };
  json = require(file);
  return {
    getProperty: _getProperty,/*异步获取属性*/
    getPropertySync: _getPropertySync/*同步获取属性*/
  }
};

/**
 * 异步获取属性值，如果属性不存在，则返回undefined
 * @param  {string}   propertiesStr 表示属性的字符串，如person.name
 * @param  {Function} callback      获取数据成功后的回调函数
 * @return {undefined}              无数据返回
 */
function _getProperty(propertiesStr, callback){
  if (typeof propertiesStr !== 'string' || !callback) {
    return;
  };
  callback(_getPropertyTool(propertiesStr));
}

/**
 * 同步获取属性值，如果属性不存在，则返回undefined
 * @param  {string} propertiesStr 表示属性的字符串，如person.name
 * @return {Object}               返回获取的属性值
 */
function _getPropertySync(propertiesStr){
  return _getPropertyTool(propertiesStr);
};

function _getPropertyTool(propertiesStr){
  propertiesStr = propertiesStr || '';
  propertiesStr = propertiesStr.trim();

  if(!propertiesStr || !json){
    return undefined;
  }

  var properties = propertiesStr.split('.');
  var length = properties.length;
  var result = undefined;
  var currentDomain = json;

  for (var i = 0; i < length; i++) {
    currentDomain = result = currentDomain[properties[i]];
    if(!result)break;
  }

  return result;
}


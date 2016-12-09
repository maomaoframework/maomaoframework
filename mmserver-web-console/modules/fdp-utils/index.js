var path = require('path');
var jsonData = require('fdp-get-json-properties');
var mkdirp = require('mkdirp');
var fs = require('fs');

exports.typeof = _typeof;
exports.getJsonProp = _getJsonProp;
exports.createDir = _createDir;

/*
 *判断所输入参数的类型，并以小写字母组成的字符串返回结果
 *@param {any type} param
 *@return type
 */
function _typeof(o){
  if(o === null){
    return "null";
  }
  if(o === undefined){
    return "undefined";
  }
  return Object.prototype.toString.call(o).slice(8, -1).toLowerCase();
}

/**
 * 从JSON文件中获取出行
 * @param  {[type]} file     JSON文件的路径，文件路径从根目录开始。
 * @param  {[type]} propsStr 以.分割的属性列表
 * @return {[type]}          返回获取的属性
 */
function _getJsonProp(file, propsStr){
  /*应用根路径*/
  var rootDir = process.env.NODE_FDP_ROOT || __dirname;
  /*配置文件*/
  file = file || 'fdp-config.js';
  var configFile = path.join(rootDir, file);

  /*结果*/
  var result;

  /*判断配置文件是否存在*/

  configData = jsonData(configFile);
  if(typeof configData == 'object'){
    result = configData.getPropertySync(propsStr);
  }else{
    result = null;
    logger.error(file + '不存在。');
  }

  return result;
}

function _createDir(path){
  var result = true;
  if(!fs.existsSync(path)){
    if(!mkdirp.sync(path)){
      result = false;
    }
  }

  return result;
}
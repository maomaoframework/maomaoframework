var logger = require('fdp-logger');
var util = require('util');
var qs = require('querystring');

/**
 * 属性名隐射器的构造函数
 * @author 康永胜
 * @date   2016-09-06T10:15:34+0800
 * @param  {Array}     aPropsMap [属性映射的配置数组]
 * @return {Object               [属性名隐射器]
 */
module.exports = function(aPropsMap){
  if(!util.isArray(aPropsMap)){
    logger.error('属性隐射配置必须是数组。');
    throw new Error('属性隐射配置必须是数组。');
  }

  /*记录属性映射规则的数组, 每一个规则的数据结构如如下:
   * {
   *   dataPro: 'aaa', //程序调用api时候的参数名
   *   paramPro: 'bbb' //框架发起请求时的接口名
   * }
  */
  var mapArrs = [];

  /*映射规则数组的长度, 方便后续遍历使用*/
  var iMapArrsLen = 0;

  /*遍历参数, 建立规则列表*/
  var tmpMapArr;
  for (var i = 0; i < aPropsMap.length; i++) {
    tmpMapArr = aPropsMap[i].split('->');
    mapArrs.push({
      dataPro: tmpMapArr[0].trim(),
      paramPro: tmpMapArr[1].trim()
    });
  }

  iMapArrsLen = mapArrs.length;

  /**
   * [对参数对象属性进行名称映射的方法]
   * @author 康永胜
   * @date   2016-09-06T10:21:46+0800
   * @param  {Object}      param   [调用参数]
   * @return {Object}              [返回当前对象，方便链式调用]
   */
  this.transName = function(param){
    /*如果参数不是对象, 则直接返回*/
    if(!util.isObject(param)){
      return this;
    }

    /*转换属性名称*/
    for (var i = 0; i < iMapArrsLen; i++) {
      if(mapArrs[i]['dataPro'] in param){
        param[mapArrs[i]['paramPro']] = param[mapArrs[i]['dataPro']];
        delete param[mapArrs[i]['dataPro']];
      }
    }

    return this;
  };

  /**
   * [根据Content-Type将参数进行编码]
   * @author 康永胜
   * @date   2016-09-06T10:56:05+0800
   * @param  {Object}    oParam       [参数对象]
   * @param  {String}    sContentType [内容类型]
   * @return {Variant}   vResult      [编码结果]
   */
  this.encodeByContentType = function(oParam, sContentType){
    /*如果参数不是对象, 则直接返回*/
    if(!util.isObject(oParam)){
      return oParam;
    }
    /*返回结果*/
    var vResult = null;
    /*默认的Content-Type类型*/
    sContentType = sContentType || 'application/x-www-form-urlencoded';

    /*根据Content-Type进行编码*/
    switch(sContentType){
      case 'application/x-www-form-urlencoded':
        vResult = fnEncodeXWwwFormUrlencoded(oParam);
        break;
      case 'application/json':
      case 'text/json':
        vResult = fnEncodeJson(oParam);
        break;
      case 'text/plain':
        vResult = fnEncodePlainText(oParam);
        break;
      default:
        throw new Error('fdp-data-proxy|ParamMapper|不支持以下编码格式|' + sContentType);
        logger.error('fdp-data-proxy|ParamMapper|不支持以下编码格式|' + sContentType);
    }

    return vResult;
  };
};

/**
 * [对Content-Type类型为'application/x-www-form-urlencoded'的请求参数进行编码]
 * @author 康永胜
 * @date   2016-09-06T10:37:28+0800
 * @param  {Object}    param  [参数对象]
 * @return {String}           [编码结果]
 */
function fnEncodeXWwwFormUrlencoded(param){
  /*如果参数不是对象, 则直接返回*/
  if(!util.isObject(param)){
    return param;
  }

  /*将非字符串类型的参数转换为字符串*/
  for (key in param) {
    if(!param.hasOwnProperty(key)){
      continue;
    }
    /*如果不是字符串，则转换*/
    if (!util.isString(param[key])) {
      param[key] = JSON.stringify(param[key]);
    };
  }

  return qs.stringify(param);
}

/**
 * [对Content-Type类型为'application/json'的请求参数进行编码]
 * @author 康永胜
 * @date   2016-09-06T11:31:24+0800
 * @param  {Object}    param  [参数对象]
 * @return {String}           [编码结果]
 */
function fnEncodeJson(oParam){
  /*如果参数不是对象, 则直接返回*/
  if(!util.isObject(param)){
    return param;
  }

  return JSON.stringify(oParam);
}

/**
 * [对Content-Type类型为'text/plain'的请求参数进行编码]
 * @author 康永胜
 * @date   2016-09-06T11:31:24+0800
 * @param  {Object}    param  [参数对象]
 * @return {String}           [编码结果]
 */
function fnEncodePlainText(oParam){
  return fnEncodeJson(oParam);
}
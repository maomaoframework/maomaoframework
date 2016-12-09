var logger = require('fdp-logger');
var minimatch = require("minimatch");
var util = require('util');

/*参数值与数据源名称的映射缓存, 数据格式为:
 * {
 *   '参数值': '数据源名称'  
 * }
*/
var oSourceMap = {};

/*
 * 路由规则，数据格式为:
 * {
 *   'route-rule-param-name': [{rule: '', sourceName: ''}]
 * }
*/
var oRules = {};

/*'route-rule-param-name'组成的数组，数组中靠前的值会被优先选取*/
var aParamNames = [];

/**
 * [SourceRouter的构造函数]
 * @author 康永胜
 * @date   2016-09-09T14:25:20+0800
 * @param  {Object}       oConf [fdp-data-proxy.data-sources的全部配置内容]
 * @return {Object}             [新创建的对象]
 */
module.exports = function(oConf){
  if (!oConf) {
    logger.error('fdp-data-proxy|获取[data-sources]配置失败');
    throw new Error('fdp-data-proxy|获取[data-sources]配置失败');
  }

  /*初始化oRules与aParamNames*/
  var sParamName = null;
  var oSourceConf = null;
  for (var sourceName in oConf) {
    /*按照数据源配置的先后顺序将'route-rule-param-name'的值记录下来*/
    oSourceConf = oConf[sourceName];
    sParamName = oSourceConf['route-rule-param-name'] || 'service';
    if (!oRules[sParamName]) {
      aParamNames.push(sParamName);
      /*以'route-rule-param-name'的值为键存储路由规则*/
      oRules[sParamName] = [];
    }

    /*记录所有的规则*/
    for (var i in oSourceConf['route-rules']) {
      oRules[sParamName].push({
        rule: oSourceConf['route-rules'][i],
        sourceName: sourceName
      });
    }
  }

  /**
   * [根据传入的参数, 返回路由到的数据源名称]
   * @author 康永胜
   * @date   2016-09-07T14:53:46+0800
   * @param  {[type]}                 oParam [description]
   * @return {[type]}                        [description]
   */
  this.getSourceName = function(oParam){
    if(!util.isObject(oParam)){
      logger.error('fdp-data-proxy|数据源路由失败, 参数不是Object类型|' + oParam);
      throw new Error('fdp-data-proxy|数据源路由失败, 参数不是Object类型|' + oParam);
    }
    /*1. 获取用于路由的参数名称和参数值*/
    var sParamName = '';
    var iLen = aParamNames.length;
    for (var i = 0; i < iLen; i++) {
      if (aParamNames[i] in oParam) {
        sParamName = aParamNames[i];
        break;
      }
    }
    if(!sParamName){
      logger.error('fdp-data-proxy|数据源路由失败, 使用默认数据源default|');
    }

    var sParamValue = oParam[sParamName];

    if (sParamValue && !util.isString(sParamValue)) {
      try{
        sParamValue = JSON.stringify(sParamValue);
      }catch(e){
        logger.error('fdp-data-proxy|sParamValue转换为字符串失败|' + sParamValue);
      }
    }

    /*2. 根据获取到的参数名称和参数值进行路由选择*/
    return fnGetSourceName(sParamName, sParamValue);
  };
};

/**
 * 根据参数名和参数值获取匹配的数据源名称
 * @author 康永胜
 * @date   2016-09-06T17:07:14+0800
 * @param  {String}      sParamName  [参数名称]
 * @param  {String}      sParamValue [参数值]
 * @return {String}                  [被选中的数据源名称]
 */
function fnGetSourceName(sParamName, sParamValue){
  var result = 'default';
  if (!sParamValue) {
    logger.info('fdp-data-proxy|路由数据源时无参数名|采用默认数据源');
    return result;
  }

  var sCacheKey = sParamName + '--' + sParamValue;
  if (sCacheKey in oSourceMap) {
    return oSourceMap[sCacheKey];
  }

  var aRules = oRules[sParamName];
  var iLen = aRules.length;

  /*遍历规则列表*/
  for (var i = 0; i < iLen; i++) {
    if (minimatch(sParamValue, aRules[i]['rule'])) {
      result = aRules[i]['sourceName'];
      break;
    }
  }

  /*如果没有获取数据源名称，则使用默认数据源*/
  if (i >= iLen) {
    logger.info('fdp-data-proxy|使用参数名[' + sParamName + ']及参数值[' + sParamValue + ']路由数据源失败|采用默认数据源');
  }else{
    logger.info('fdp-data-proxy|使用参数名[' + sParamName + ']及参数值[' + sParamValue + ']路由成功|选择数据源[' + result + ']');
  }

  /*将匹配结果存入缓存*/
  oSourceMap[sCacheKey] = result;

  /*返回*/
  return result;
}
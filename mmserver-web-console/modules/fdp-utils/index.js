var path = require('path');
var jsonData = require('fdp-get-json-properties');
var mkdirp = require('mkdirp');
var fs = require('fs');

exports.typeof = _typeof;
exports.getJsonProp = _getJsonProp;
exports.createDir = _createDir;

/*
 *�ж���������������ͣ�����Сд��ĸ��ɵ��ַ������ؽ��
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
 * ��JSON�ļ��л�ȡ����
 * @param  {[type]} file     JSON�ļ���·�����ļ�·���Ӹ�Ŀ¼��ʼ��
 * @param  {[type]} propsStr ��.�ָ�������б�
 * @return {[type]}          ���ػ�ȡ������
 */
function _getJsonProp(file, propsStr){
  /*Ӧ�ø�·��*/
  var rootDir = process.env.NODE_FDP_ROOT || __dirname;
  /*�����ļ�*/
  file = file || 'fdp-config.js';
  var configFile = path.join(rootDir, file);

  /*���*/
  var result;

  /*�ж������ļ��Ƿ����*/

  configData = jsonData(configFile);
  if(typeof configData == 'object'){
    result = configData.getPropertySync(propsStr);
  }else{
    result = null;
    logger.error(file + '�����ڡ�');
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
var assert = require('chai').assert;
var expect = require('chai').expect;
var app = require('../index');
var path = require('path');

describe('fdp-get-json-properties', function() {
  describe('文件是否存在测试', function () {
    it('文件路径不正确时应当返回：文件./test.json不存在', function () {
      assert.equal('文件./test.json不存在', app('./test.json'));
    });
    it('文件路径不存在时应当返回：文件' +path.join(__dirname, 'test2.json') +'不存在', function () {
      assert.equal('文件' + path.join(__dirname, 'test2.json') + '不存在', app(path.join(__dirname, 'test2.json')));
    });
    it('文件名正确时应当返回一个对象', function () {
      expect(app(path.join(__dirname, 'test.json'))).to.be.an('object');
    });
  });

  var jsonFile = app(path.join(__dirname, 'test.json'));
  describe('属性获取测试', function () {
    it('无属性名返回undefined', function () {
      assert.equal(undefined, jsonFile.getPropertySync());
      assert.equal(undefined, jsonFile.getProperty());
    });
    it('属性名为空返回undefined', function () {
      assert.equal(undefined, jsonFile.getPropertySync(''));
      jsonFile.getProperty('', function(data){
        assert.equal(undefined, data);
      });
    });
    it('属性名不正确返回undefined', function () {
      assert.equal(undefined, jsonFile.getPropertySync('person.xx'));
      jsonFile.getProperty('person.xx', function(data){
        assert.equal(undefined, data);
      });
    });
    it('名称是kangys', function () {
      assert.equal('kangys', jsonFile.getPropertySync('person.name'));
      jsonFile.getProperty('person.name', function(data){
        assert.equal(data, 'kangys');
      });
    });
    it('assets属性应当是一个object', function () {
      expect(jsonFile.getPropertySync('person.assets')).to.be.an('object');
      jsonFile.getProperty('person.assets', function(data){
        expect(data).to.be.an('object');
      });
    });
  });
});
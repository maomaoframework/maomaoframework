module.exports = function(){
  var cache = {};

  this.set = function(key, value){
    cache[key] = value;
    return this;
  };

  this.get = function(key){
    return cache[key];
  };

  this.getAll = function(){
    return cache;
  };
};
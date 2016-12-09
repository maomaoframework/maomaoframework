var express = require("express");
var router = express.Router();
var logger = require('fdp-logger');

router.get('/', function(req, res){
  sendRequest(req, res, 'get');
});

router.post('/', function(req, res){
  sendRequest(req, res, 'post');
});

module.exports = router;

function sendRequest(req, res, method){
  var body = req.body;
  var reqPath; /*记录请求路径*/
  var tmpParam; /*记录参数解析时候的查询参数*/
  logger.info('ajax|' + method + '|' + JSON.stringify(body));
  body.complete = function(data){
    logger.info('ajax|result|' + data);
    res.send(data);
  };
  // if(method == 'get'){
  reqPath = req.url;
  body = req.body || {};
  reqPath = reqPath.split('?');
  if(reqPath[1]){
    reqPath = reqPath[1].split('&');
    for(var i = 0; i< reqPath.length; i++){
      tmpParam = reqPath[i].split('=');
      body[tmpParam[0]] = tmpParam[1] || '';
    }
  }
  // }
  dataproxy[method](req, res, body, 'text');
}
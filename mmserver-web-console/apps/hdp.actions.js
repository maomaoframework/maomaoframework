var logger = require('fdp-logger');
var qs = require('querystring');
var fs = require('fs');
var path = require('path')
var logger = require('fdp-logger');
var utils = require('hdp-util');

var hdserver = require('hdp-ice').hdpserver;
var AppService = require('hdp/AppService.js');
var SSHServerService = require('hdp/SSHServerService.js');

module.exports = HdpAction;

var currentPath = path.resolve(__dirname, '.');

function HdpAction (){
}


/**
 *获取全部的应用
 */
HdpAction.prototype.data_applist = function(req, res){
  // 查询全部的应用
    var app = hdserver.getApp("master");
    app.getService("AppService", AppService).then(function(service){
        service.loadApps().then(function(result){
            res.json(result);
        });
    });
}

/**
 *获取全部的应用
 */
HdpAction.prototype.app_detail = function(req, res){
	var appid = utils.getParam(req, "appid");
	
	// 读取全部服务
	res.render('apps/hdp/app_detail', { appid : appid} );
}


/**
 * 提交一个应用实例
 */
HdpAction.prototype.data_commitInstance = function(req, res){
	var instance = req.body;
	
	// 查询全部的应用
    var app = hdserver.getApp("master");
    if (instance.appIndex.length > 0) {
    	// 修改已有的实例
    	
    	app.getService("AppService", AppService).then(function(service){
        	// 判断是添加一个实例还是修改一个实例
    		var _ins = instance;
            service.updateAppInstance(_ins.appid, _ins.appIndex, JSON.stringify(_ins)).then(function(result){
                res.json(result);
            });
        });
    } else {
    	// 创建一个新的实例
    	app.getService("AppService", AppService).then(function(service){
        	// 判断是添加一个实例还是修改一个实例
    		var _ins = instance;
            service.createAppInstance(_ins.appid, JSON.stringify(_ins)).then(function(result){
                res.json(result);
            });
        });
    }
    
}

/**
 *获取一个应用的运行实例
 */
HdpAction.prototype.data_instancelist = function(req, res){
  // 查询全部的应用
    var app = hdserver.getApp("master");
    app.getService("AppService", AppService).then(function(service){
        service.getHdpServerInfo().then(function(result){
            res.json(result);
        });
    });
}

/**
 *　启动一个应用实例
 */
HdpAction.prototype.data_startup = function(req, res){
	var appid = utils.getParam(req, "appid");
	var insid = utils.getParam(req, 'insid');
	
    var app = hdserver.getApp("master");
    app.getService("AppService", AppService).then(function(service){
        service.startAppInstance(appid, insid).then(function(result){
            res.json(result);
        });
    });
}

/**
 * 停止一个应用实例
 */
HdpAction.prototype.data_shutdown = function(req, res){
	var appid = utils.getParam(req, "appid");
	var insid = utils.getParam(req, 'insid');
	
    var app = hdserver.getApp("master");
    app.getService("AppService", AppService).then(function(service){
        service.stopAppInstance(appid, insid).then(function(result){
            res.json(result);
        });
    });
}

/**
 * 删除一个应用实例
 */
HdpAction.prototype.data_undeploy = function(req, res){
	var appid = utils.getParam(req, "appid");
	var insid = utils.getParam(req, 'insid');
	
    var app = hdserver.getApp("master");
    app.getService("AppService", AppService).then(function(service){
        service.removeAppInstance(appid, insid).then(function(result){
            res.json(result);
        });
    });
}

/**
 * 服务器管理主界面
 */
HdpAction.prototype.page_server_index = function(req, res){
	// 检查文件是否存在，如果不存在，则创建
//	var isExist = fs.existsSync(currentPath + '/server.json');
//	
//	if (!isExist) {
//		// 创建一个文件对象
//		writeServersFile("[]");
//	}
	
	// 显示当前所有设定的服务器
	res.render('apps/hdp/server_manager/index', null);
}

/**
 * 提交服务器信息
 */
HdpAction.prototype.data_commitServer = function(req, res){
	var newServer = req.body;
	
	// 读取json文件
	var app = hdserver.getApp("master");
	if (newServer.key.length > 0) {
		// 更新服务器信息
		app.getService("SSHServerService", SSHServerService).then(function(service){
	        service.updateServer(newServer.key, JSON.stringify(newServer)).then(function(result){
	            res.json(JSON.parse(result));
	        });
	    });
	} else {
		// 添加一个服务器实例
		app.getService("SSHServerService", SSHServerService).then(function(service){
	        service.addServer(JSON.stringify(newServer)).then(function(result){
	        	var ret = JSON.parse(result);
	            res.json(ret);
	        });
	    });
	}
}

/**
 * 删除服务器信息
 */
HdpAction.prototype.data_removeServer = function(req, res){
	var key = utils.getParam(req, "key");
	var app = hdserver.getApp("master");
	app.getService("SSHServerService", SSHServerService).then(function(service){
        service.removeServer(key).then(function(result){
            res.json(JSON.parse(result));
        });
    });
}

/**
 * 服务器管理主界面
 */
HdpAction.prototype.data_loadServers = function(req, res){
	// 读取json文件
	var app = hdserver.getApp("master");
    app.getService("SSHServerService", SSHServerService).then(function(service){
        service.loadServers().then(function(result){
            res.json(JSON.parse(result).RtnMsg.rows);
        });
    });
}
//
///**
// * 创建一个空的json文件
// */
//function writeServersFile (str){
//	fs.writeFileSync(currentPath + '/server.json',str);
//}
//
///**
// * 读取servers配置文件
// * @returns
// */
//function loadServersFile(){
//	return JSON.parse(fs.readFileSync(currentPath + '/server.json'));
//}

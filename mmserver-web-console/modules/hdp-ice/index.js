/************************************
 *  hdp-ice类库
 *  
 *  本类库用于支持通过ice连接后端java服务
 *  请注意：本库尚未对服务器状态进行检查，后续需要增加
 *  动态定时与主hdp服务器同步应用路由表的功能．可以采用
 *  多线程或定时任务开启同步路由表功能．此功能可以使当有服务器发生宕机时，node服务器可以自动切换到正确
 *  的hdp服务器上．同时还要增加hdp主服务器推送变化的路由表信息到node端，以更细node端的路由表，及时
 *  获取后端hdp服务器的最新路由表信息，避免因为应用宕机而导致node服务器无法连接后端服务．
 *  
 *  Author:huxg
 *  Date:2016-11-02
 ***********************************/

var AppService = require("hdp/AppService.js");
var Ice = require("ice").Ice;

_hdpServer_ = new HdpServer();
__IS_INIT_HDP_SERVER__ = false;

module.exports = {
		// hdp服务器对象，这里采用单态实例
		hdpserver : _hdpServer_,
		
		/**
		 * 初始化hdp服务器
		 */
		init : function(ice_options) {
			this.hdpserver.init(ice_options);
		},
		
		/**
		 * 获取hdp服务器实例
		 */
		getAppInstance : function(appName){
			return this.hdpserver.getAppInstance(appName);
		}
}

/**
 * hdp服务器连接对象
 */
function HdpServer() {
	/**
	 * 单例模式实现
	 * 
	 * @returns {HdpServer}
	 */
	if (!(this instanceof HdpServer)) {
		return new HdpServer();
	}
	
	// 创建服务器缓存对象
	// 所谓servers，指的是在hdp平台中都运行了哪些app服务器，一个应用，对应一个server，一个server
	// 下面又对应多个实例，其数据结构类似于:
	// [{name:'server1 , instances: [{ip:'localhost', port: 1000}, {ip: '10.1.4.232', port : 12000}]}, {name:'server2', instances : []}]
	this.servers = {};
}
/**
 * 创建master
 */
HdpServer.prototype.init = function(ice_options) {
	// master -- 主hdp服务器的ip地址，不管是通过zookeeper进行负载均衡，还是单独的hdp服务器都可以成为主hdp服务器
	// 在初始化时，首先创建主服务器对象，用于与服务器进行交互以获取重要的hdp应用实例的路由表信息
	this.servers.master = new HdpApp(this, 'master');
	
	// 初始化master的连接池
	var appInstance = this.servers.master.createInstance(ice_options);
	
	// 同步hdp主服务器所维护的应用路由表信息
	this.syncHdpServerInfo(appInstance);
}

/**
 * 取得master
 */
HdpServer.prototype.getMaster = function(){
	if (__IS_INIT_HDP_SERVER__ == false) {
		// 断路路由
		console.log("-- Error: The hdp server is not inited.May be hdp server cannot connect. Please check whether the hdp server is avaliable.");
		
		var fdpUtils = require('fdp-utils');
		_hdpServer_.init(fdpUtils.getJsonProp('/fdp-config.js', 'hdp-server'));
		return null;
	} else {
		return this.servers.master;
	}
}

/**
 * 取得一个应用
 */
HdpServer.prototype.getApp = function(appName){
	if (__IS_INIT_HDP_SERVER__ == false) { 
		console.log("-- Error: The hdp server is not inited.May be hdp server cannot connect. Please check whether the hdp server is avaliable.");
		
		var fdpUtils = require('fdp-utils');
		_hdpServer_.init(fdpUtils.getJsonProp('/fdp-config.js', 'hdp-server'));
		return null;
	} else {
		if (null == this.servers[appName]) {
			this.servers[appName] = new HdpApp(this, appName);
		}
		return this.servers[appName];
	}
}

/**
 * 同步服务器配置
 */
HdpServer.prototype.syncHdpServerInfo = function(appInstance){
	// 检查是否已经存在了该Service的配置信息
	var _self = this;
	Ice.Promise.try(function() {
		var ic = Ice.initialize();
    	var base = ic.stringToProxy("AppService:" + appInstance.ice_options);
        AppService.idl.AppServicePrx.checkedCast(base).then(function(service){
				if (null == service) {
					console.log("无法同步HDP后端服务器相关服务信息！");
				}
				service.getHdpServerInfo().then(function(result){
					
					// 返回数据后创建应用连接
					var servers = JSON.parse(result);
					if (undefined != servers && servers.length > 0){
						servers.forEach(function(e){
							// 缓存应用
							var app = _self.getApp(e.appId);
							
							var __self = _self;
							// 缓存实例
							if (undefined != e.instances && null != e.instances && e.instances.length > 0) {
								e.instances.forEach(function(elInstance){
									// 输出日志
									__IS_INIT_HDP_SERVER__ = true;
									console.log("-- 已加载hdp服务器路由表 " + e.appId + " " + JSON.stringify(elInstance));
									var appInstance = app.createInstance(elInstance);
								});
							}
						});
					}
				});
            });
    }).finally( function() {
        if(ic) {
            return ic.destroy();
        }
    }).exception( function(ex) {
        console.log(ex.toString());
        process.exit(1);
    });
}

/**
 * 应用
 */
function HdpApp(hdpserver, name){
	this.hdpserver = hdpserver;
	this.name = name;
	this.cachedAppInstances = new Array();
}

/**
 * 创建一个App实例
 */
HdpApp.prototype.createInstance = function(ice_options, callback){
	var appInstance = new HdpAppInstance(ice_options);
	this.cachedAppInstances.push(appInstance);
	return appInstance;
}

/**
 * 取得一个服务
 */
HdpApp.prototype.getService = function (serviceName, service, callback){
	
	// 取得当前时间
	var pos = -1;
	if (this.cachedAppInstances.length == 0){
		return null;
	} else if (this.cachedAppInstances.length == 1){
		pos = 0;
	} else if (this.cachedAppInstances.length > 1) {
		var time = new Date().getTime();
		var strTime = time.toString();
		pos = parseInt(strTime.substring(strTime.length - 1)) % 3;
	}
	
//	console.log(service);
	var appInstance = this.cachedAppInstances[pos];
	
	// 检查是否已经存在了该Service的配置信息
	var servicePrx = null;
	if (service.idl != undefined) {
		for (key in service.idl) {
			if (key == serviceName + "Prx") {
				servicePrx = service.idl[key];
			}
		}
	}
	
//	console.log(service.idl);
//	console.log("------------")
	// 返回Promise对象
	if (null != servicePrx && undefined != servicePrx.checkedCast) {
		return (function(appInstance){
			var promise = new Promise(function(resolve, reject){
				Ice.Promise.try(function() {
					var ic = Ice.initialize();
					var base = ic.stringToProxy(serviceName + ":" + appInstance.ice_options);
	    			return servicePrx.checkedCast(base).then(function(service){
	    				resolve(service);
		       		});
				}).finally( function() {
					if(ic) {
			        	return ic.destroy();
				    }
				}).exception( function(ex) {
			        console.log(ex.toString());
			    });
			});
			return promise;
		})(appInstance);
	}
}

/**
 * 应用实例
 */
function HdpAppInstance(ice_options){
	// 将给定的ice_options对象拼装成字符串命令
	// 可支持的字符串命令参考：https://doc.zeroc.com/display/Ice36/Proxy+and+Endpoint+Syntax
	// TCP Endpoint Syntax tcp -h host -p port -t timeout -z --sourceAddress addr
	// SSL Endpoint Syntax ssl -h host -p port -t timeout -z --sourceAddress addr
	var str = "";
	if (ice_options.protocal){
		str += ice_options.protocal;	
	} else {
		str += "default";
	}

	if (ice_options.ip){
		str += " -h " + ice_options.ip;
	}
	if (ice_options.port){
		str += " -p " + ice_options.port;
	}
	this.ice_options = str;
}
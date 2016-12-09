#include <App.ice>
#include <AppHashServerInfo.ice>

/**
 * 服务
 */
[["java:package:com.maomao.server.manager"]]
module idl {
	["java:type:java.util.ArrayList<App>"]
	sequence<App> ListApps;
	
	/**
	 * App服务
	 */	
	interface AppService {
		void stopServer();
		
		/**
		 * 返回全部App
		 */
		ListApps loadApps();
	    
	    /**
	     * 停止一个App的某一个实例
	     * instanceId 传递为空时，表示停止该App的全部实例
	     */
	    string stopAppInstance(string appId, string instanceId);
	    
	    /**
	     * 停止一个App的某一个实例
	     * instanceId 传递为空时，表示停止该App的全部实例
	     */
	    string restartAppInstance(string appId, string instanceId);
	    
	    /**
	     * 启动一个App实例
	     * instanceId为空时,表示启动该App的全部实例
	     */
	    string startAppInstance(string appId, string instanceId);
	    
	    /**
	     * 删除一个App实例
	     */
	    string removeAppInstance(string appId, string instanceId);
		
		/**
		 * Create a new app
		 */
		string createApp(string appId);
		
		/**
		 * 删除一个App
		 */
		string removeApp(string appId);
	
		/**
		 * 停止一个App及其所有实例
		 */
		string stopApp(string appId);
		
		/**
		 * 启动App的所有实例
		 */
		string startApp (string appId);
		
		/**
		 * 重启一个应用的所有实例
		 */
		string restartApp(string appId);
		
		/**
		 * 创建一个新的App实例
		 */	    
		string createAppInstance(string appId,string appInstanceJson);
		
		/**
		 * 更新一个现有的App实例
		 */	    
		string updateAppInstance(string appId, string instanceId, string appInstanceJson);
		
		/**
		 * 同步服务器信息
		 */
		string syncServerInfo();
		
		/**
		 *　返回服务器信息
		 */
		string getHdpServerInfo();
		
		/**
		 *　app与hdpserver保持同步状态
		 */
		string appSyncStatus(string jsonInfo, string ip, int port);
		
		/**
		 * app停止时发送给hdp server的通知事件
		 */
		string appStopNotify(string appId, string ip, int port);
		
		/**
		 * app启动完毕后，发送通知
		 */
		string appStartupNotify(string appId, string ip, int port);
		
		/**
		 * 迫使服务器关闭
		 */
		void forceAppInstanceShutdown(int seconds);
		
		/**
		 * 迫使服务器关闭
		 */
		void forceAppInstanceRestart(int seconds);
	};
};
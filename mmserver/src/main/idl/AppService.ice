#include <App.ice>
#include <AppHashServerInfo.ice>

[["java:package:com.maomao.server.manager"]]
module idl {
	["java:type:java.util.ArrayList<App>"]
	sequence<App> ListApps;
	
	interface AppService {
		void stopServer();
		
		ListApps loadApps();
	    
	    string stopAppInstance(string appId, string instanceId);
	    
	    string restartAppInstance(string appId, string instanceId);
	    
	    string startAppInstance(string appId, string instanceId);
	    
	    string removeAppInstance(string appId, string instanceId);
		
		string createApp(string appId);
		
		string removeApp(string appId);
	
		string stopApp(string appId);
		
		string startApp (string appId);
		
		string restartApp(string appId);
		
		string createAppInstance(string appId,string appInstanceJson);
		
		string updateAppInstance(string appId, string instanceId, string appInstanceJson);
		
		string syncServerInfo();
		
		string getServerInfo();
		
		string appSyncStatus(string jsonInfo, string ip, int port);
		
		string appStopNotify(string appId, string ip, int port);
		
		string appStartupNotify(string appId, string ip, int port);
		
		void forceAppInstanceShutdown(int seconds);
		
		void forceAppInstanceRestart(int seconds);
	};
};
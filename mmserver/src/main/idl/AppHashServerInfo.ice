/**
 * App运行实例
 */
[["java:package:com.maomao.server.manager"]]
module idl {
	struct AppHashServerInfo {
		string appId;
		string ip;
		int port;
		bool ssl;
	};
};
/**
 * SSH服务器服务类 
 */

/**
 * 服务
 */
[["java:package:com.maomao.server.manager"]]
module idl {
	/**
	 * App服务
	 */	
	interface SSHServerService {
		/**
		 * 返回服务器信息
		 */
		string loadServers();
	    
	    /**
	     * 添加一个服务器信息
	     */
	    string addServer(string serverJson);
	    
	    /**
	     * 更新一个服务器信息
	     * 
	     */
	    string updateServer(string key, string serverJson);
	    
	    /**
	     * 删除一个服务器信息
	     */
	    string removeServer(string key);
	};
};
/**
 * App类 
 */
[["java:package:com.maomao.server.manager"]]
module idl {
	struct App {
	  // 包名
	  string pk;
	  
	  // 应用ｉｄ
	  string appid;
	  
	  // 应用名称
	  string name;
	  
	  // 应用描述
	  string description;
	  
	  // 开发者邮件
	  string email;
	  
	  // 开发者
	  string developer;
	  
	  // 版本号
	  string versionLabel;
	  
	  // 版本
	  int version;
	};
};
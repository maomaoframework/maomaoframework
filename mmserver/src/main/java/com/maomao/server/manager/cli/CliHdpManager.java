/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.maomao.server.manager.cli;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.Properties;


import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.maomao.framework.support.rpc.ice.IceClient;
import com.maomao.framework.support.rpc.ice.IceClient.Action;
import com.maomao.framework.utils.FileUtils;
import com.maomao.framework.utils.JsonUtils;
import com.maomao.framework.utils.Message;
import com.maomao.server.App;
import com.maomao.server.AppManager;
import com.maomao.server.Constants;
import com.maomao.server.manager.idl.AppService;
import com.maomao.server.manager.idl.AppServicePrx;
import com.maomao.server.manager.idl.SSHServerService;
import com.maomao.server.manager.idl.SSHServerServicePrx;
import com.maomao.server.util.ZipUtils;

/**
 * Command line manager
 * 
 * @author maomao
 * 
 */
public class CliHdpManager {

	public static final String formatstr = "[-ic][-id][-is][-im][-ir][-ac][-ad][-ar][-as][-au] \"other params\"";
	String[] args;
	CommandLine commandLine;
	String hdpserver_ip;
	int hdpserver_port;
	boolean hdpserver_ssl;
	File hdpbaseFolder;
	File hdpHomeFolder;
	File hdpAppsFolder;

	public CliHdpManager(String[] args) {
		this.args = args;
	}

	public static void main(String[] args) throws Exception {
		new CliHdpManager(args).doCommand();
	}

	/**
	 * execute command
	 * 
	 * @throws Exception
	 */
	void doCommand() throws Exception {
		// build the command options
		commandLine = buildOptions();
		try {
			// parse command line
			commandLine.parse(args);
		} catch (Exception e) {
			printUsage(commandLine);
			System.exit(-1);
		}

		try {
			// load system configuration
			loadHdpServerConfiguration();
		} catch (Exception e) {
			System.out.println("cannot find server.properties file in $HDP_BASE : " + System.getProperty(Constants.BASE_PROP));
			System.exit(-2);
		}

		try {
			// execute command
			executeOpt(commandLine);
		} catch (Exception e) {
			e.printStackTrace();
			printError(e.getMessage());
			printUsage(commandLine);
		}
	}

	/**
	 * execute option
	 * 
	 * @param cmd
	 * @throws ParseException
	 */
	void executeOpt(CommandLine cmd) throws Exception {
		Method method = this.getClass().getMethod(cmd.result.option.argName, String.class);
		method.invoke(this, cmd.result.value);
	}

	/**
	 * build command line options
	 * 
	 * @return
	 */
	CommandLine buildOptions() {
		CommandLine cmd = new CommandLine();
		Option icOption = Option
				.builder("ic")
				.longOpt("instance-create")
				.hasArg()
				.argName("ic")
				.desc(" Create a new app instance. \n    For example:\n        mmadmin -ic \"{app:\\\"hdp-weixinplatform\\\", instance:{ip: \\\"10.126.3.48\\\" , port: \\\"20000\\\" , jvm :\\\"\\\", ssl:\\\"false\\\" , enable:\\\"true\\\", remote:\\\"false\\\", sshServer:\\\"\\\" }}\"")
				.build();
		Option ilOption = Option.builder("il").longOpt("instance-launch").hasArg().argName("il").desc("Launch app instance.").build();
		Option idOption = Option.builder("id").longOpt("instance-delete").hasArg().argName("id")
				.desc(" Delete an app instance. \n    For example: \n        mmadmin -id \"app=hdp-weixinplatform, instance=10.126.3.48:20000\"").build();
		Option isOption = Option.builder("is").longOpt("instance-stop").hasArg().argName("is")
				.desc(" Stop the app instance. \n    For example : \n        mmadmin -is \"app=hdp-weixinplatform,instance=10.126.3.45:20000\"").build();
		Option imOption = Option
				.builder("im")
				.longOpt("instance-modify")
				.hasArg()
				.argName("im")
				.desc(" Modify the setting of an app instance. Attention:this command doesnot restart this modified instance. \n    For example :\n        mmadmin -im \"{app:\\\"hdp-weixinplatform\\\", edit:\\\"10.126.3.48:10002\\\", instance:{ip: \\\"10.126.3.48\\\" , port: \\\"20000\\\" , jvm :\\\"\\\", ssl:\\\"false\\\" , enable:\\\"true\\\", remote:\\\"false\\\", sshServer:\\\"\\\" }}\"")
				.build();
		Option irOption = Option.builder("ir").longOpt("instance-restart").hasArg().argName("ir")
				.desc(" Restart app instance. \n    For example :\n        mmadmin -ir \"app=hdp-weixinplatform,instance=10.126.3.45:20000\"").build();

		Option acOption = Option.builder("ac").longOpt("app-create").hasArg().argName("ac")
				.desc(" Upload and create a new app. Attention: this command doesnot create a app instance.").build();
		Option adOption = Option.builder("ad").longOpt("app-delete").hasArg().argName("ad")
				.desc(" Delete app and all of the instances of this app.\n    For example :\n        mmadmin -ad hdp-weixinplatform").build();

		Option alOption = Option.builder("al").longOpt("app-start").hasArg().argName("al").desc(" Start app.").build();
		Option arOption = Option.builder("ar").longOpt("app-restart").hasArg().argName("ar").desc(" Restart app.").build();
		Option asOption = Option.builder("as").longOpt("app-stop").hasArg().argName("as").desc(" Stop the specified app.").build();
		Option auOption = Option.builder("au").longOpt("app-update").hasArg().argName("au").desc(" Update app, then restart all of the instances of this app.")
				.build();
		Option stopOption = Option.builder("stop").longOpt("stop").hasArg(false).argName("stop").desc(" Stop hdp server.").build();

		// Create remote instance
		Option sshCreateOption = Option.builder("rc").longOpt("ssh-create").hasArg().argName("rc").desc(" Create ssh server.").build();

		// remove remote instance
		Option sshDeleteOption = Option.builder("rd").longOpt("ssh-delete").hasArg().argName("rd").desc(" Delete ssh server.").build();

		// modify remote instance
		Option sshModifyOption = Option.builder("rm").longOpt("ssh-modify").hasArg().argName("rm").desc(" Modify ssh server.").build();

		cmd.addOption(icOption);
		cmd.addOption(ilOption);
		cmd.addOption(idOption);
		cmd.addOption(isOption);
		cmd.addOption(imOption);
		cmd.addOption(irOption);
		cmd.addOption(acOption);
		cmd.addOption(adOption);
		cmd.addOption(alOption);
		cmd.addOption(arOption);
		cmd.addOption(asOption);
		cmd.addOption(auOption);
		cmd.addOption(sshCreateOption);
		cmd.addOption(sshDeleteOption);
		cmd.addOption(sshModifyOption);
		cmd.addOption(stopOption);
		return cmd;
	}

	/**
	 * load server configuration
	 */
	void loadHdpServerConfiguration() throws Exception {
		hdpbaseFolder = new File(System.getProperty(Constants.BASE_PROP));
		hdpHomeFolder = new File(System.getProperty(Constants.HOME_PROP));
		hdpAppsFolder = new File(hdpHomeFolder, "apps");

		File serverProperties = new File(hdpbaseFolder, "conf/server.properties");
		if (serverProperties.exists()) {
			Properties p = new Properties();
			p.load(new FileInputStream(serverProperties));
			this.hdpserver_ip = p.getProperty("rpc.ip");
			this.hdpserver_port = Integer.parseInt(p.getProperty("rpc.port"));
			this.hdpserver_ssl = Boolean.parseBoolean(p.getProperty("rpc.ssl"));
		}
	}

	/**
	 * print help
	 * 
	 * @param options
	 */
	void printUsage(CommandLine commandLine) {
		commandLine.printHelp();
	}

	/**
	 * print success
	 */
//	void printSuccess() {
//		System.out.println("Success!");
//	}
//
	void printError(String message) {
		System.out.println("Error: " + message);
	}
	
	void printResult(String result) {
		Message message = JsonUtils.String2Bean(result, Message.class);
		if (!message.isSuccess() && !StringUtils.isEmpty(message.getMessage())) {
			printError(message.getMessage());
		}
		else if (!message.isSuccess()) {
			printError("cannot execute command!");
		} else if (message.isSuccess() && !StringUtils.isEmpty(message.getMessage())) {
			System.out.println(message.getMessage());
		} else  {
			System.out.println("Command execute successfully");
		} 
	}

	/**
	 * create app instance 
	 */
	public void ic(String params) throws Exception {
		if (StringUtils.isEmpty(params))
			throw new Exception(" Must specified [app] and [instance] parameters.");

		JSONObject jo = JsonUtils.String2JSONObject(params);
		final String app = jo.getString("app");
		final JSONObject joInstance = jo.getJSONObject("instance");

		IceClient iceClient = new IceClient(this.hdpserver_ip, this.hdpserver_port, this.hdpserver_ssl);
		iceClient.invoke(AppService.class, new Action() {
			@Override
			public void execute(Object prx) {
				AppServicePrx appServicePrx = (AppServicePrx) prx;
				String result = appServicePrx.createAppInstance(app, joInstance.toString());
				printResult(result);
			}
		});
	}
	
	/**
	 * start app instance 
	 */
	public void il(String params) throws Exception {
		if (StringUtils.isEmpty(params))
			throw new Exception(" Must specified [app] and [instance] parameters.");

		JSONObject jo = JsonUtils.String2JSONObject(params);
		final String app = jo.getString("app");
		final String instanceId = jo.getString("instance");

		IceClient iceClient = new IceClient(this.hdpserver_ip, this.hdpserver_port, this.hdpserver_ssl);
		iceClient.invoke(AppService.class, new Action() {
			@Override
			public void execute(Object prx) {
				AppServicePrx appServicePrx = (AppServicePrx) prx;
				String result = appServicePrx.startAppInstance(app, instanceId);
				printResult(result);
			}
		});
	}

	/**
	 * delete instance 
	 */
	public void id(String params) throws Exception {
		if (StringUtils.isEmpty(params))
			throw new Exception(" Must specified [app] and [instance] parameters.");

		JSONObject jo = JsonUtils.String2JSONObject(params);
		final String app = jo.getString("app");
		final String instanceId = jo.getString("instance");

		if (StringUtils.isEmpty(app) || StringUtils.isEmpty(instanceId)) {
			throw new Exception(" For example : mmadmin -id \"app=hdp-weixinplatform,instance=10.126.3.45:20000\" ");
		}

		final String p1 = app, p2 = instanceId;
		IceClient iceClient = new IceClient(this.hdpserver_ip, this.hdpserver_port, this.hdpserver_ssl);
		iceClient.invoke(AppService.class, new Action() {
			@Override
			public void execute(Object prx) {
				AppServicePrx appServicePrx = (AppServicePrx) prx;
				String result = appServicePrx.removeAppInstance(p1, p2);
				printResult(result);
			}
		});
	}

	/**
	 * stop instance
	 */
	public void is(String params) throws Exception {
		if (StringUtils.isEmpty(params))
			throw new Exception(" Must specified [app] and [instance] parameters.");

		JSONObject jo = JsonUtils.String2JSONObject(params);
		final String app = jo.getString("app");
		final String instanceId = jo.getString("instance");

		if (StringUtils.isEmpty(app) || StringUtils.isEmpty(instanceId)) {
			throw new Exception(" For example : mmadmin -is \"app=hdp-weixinplatform,instance=10.126.3.45:20000\" ");
		}

		final String p1 = app, p2 = instanceId;
		IceClient iceClient = new IceClient(this.hdpserver_ip, this.hdpserver_port, this.hdpserver_ssl);
		iceClient.invoke(AppService.class, new Action() {
			@Override
			public void execute(Object prx) {
				AppServicePrx appServicePrx = (AppServicePrx) prx;
				String result = appServicePrx.stopAppInstance(p1, p2);
				printResult(result);
			}
		});
	}

	/**
	 * restart instance
	 */
	public void ir(String params) throws Exception {
		if (StringUtils.isEmpty(params))
			throw new Exception(" Must specified [app] and [instance] parameters.");

		JSONObject jo = JsonUtils.String2JSONObject(params);
		final String app = jo.getString("app");
		final String instanceId = jo.getString("instance");

		if (StringUtils.isEmpty(app) || StringUtils.isEmpty(instanceId)) {
			throw new Exception(" For example : mmadmin -ir \"app=hdp-weixinplatform,instance=10.126.3.45:20000\" ");
		}

		final String p1 = app, p2 = instanceId;
		IceClient iceClient = new IceClient(this.hdpserver_ip, this.hdpserver_port, this.hdpserver_ssl);
		iceClient.invoke(AppService.class, new Action() {
			@Override
			public void execute(Object prx) {
				AppServicePrx appServicePrx = (AppServicePrx) prx;
				String result = appServicePrx.restartAppInstance(p1, p2);
				printResult(result);
			}
		});
	}

	/**
	 * modify instance configuration
	 */
	public void im(String params) throws Exception {
		if (StringUtils.isEmpty(params))
			throw new Exception(" Must specified [app] and [instance] parameters.");

		JSONObject jo = JsonUtils.String2JSONObject(params);
		final String app = jo.getString("app");
		final String instanceId = jo.getString("edit");
		final JSONObject joInstance = jo.getJSONObject("instance");

		IceClient iceClient = new IceClient(this.hdpserver_ip, this.hdpserver_port, this.hdpserver_ssl);
		iceClient.invoke(AppService.class, new Action() {
			@Override
			public void execute(Object prx) {
				AppServicePrx appServicePrx = (AppServicePrx) prx;
				String result = appServicePrx.updateAppInstance(app, instanceId, joInstance.toString());
				printResult(result);
			}
		});
	}

	/**
	 * stop all the instances of a app
	 */
	public void as(String params) throws Exception {
		if (StringUtils.isEmpty(params)) {
			throw new Exception(" Must specified [app]  parameters.\n For example : \n mmadmin -as hdp-weixinplatform");
		}

		final String app = params;
		IceClient iceClient = new IceClient(this.hdpserver_ip, this.hdpserver_port, this.hdpserver_ssl);

		iceClient.invoke(AppService.class, new Action() {
			@Override
			public void execute(Object prx) {
				AppServicePrx appServicePrx = (AppServicePrx) prx;
				String result = appServicePrx.stopApp(app);
				printResult(result);
			}
		});
	}

	/**
	 * restart app
	 */
	public void ar(String params) throws Exception {
		if (StringUtils.isEmpty(params)) {
			throw new Exception(" Must specified [app]  parameters.\n For example : \n mmadmin -ar hdp-weixinplatform");
		}

		final String app = params;
		IceClient iceClient = new IceClient(this.hdpserver_ip, this.hdpserver_port, this.hdpserver_ssl);
		iceClient.invoke(AppService.class, new Action() {
			@Override
			public void execute(Object prx) {
				AppServicePrx appServicePrx = (AppServicePrx) prx;
				String result = appServicePrx.restartApp(app);
				printResult(result);
			}
		});
	}

	/**
	 * start app
	 */
	public void al(String params) throws Exception {
		if (StringUtils.isEmpty(params)) {
			throw new Exception(" Must specified [app]  parameters.\n For example : \n mmadmin -ar hdp-weixinplatform");
		}

		final String app = params;
		IceClient iceClient = new IceClient(this.hdpserver_ip, this.hdpserver_port, this.hdpserver_ssl);
		iceClient.invoke(AppService.class, new Action() {
			@Override
			public void execute(Object prx) {
				AppServicePrx appServicePrx = (AppServicePrx) prx;
				String result = appServicePrx.startApp(app);
				printResult(result);
			}
		});
	}

	/**
	 * remove app and all of its instances
	 */
	public void ad(String params) throws Exception {
		if (StringUtils.isEmpty(params)) {
			throw new Exception(" Must specified [app]  parameters.\n For example : \n mmadmin -ad hdp-weixinplatform");
		}

		final String app = params;
		IceClient iceClient = new IceClient(this.hdpserver_ip, this.hdpserver_port, this.hdpserver_ssl);
		iceClient.invoke(AppService.class, new Action() {
			@Override
			public void execute(Object prx) {
				AppServicePrx appServicePrx = (AppServicePrx) prx;
				String result = appServicePrx.removeApp(app);
				printResult(result);
			}
		});
	}

	/**
	 * create app without startup it.
	 */
	public void ac(String params) throws Exception {
		if (StringUtils.isEmpty(params)) {
			throw new Exception("must specified [file] parameters.\nFor example :\n hdmanager /home/app.jar");
		}

		File file = new File(params);
		if (!file.exists()) {
			throw new Exception("cannot find app file: " + params);
		}

		String filename = file.getName();
		App app = new App();
		AppManager.parseAppModeXml(app, file);
		final String appid = app.getAppid();
		File appFolder = new File(hdpAppsFolder, appid);
		if (!appFolder.exists()) {
			appFolder.mkdirs();
		}

		if (filename.endsWith(".jar") && file.isFile()) {
			ZipUtils.unzip(file.getCanonicalPath(), appFolder.getCanonicalPath());
		} else if (file.isDirectory()) {
			FileUtils.copyFiles2(file.getCanonicalPath(), appFolder.getCanonicalPath());
		}
		
		// create an app in server
		IceClient iceClient = new IceClient(this.hdpserver_ip, this.hdpserver_port, this.hdpserver_ssl);
		iceClient.invoke(AppService.class, new Action() {
			@Override
			public void execute(Object prx) {
				AppServicePrx appServicePrx = (AppServicePrx) prx;
				String result = appServicePrx.createApp(appid);
				printResult(result);
			}
		});
	}

	/**
	 * Create ssh server.
	 */
	public void rc(String params) throws Exception {
		if (StringUtils.isEmpty(params)) {
			throw new Exception(
					" Must provide ssh json parameters.\n For example : \n ./mmadmin -rc \"{name:\\\'48\\\',ip:\\\'10.126.3.48\\\',port:20000,account:\\\'root\\\',password:\\\'jianghulu521\\\'}\"");
		}

		final String serverJson = params;
		IceClient iceClient = new IceClient(this.hdpserver_ip, this.hdpserver_port, this.hdpserver_ssl);
		iceClient.invoke(SSHServerService.class, new Action() {
			@Override
			public void execute(Object prx) {
				SSHServerServicePrx sshServicePrx = (SSHServerServicePrx) prx;
				String result = sshServicePrx.addServer(serverJson);
				printResult(result);
			}
		});
	}

	/**
	 * Delete ssh server.
	 */
	public void rd(String params) throws Exception {
		if (StringUtils.isEmpty(params)) {
			throw new Exception(" Must specified [key]  parameters.\n For example : \n mmadmin -ssh-delete ABS3LKSIUWWEUWIEOWWE2B8");
		}

		final String key = params;
		IceClient iceClient = new IceClient(this.hdpserver_ip, this.hdpserver_port, this.hdpserver_ssl);
		iceClient.invoke(SSHServerService.class, new Action() {
			@Override
			public void execute(Object prx) {
				SSHServerServicePrx sshServicePrx = (SSHServerServicePrx) prx;
				String result = sshServicePrx.removeServer(key);
				printResult(result);
			}
		});
	}

	/**
	 * Modify ssh server.
	 */
	public void rm(String params) throws Exception {
		if (StringUtils.isEmpty(params)) {
			throw new Exception(" Must provide ssh json parameters.\n For example : \n mmadmin --ssh-modify \"{key :\"\", name:\"\", ip:\"\" }\"");
		}

		final JSONObject jo = JsonUtils.String2JSONObject(params);
		if (StringUtils.isEmpty(jo.getString("key")))
			throw new Exception("Must provide key property in json");

		IceClient iceClient = new IceClient(this.hdpserver_ip, this.hdpserver_port, this.hdpserver_ssl);
		iceClient.invoke(SSHServerService.class, new Action() {
			@Override
			public void execute(Object prx) {
				SSHServerServicePrx sshServicePrx = (SSHServerServicePrx) prx;
				String result = sshServicePrx.updateServer(jo.getString("key"), jo.toString());
				printResult(result);
			}
		});
	}

	/**
	 * stop the server
	 * 
	 * @throws Excpetion
	 */
	public void stop(String params) throws Exception {
		IceClient iceClient = new IceClient(this.hdpserver_ip, this.hdpserver_port, this.hdpserver_ssl);
		iceClient.invoke(AppService.class, new Action() {
			@Override
			public void execute(Object prx) {
				AppServicePrx appServicePrx = (AppServicePrx) prx;
				appServicePrx.stopServer();
				System.out.println("Server will stop in 5 seconds!");
			}
		});
	}
}

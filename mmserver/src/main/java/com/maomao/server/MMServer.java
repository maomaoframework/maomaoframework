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
package com.maomao.server;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.maomao.framework.configuration.SysConfiguration;
import com.maomao.framework.service.MaoMaoService;
import com.maomao.framework.support.rpc.ice.IceClient;
import com.maomao.framework.utils.StringUtils;
import com.maomao.server.event.ServerEvent;
import com.maomao.server.event.ServerEventFacotory;
import com.maomao.server.event.ServerEventRunner;
import com.maomao.server.event.ShutdownEvent;
import com.maomao.server.support.rpc.IRPCServer;
import com.maomao.server.support.rpc.RPCServerFactory;
import com.maomao.server.support.ssh.SSHServer;
import com.maomao.server.support.ssh.SSHServerManager;
import com.maomao.server.util.ProcessManager;

/**
 * hdp服务器类
 * 
 * @author maomao
 * 
 */
public class MMServer implements IMMServer {
	static Logger logger = LoggerFactory.getLogger(MMServer.class);

	// 保存了服务的长类名
	ClassLoader appClassLoader;

	ApplicationContext applicationContext;

	// 服务器配置文件
	String serverConfig = "conf/server.properties";

	String logConfig = "conf/logging.properties";

	AppManager appManager;

	@Override
	public void init() {
		// 加载spring配置文件
		this.applicationContext = new ClassPathXmlApplicationContext("spring.xml");

		try {
			appManager = new AppManager();
			appManager.init();
		} catch (Exception e) {
			logger.error("Apps parse error. Ignore this err and continue to start.", e);
		}
	}

	/**
	 * 启动服务器
	 */
	@Override
	public void start() {
		// 启动主程序监听
		// 将所有接口注入到thrift容器中
		// 取得所有具有接口
		Map<String, Object> objects = getApplicationContext().getBeansWithAnnotation(MaoMaoService.class);
		IRPCServer server = RPCServerFactory.createSliceServer();
		server.setPort(getPort());
		server.setServices(objects.values());
		server.start();

		// 启动应用
		if (this.appManager.getApps() != null) {
			for (App app : this.appManager.getApps()) {
				// 加载模块配置文件
				try {
					_startApp_(app);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 启动应用
	 * 
	 * @param file
	 * @throws Exception
	 */
	void _startApp_(App app) throws Exception {
		logger.info("　　Starting app : " + app.getAppid() + " ...");

		List<AppInstance> instances = app.getInstances();
		if (instances != null) {
			for (AppInstance instance : instances) {
				// 如果不是自启动的，则启动时不启动该应用
				if (!instance.isEnable())
					continue;

				if (instance.isRemote()) {
					startRemoteAppInstance(app, instance);
				} else {
					startLocalAppInstance(app, instance);
				}
			}
		}
	}

	/**
	 * 启动一个App实例
	 */
	public void startRemoteAppInstance(App app, AppInstance instance) throws Exception {
		// 取得SSH服务器
		SSHServer server = SSHServerManager.getInstance().getServerById(instance.getSshServer());
		if (server != null) {
			RemoteInstanceHelper.start(server, app, instance, false);
		} else {
			throw new Exception("Cannot find remote ssh server.");
		}
	}

	/**
	 * 发布远程服务器
	 */
	public void deployRemoteAppInstance(App app, AppInstance instance) throws Exception {
		// 取得SSH服务器
		SSHServer server = SSHServerManager.getInstance().getServerById(instance.getSshServer());
		if (server != null) {
			RemoteInstanceHelper.deploy(server, app, instance, false);
		} else {
			throw new Exception("Cannot find remote ssh server.");
		}
	}

	/**
	 * 启动本地应用实例
	 * 
	 * @param appFolder
	 * @param app
	 * @param instance
	 */
	public void startLocalAppInstance(App app, AppInstance instance) throws Exception {
		// 取得应用所在目录
		String serverIp = SysConfiguration.getProperty("rpc.ip");
		int serverPort = Integer.parseInt(SysConfiguration.getProperty("rpc.port"));

		String appHome = app.getDocBase();
		String classpath = System.getProperty("java.class.path");
		String jvmParams = StringUtils.isEmpty(instance.getJvm()) ? "" : instance.getJvm();
		String hdpHome = Main.getServerHome();
		String hdpBase = Main.getServerBase();

		String command = "java " + jvmParams + " -Dfile.encoding=UTF-8" + " -classpath " + classpath + " -D" + Constants.HOME_PROP + "=" + hdpHome + " -D"
				+ Constants.BASE_PROP + "=" + hdpBase + " -Dapp.port=" + instance.getPort() + " -Dapp.ip=" + instance.getIp() + " -Dserver.port=" + serverPort
				+ " -Dserver.ip=" + serverIp + " -Dprototype=appserver" + " -Dlog4j.configuration=file:" + getLogConfigFile().getCanonicalPath()
				+ " -Dapp.home=" + appHome + " " + Constants.PK + ".startup.Bootstrap start";

		ProcessManager.getInstance().createProcess(app.getAppid(), command);
	}

	/**
	 * 关闭一个App实例
	 */
	public void shutdownAppInstance(AppInstance instance) {
		IceClient client = new IceClient(instance.getIp(), instance.getPort(), instance.isSsl());

		// 向指定的服务器发送停止命令
		ServerEvent shutdownEvent = ServerEventFacotory.getInstance().getEvent(ShutdownEvent.class);
		ServerEventRunner runner = new ServerEventRunner(shutdownEvent, client);
		runner.post();

		instance.setRunningStatus(AppInstance.STATUS_STOP);
	}

	/**
	 * 关闭一个App实例
	 */
	public void restartAppInstance(final App app, final AppInstance instance) {
		IceClient client = new IceClient(instance.getIp(), instance.getPort(), instance.isSsl());

		// 向指定的服务器发送停止命令
		ServerEvent shutdownEvent = ServerEventFacotory.getInstance().getEvent(ShutdownEvent.class);
		ServerEventRunner runner = new ServerEventRunner(shutdownEvent, client);
		runner.post();

		instance.setRunningStatus(AppInstance.STATUS_STOP);

		// 创建一个启动线程
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(6 * 1000);

					// 启动应用
					if (!instance.isRemote()) {
						startLocalAppInstance(app, instance);
					} else
						startRemoteAppInstance(app, instance);
				} catch (Exception e) {

				}
			}
		}).start();
	}

	@Override
	public void stop() {
		ProcessManager.getInstance().close();
	}

	@Override
	public int getPort() {
		return Integer.parseInt(SysConfiguration.getProperty("rpc.port"));
	}

	@Override
	public String getIp() {
		return SysConfiguration.getProperty("rpc.ip");
	}

	@Override
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public String getServerConfig() {
		return serverConfig;
	}

	public void setServerConfig(String serverConfig) {
		this.serverConfig = serverConfig;
	}

	public String getLogConfig() {
		return logConfig;
	}

	public void setLogConfig(String logConfig) {
		this.logConfig = logConfig;
	}

	/**
	 * 取得日志文件
	 * 
	 * @return
	 */
	public File getLogConfigFile() {
		File file = new File(logConfig);
		if (!file.isAbsolute()) {
			file = new File(System.getProperty(Constants.BASE_PROP), logConfig);
		}
		return (file);
	}

	/**
	 * 取得服务器配置
	 * 
	 * @return
	 */
	public File getServerConfigFile() {
		File file = new File(serverConfig);
		if (!file.isAbsolute()) {
			file = new File(System.getProperty(Constants.BASE_PROP), serverConfig);
		}
		return (file);
	}

	@Override
	public boolean supportManager() {
		return true;
	}

	public AppManager getAppManager() {
		return appManager;
	}

	public void setAppManager(AppManager appManager) {
		this.appManager = appManager;
	}
}

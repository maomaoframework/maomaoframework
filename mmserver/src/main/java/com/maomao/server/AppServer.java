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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.maomao.framework.service.MaoMaoService;
import com.maomao.server.event.ServerEventFacotory;
import com.maomao.server.plugin.PluginFactory;
import com.maomao.server.support.rpc.IRPCServer;
import com.maomao.server.support.rpc.RPCServerFactory;

/**
 * App Server
 * 
 * @author maomao
 * 
 */
public class AppServer extends AbstractServer {
	private static Logger logger = LoggerFactory.getLogger(AppServer.class);

	// the app port
	private int port;

	// the app ip
	private String ip;

	// the server's ip
	private String serverIp;

	// the server's port
	private int serverPort;

	// if use ssl
	private boolean serverSsl = false;

	App app;

	String appHome;


	public AppServer() {
	}

	@Override
	public void init() {
		// loading server configuration
		super.init();
		
		appHome = System.getProperty("app.home");

		try {
			app = new App();
			app.setDocBase(appHome);
			AppManager.parseAppModeXml(app, new File(appHome));
			ip = System.getProperty("app.ip");
			port = Integer.parseInt(System.getProperty("app.port"));

			serverPort = Integer.parseInt(System.getProperty("server.port"));
			serverIp = System.getProperty("server.ip");

			try {
				serverSsl = Integer.parseInt(System.getProperty("server.ssl")) == 1;
			} catch (Exception e) {

			}

			// load spring xml configuration
			applicationContext = new ClassPathXmlApplicationContext(new String[] { "spring-start-app.xml", "spring-app.xml" });

			// init plugin factory;
			pluginFactory = new PluginFactory(this);
			pluginFactory.init();

		} catch (Exception e) {
			logger.error("Parse app" + appHome + " spring-app.xml error: ", e);
			e.printStackTrace();
			System.exit(-1);
		}

	}

	@Override
	public void start() {
		try {
			pluginFactory.beforeStart();

			_start_();

			ServerEventFacotory.getInstance();

			// after startup, synchronize the status between server and app
			// server
			ServerSynchronizer.start();
			logger.info("--- App " + appHome + " start finish!");
		} catch (Exception e) {
			logger.error("App startup failed : ", e);
			e.printStackTrace();
			System.exit(-1);
		}
	}

	void _start_() throws Exception {
		Map<String, Object> objects = applicationContext.getBeansWithAnnotation(MaoMaoService.class);

		Collection<Object> services = objects.values();
		Collection<Object> avaliableService = new ArrayList<Object>();

		try {
			if (null != services) {
				for (Object o : services) {
					if (o.getClass().getName().startsWith(app.getPk())) {
						continue;
					}
					avaliableService.add(o);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		IRPCServer rpcServer = RPCServerFactory.createSliceServer();
		rpcServer.setPort(port);
		rpcServer.setServices(objects.values());
		rpcServer.start();
	}

	@Override
	public void stop() {
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public App getApp() {
		return app;
	}

	public void setApp(App app) {
		this.app = app;
	}

	public String getAppHome() {
		return appHome;
	}

	public void setAppHome(String appHome) {
		this.appHome = appHome;
	}

	public boolean isServerSsl() {
		return serverSsl;
	}

	public void setServerSsl(boolean serverSsl) {
		this.serverSsl = serverSsl;
	}

	@Override
	public boolean supportManager() {
		return false;
	}

}

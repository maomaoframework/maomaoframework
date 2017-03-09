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
import java.io.FileFilter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.maomao.framework.service.MaoMaoService;
import com.maomao.server.plugin.PluginFactory;
import com.maomao.server.support.rpc.IRPCServer;
import com.maomao.server.support.rpc.RPCServerFactory;
import com.maomao.startup.Constants;

/**
 * Singleton server is used to debug. The singleton server run every app in the
 * same process, so that we can use debug our apps in development mode.
 * 
 * @author maomao
 * 
 */
public class SingletonServer extends AbstractServer {
	static Logger logger = LoggerFactory.getLogger(SingletonServer.class);

	String serverConfig = "conf/server.properties";

	String logConfig = "conf/logging.properties";

	Set<Object> avaliableServices = new HashSet<>();

	URL[] appLoaderUrls;

	AppManager appManager;

	@Override
	public void init() {
		super.init();

		// load spring.xml
		this.applicationContext = new ClassPathXmlApplicationContext("spring.xml");

		// init plugin factory;
		pluginFactory = new PluginFactory(this);
		pluginFactory.init();

		try {
			appManager = new AppManager();
			appManager.init();
		} catch (Exception e) {
			logger.error("Apps parse error. Ignore this err and continue to start.", e);
		}
	}

	/**
	 * start server
	 */
	@Override
	public void start() {
		if (this.getAppManager().getApps() != null) {
			// load app url
			for (App app : this.getAppManager().getApps()) {
				try {
					final URL[] urls = loadAppLibUrls(app);


					// craate a classload from these urls
					ClassLoader appClassloader = AccessController.doPrivileged(new PrivilegedAction<URLClassLoader>() {
						@Override
						public URLClassLoader run() {
							return new URLClassLoader(urls , Thread.currentThread().getContextClassLoader());
						}
					});
					
					// start app
					_start_app_(appClassloader);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}

		// load main services
		Collection<Object> avaliableService = loadServices(applicationContext);
		RPCServerFactory.startDefault(avaliableService, serverConfiguration.getRpc().getPort());
		logger.error("Server started!");
	}

	/**
	 * load all services;
	 */
	Collection<Object> loadServices(ApplicationContext context) {
		Map<String, Object> objects = context.getBeansWithAnnotation(MaoMaoService.class);
		Collection<Object> services = objects.values();
		Collection<Object> avaliableService = new ArrayList<Object>();
		try {
			if (null != services) {
				for (Object o : services) {
					avaliableService.add(o);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return avaliableService;
	}

	void _start_app_(final ClassLoader cl) {
		ExecutorService service = Executors.newCachedThreadPool();

		int appCount = appManager.getApps().size();
		final CountDownLatch cdOrder = new CountDownLatch(appCount);
		final CountDownLatch cdAnswer = new CountDownLatch(appCount);
		for (final App app : appManager.getApps()) {
			Runnable runnable = new Runnable() {
				public void run() {
					try {
						cdOrder.await();
						Thread.currentThread().setContextClassLoader(cl);
						ClassPathXmlApplicationContext appCtx = new ClassPathXmlApplicationContext("spring-app.xml");

						Collection<Object> avaliableService = loadServices(appCtx);

						AppInstance ai = app.getInstances().get(0);
						int port = getAvaliablePort();
						ai.setPort(port);
						ai.setIp(getIp());

						IRPCServer rpcServer = RPCServerFactory.createSliceServer();
						rpcServer.setPort(port);
						rpcServer.setServices(avaliableService);
						rpcServer.start();
						cdAnswer.countDown();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						cdAnswer.countDown();
					}
				}
			};
			service.execute(runnable);
		}

		try {
			cdOrder.countDown();
			cdAnswer.await();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	@Override
	public void stop() {
		System.exit(1);
	}

	@Override
	public int getPort() {
		return serverConfiguration.getRpc().getPort();
	}

	@Override
	public String getIp() {
		return serverConfiguration.getRpc().getIp();
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

	public File getLogConfigFile() {
		File file = new File(logConfig);
		if (!file.isAbsolute()) {
			file = new File(System.getProperty(Constants.BASE_PROP), logConfig);
		}
		return (file);
	}

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

	/**
	 * get avaliable port
	 * 
	 * @return
	 */
	public int getAvaliablePort() {
		int avaliablePort = -1;
		int portStart = serverConfiguration.getRpc().getPort();
		portStart++;

		for (int i = portStart; i < 65535; i++) {
			ServerSocket ss = null;
			try {
				ss = new ServerSocket(i);
				avaliablePort = i;
				break;
			} catch (IOException e) {
			} finally {
				try {
					if (ss != null) {
						ss.close();
					}
				} catch (Exception e) {
				}
			}
		}
		return avaliablePort;
	}

	/**
	 * load app libraries
	 * 
	 * @return
	 */
	URL[] loadAppLibUrls(App app) throws Exception {
		final List<URL> urls = new ArrayList<URL>();
		File appFolder = new File(app.getDocBase());
		File libFolder = new File(appFolder, "lib");
		urls.add(appFolder.toURI().toURL());

		libFolder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				if (file.getName().endsWith(".jar")) {
					try {
						urls.add(file.toURI().toURL());
					} catch (Exception e) {
						logger.error("Error:" , e);
					}
				}
				return false;
			}
		});

		return urls.toArray(new URL[0]);

	}
}

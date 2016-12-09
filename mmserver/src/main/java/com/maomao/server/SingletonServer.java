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

import com.maomao.framework.configuration.SysConfiguration;
import com.maomao.framework.service.MaoMaoService;
import com.maomao.server.support.rpc.RPCServerFactory;
import com.maomao.startup.Constants;

/**
 * Singleton server is used to debug. The singleton server run every app in the
 * same process, so that we can use debug our apps in development mode.
 * 
 * @author maomao
 * 
 */
public class SingletonServer implements IHdpServer {
	static Logger logger = LoggerFactory.getLogger(SingletonServer.class);

	ClassLoader appClassLoader;

	ApplicationContext applicationContext;
	String serverConfig = "conf/server.properties";

	String logConfig = "conf/logging.properties";

	Set<Object> avaliableServices = new HashSet<>();

	URL[] appLoaderUrls;

	AppManager appManager;

	@Override
	public void init() {
		// load spring.xml
		this.applicationContext = new ClassPathXmlApplicationContext("spring.xml");

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
			parseAppUrl();
			loadAppServices();
		}

		RPCServerFactory.startDefault(avaliableServices);
		logger.error("Server started!");
	}

	/**
	 * Parse app location
	 */
	void parseAppUrl() {
		// load app url
		List<URL> murl = new ArrayList<URL>();
		for (App app : this.getAppManager().getApps()) {
			try {
				murl.add(new File(app.getDocBase()).toURI().toURL());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		appLoaderUrls = murl.toArray(new URL[0]);

		// craate a classload from these urls
		appClassLoader = AccessController.doPrivileged(new PrivilegedAction<URLClassLoader>() {
			@Override
			public URLClassLoader run() {
				return new URLClassLoader(appLoaderUrls, Thread.currentThread().getContextClassLoader());
			}
		});
	}

	/**
	 * load all these services
	 */
	void loadAppServices() {
		ExecutorService service = Executors.newCachedThreadPool();

		int appCount = appManager.getApps().size();
		final CountDownLatch cdOrder = new CountDownLatch(appCount);
		final CountDownLatch cdAnswer = new CountDownLatch(appCount);
		for (final App app : appManager.getApps()) {
			Runnable runnable = new Runnable() {
				public void run() {
					try {
						cdOrder.await();
						Thread.currentThread().setContextClassLoader(appClassLoader);
						ClassPathXmlApplicationContext appCtx = new ClassPathXmlApplicationContext("spring-app.xml");
						mergeService(app, appCtx);
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

	/**
	 * merge services
	 * 
	 * @param app
	 * @param context
	 */
	void mergeService(App app, ApplicationContext context) {
		Map<String, Object> objects = context.getBeansWithAnnotation(MaoMaoService.class);
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
		avaliableServices.addAll(objects.values());
	}

	@Override
	public void stop() {
		System.exit(1);
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

}

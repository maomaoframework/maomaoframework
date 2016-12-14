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

import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.maomao.server.config.ServerConfiguration;
import com.maomao.server.plugin.PluginFactory;

/**
 * @author maomao
 * 
 */
public abstract class AbstractServer implements IMMServer {
	public static final String logConfig = "conf/logging.properties";
	public static final String serverConfig = "conf/server.xml";

	ApplicationContext applicationContext;
	ServerConfiguration serverConfiguration;
	PluginFactory pluginFactory;
	ThreadPoolTaskExecutor executor;

	public void init() {
		// read the server configuration
		initServerConfiguration();

		// create thread pool
		initThreadPoolTaskExecutor();
	}

	@Override
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public PluginFactory getPluginFactory() {
		return pluginFactory;
	}

	public ServerConfiguration getServerConfiguration() {
		return serverConfiguration;
	}

	private void initServerConfiguration() {
		File configFile = new File(Main.getServerBaseFolder(), serverConfig);
		serverConfiguration = ServerConfiguration.load(configFile);
	}

	private void initThreadPoolTaskExecutor() {
		if (null != executor) {
			return;
		}

		executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(serverConfiguration.getTaskPool().getCorePoolSize());
		executor.setMaxPoolSize(serverConfiguration.getTaskPool().getMaxPoolSize());
		executor.setQueueCapacity(serverConfiguration.getTaskPool().getQueueCapacity());
		executor.setKeepAliveSeconds(serverConfiguration.getTaskPool().getAliveSeconds());
		executor.afterPropertiesSet();
	}
	
	public ThreadPoolTaskExecutor getThreadPoolTaskExecutor(){
		return this.executor;
	}
}

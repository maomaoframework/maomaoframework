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
package com.maomao.server.plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maomao.server.IMMServer;
import com.maomao.server.config.PluginElement;
import com.maomao.server.config.ServerConfiguration;

/**
 * Plugin Factory
 * 
 * @author maomao
 * 
 */
@SuppressWarnings("unchecked")
public class PluginFactory {
	static Logger logger = LoggerFactory.getLogger(PluginFactory.class);
	Map<String, IPlugin> m_pluginsWithName;
	Map<Class<?>, IPlugin> m_pluginWithCClass;

	IMMServer server;

	public PluginFactory(IMMServer server) {
		this.server = server;
		m_pluginsWithName = new HashMap<String, IPlugin>();
		m_pluginWithCClass = new HashMap<Class<?>, IPlugin>();
	}

	/**
	 * Init factory
	 */
	public void init() {
		// load plugins.
		try {
			ServerConfiguration serverConfigruation = server.getServerConfiguration();
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			List<PluginElement> plugins = serverConfigruation.getPlugins();

			String className;
			Class<?> clazz;
			Object plugin;
			for (PluginElement p : plugins) {
				// load every plugin
				className = p.getClassimpl();
				clazz = classLoader.loadClass(className);
				plugin = clazz.newInstance();
				m_pluginsWithName.put(p.getName(), (IPlugin) plugin);
				m_pluginWithCClass.put(plugin.getClass(), (IPlugin) plugin);
			}
		} catch (Exception e) {
			logger.error("Error :", e);
		}
	}

	public <T> T getPlugin(Class<T> clazz) {
		return (T) m_pluginWithCClass.get(clazz);
	}

	public void beforeStart() {
		for (Entry<String, IPlugin> entry : m_pluginsWithName.entrySet()) {
			entry.getValue().beforeStart();
		}
	}
}

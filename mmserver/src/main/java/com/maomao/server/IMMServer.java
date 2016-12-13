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

import org.springframework.context.ApplicationContext;

import com.maomao.server.config.ServerConfiguration;
import com.maomao.server.plugin.PluginFactory;
import com.maomao.startup.IServer;

/**
 * the server interface
 * 
 * @author maomao
 * 
 */
public interface IMMServer extends IServer {

	/**
	 * support manager
	 * 
	 * @return
	 */
	boolean supportManager();

	/**
	 * get application context
	 * 
	 * @return
	 */
	ApplicationContext getApplicationContext();

	/**
	 * Get plugin factory
	 * @return
	 */
	PluginFactory getPluginFactory();
	
	
	/**
	 * get Server configurations
	 * @return
	 */
	ServerConfiguration getServerConfiguration();
	
}

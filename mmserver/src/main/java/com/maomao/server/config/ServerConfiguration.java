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
package com.maomao.server.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

/**
 * @author maomao
 * 
 */
public class ServerConfiguration {
	RpcElement rpc;
	List<PluginElement> plugins;

	public static ServerConfiguration load(File configFile) {
		ServerConfiguration config = null;
		if (configFile.exists()) {
			XStream x = new XStream();
			x.alias("server-config", ServerConfiguration.class);
			x.alias("rpc", RpcElement.class);
			x.alias("plugins", ArrayList.class);
			x.alias("plugin", PluginElement.class);
			config = (ServerConfiguration) x.fromXML(configFile);
		}
		return config;
	}

	public RpcElement getRpc() {
		return rpc;
	}

	public void setRpc(RpcElement rpc) {
		this.rpc = rpc;
	}

	public List<PluginElement> getPlugins() {
		return plugins;
	}

	public void setPlugins(List<PluginElement> plugins) {
		this.plugins = plugins;
	}
	
	
}

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry
 * 
 * @author maomao
 * 
 */
public class Main {
	static Logger logger = LoggerFactory.getLogger(Main.class);

	public static final String INDEFINER_DPSERVER = "hdpserver";
	public static final String INDEFINER_APPSERVER = "appserver";
	public static final String INDEFINER_SINGLETON = "singleton";

	static IHdpServer server;

	public static IHdpServer start(String prototype) {
		if (server != null)
			return server;

		if (INDEFINER_DPSERVER.equals(prototype)) {
			// Start mmserver
			server = new HdpServer();
		} else if (INDEFINER_APPSERVER.equals(prototype)) {
			// Start app server
			server = new AppServer();
		} else if (INDEFINER_SINGLETON.equals(prototype)) {
			// Start singleton server
			server = new SingletonServer();
		}

		return server;
	}

	public static IHdpServer getServer() {
		return server;
	}

	/**
	 * Get home enviroment var.
	 * 
	 * @return
	 */
	public static String getServerHome() {
		return System.getProperty(Constants.HOME_PROP, System.getProperty(Constants.HOME_PROP));
	}

	/**
	 * Get base enviroment var.
	 * 
	 * @return
	 */
	public static String getServerBase() {
		return System.getProperty(Constants.BASE_PROP, System.getProperty(Constants.BASE_PROP));
	}

	/**
	 * Get base folder
	 * 
	 * @return
	 */
	public static File getServerBaseFolder() {
		return new File(getServerBase());
	}

	/**
	 * Get home Folder.
	 * 
	 * @return
	 */
	public static File getServerHomeFolder() {
		return new File(getServerHome());
	}
}

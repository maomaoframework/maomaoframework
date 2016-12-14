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
package com.maomao.server.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maomao.framework.support.rpc.ice.IceClient;
import com.maomao.server.IMMServer;
import com.maomao.server.Main;

/**
 * Server event Runner
 * 
 * @author maomao
 * 
 */
public class ServerEventRunner {
	static Logger logger = LoggerFactory.getLogger(ServerEventRunner.class);
	ServerEvent events;
	IceClient iceClient;

	public ServerEventRunner(ServerEvent events, IceClient iceClient) {
		this.events = events;
		this.iceClient = iceClient;
	}

	/**
	 * execute event
	 */
	public void post() {
		IMMServer server = Main.getServer();
		
		server.getThreadPoolTaskExecutor().execute(new Runnable() {
			@Override
			public void run() {
				try {
					events.execute(iceClient);
				} catch (Exception e) {
					logger.error("Error occured:", e);
				}
			}
		});
	}
}

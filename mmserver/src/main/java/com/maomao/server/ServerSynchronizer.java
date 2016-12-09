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

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.maomao.framework.support.rpc.ice.IceClient;
import com.maomao.server.event.HeartBeatEvent;
import com.maomao.server.event.ServerEvent;
import com.maomao.server.event.ServerEventFacotory;
import com.maomao.server.event.ServerEventRunner;

/**
 * Server status synchronizer
 * 
 * @author maomao
 * 
 */
public class ServerSynchronizer {
	static ServerSynchronizer instance = null;

	/**
	 * Keep the connection between the app server and the master server
	 */
	TimerTask heartBeatWork = new TimerTask() {
		@Override
		public void run() {
			ServerEvent event = ServerEventFacotory.getInstance().getEvent(HeartBeatEvent.class);
			
			IMMServer server = Main.getServer();
			if (server instanceof AppServer) {
				AppServer appServer = (AppServer) server;
				
				IceClient ic = new IceClient(appServer.getServerIp(), appServer.getServerPort(), appServer.isServerSsl());
				new ServerEventRunner(event, ic).post();
			}
		}
	};

	/**
	 * start the server synchronizer
	 */
	public static void start() {
		if (null == instance) {
			instance = new ServerSynchronizer();
			instance._start_();
		}
	}

	private void _start_() {
		List<ServerEvent> events = ServerEventFacotory.getInstance().getStartEvents();

		ServerEventRunner er;

		IMMServer server = Main.getServer();
		if (server instanceof AppServer) {
			AppServer appServer = (AppServer) server;
			
			IceClient ic = new IceClient(appServer.getServerIp(), appServer.getServerPort(), appServer.isServerSsl());
			for (ServerEvent se : events) {
				
				er = new ServerEventRunner(se, ic);
				er.post();
			}

			// start the heartbeat work every 30 minutes 
			Timer timer = new Timer();
			timer.schedule(heartBeatWork, 10 * 1000, 30 * 60 * 1000);
		}

	}
}

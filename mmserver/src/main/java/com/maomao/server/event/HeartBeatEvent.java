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

import com.maomao.framework.support.rpc.ice.IceClient;
import com.maomao.framework.support.rpc.ice.IceClient.Action;
import com.maomao.server.AppServer;
import com.maomao.server.IHdpServer;
import com.maomao.server.Main;
import com.maomao.server.manager.idl.AppService;
import com.maomao.server.manager.idl.AppServicePrx;

@ServerEventAnno
public class HeartBeatEvent implements ServerEvent {
	@Override
	public void execute(IceClient iceClient) {
		iceClient.invoke(AppService.class, new Action() {
			@Override
			public void execute(Object prx) {
				AppServicePrx appServicePrx = (AppServicePrx) prx;

				IHdpServer server = Main.getServer();
				if (server instanceof AppServer) {
					AppServer appServer = (AppServer) server;
					appServicePrx.appSyncStatus(appServer.getApp().getAppid(), appServer.getIp(), appServer.getPort());
				}
			}
		});
	}
}

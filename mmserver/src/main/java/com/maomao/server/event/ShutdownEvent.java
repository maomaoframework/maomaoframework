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
import com.maomao.framework.support.rpc.ice.IceClient.Action;
import com.maomao.server.manager.idl.AppService;
import com.maomao.server.manager.idl.AppServicePrx;

/**
 * Server shutdown event
 * 
 * @author maomao
 * 
 */
@ServerEventAnno
public class ShutdownEvent implements ServerEvent {
	static Logger logger = LoggerFactory.getLogger(ShutdownEvent.class);
	/**
	 * execute shutdown event@author huxg
	 */
	@Override
	public void execute(IceClient iceClient) {
		try {
			iceClient.invoke(AppService.class, new Action() {
				@Override
				public void execute(Object prx) {
					AppServicePrx appServicePrx = (AppServicePrx) prx;
					appServicePrx.forceAppInstanceShutdown(0);
				}
			});
		} catch (Exception e) {
			logger.error("Cannot shutdown server, because the server cannot be connected.");
		}
	}
}

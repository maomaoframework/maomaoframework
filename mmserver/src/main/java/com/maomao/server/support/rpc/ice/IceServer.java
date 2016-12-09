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
package com.maomao.server.support.rpc.ice;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maomao.framework.configuration.SysConfiguration;
import com.maomao.server.support.rpc.IRPCServer;

public class IceServer implements IRPCServer {
	private static Logger logger = LoggerFactory.getLogger(IceServer.class);

	public static int DEFAULT_PORT = 7918;

	private int port;

	private Collection<Object> services;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Collection<Object> getServices() {
		return services;
	}

	public void setServices(Collection<Object> services) {
		this.services = services;
	}

	public static void startDefault(Collection<Object> services) {
		IceServer proxy = new IceServer();
		int port = DEFAULT_PORT;
		try {
			port = Integer.parseInt(SysConfiguration.getProperty("rpc.port"));
		} catch (Exception e) {

		}

		proxy.setPort(port);
		proxy.setServices(services);
		proxy.start();
	}

	public void start() {
		new Thread() {
			public void run() {
				Ice.Communicator ic = null;
				try {
					ic = Ice.Util.initialize();
					// tcp -h host -p port -t timeout -z --sourceAddress addr
					// https://doc.zeroc.com/displ	ay/Ice36/Proxy+and+Endpoint+Syntax
					Ice.ObjectAdapter adapter = ic.createObjectAdapterWithEndpoints("hdp", "default -p " + port);

					String serviceName, prefix;
					for (Object service : services) {
						serviceName = service.getClass().getSimpleName();
						int idx = serviceName.indexOf("Impl");
						if (idx < 0) {
							logger.error("Service " + service.getClass().getName() + " load failed! The service must be end with \"Impl\". Please check it!");
							continue;
						}
						prefix = serviceName.substring(0, idx);
						adapter.add((Ice.Object) service, ic.stringToIdentity(prefix));
						
						if (logger.isDebugEnabled())
							logger.debug("Service:" + service.getClass() + " is loaded.");
					}
					// Start ice server
					logger.info("Zero Ice Service started , port:" + port);
					adapter.activate();
					ic.waitForShutdown();
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (ic != null) {
					try {
						ic.destroy();
					} catch (Exception e) {
						System.err.println(e.getMessage());
					}
				}
			}
		}.start();
	}
}

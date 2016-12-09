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
package com.maomao.server.support.rpc;

import java.util.Collection;

import com.maomao.server.support.rpc.ice.IceServer;

/**
 * RPCServe factory
 * 
 * @author maomao
 * 
 */
public class RPCServerFactory {
	public static final int DEFAULT_PORT = 10000;

	public static IRPCServer createSliceServer() {
		return new IceServer();
	}

	public static void startDefault(Collection<Object> services) {
		IceServer.startDefault(services);
	}
}

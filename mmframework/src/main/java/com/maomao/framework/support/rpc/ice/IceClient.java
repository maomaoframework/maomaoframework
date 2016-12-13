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
package com.maomao.framework.support.rpc.ice;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ice客户端
 * 
 * @author maomao
 * 
 */
public class IceClient {
	static Logger logger = LoggerFactory.getLogger(IceClient.class);
	boolean ssl;
	String ip;
	int port;

	public IceClient(String ip, int port, boolean ssl) {
		this.ip = ip;
		this.port = port;
		this.ssl = ssl;
	}
	
	public static IceClient createIceClient(String url) {
		Pattern hostFinderPattern = Pattern.compile("(\\w+):\\/\\/([^/:]+):(\\d*)?([^# ]*)");
		final Matcher match = hostFinderPattern.matcher(url);

		IceClient client = null;
		if (match.find()) {
			client = new IceClient(match.group(2), Integer.parseInt(match.group(3)), match.group(1).equals("mms"));
		}
		return client;
	}

	/**
	 * execute rpc call
	 * 
	 * @param serviceClass
	 * @param action
	 */
	public void invoke(Class<?> serviceClass, Action action) {
		Ice.Communicator ic = null;
		try {
			ic = Ice.Util.initialize();

			Ice.ObjectPrx base = ic.stringToProxy(serviceClass.getSimpleName() + ":" + createConnectionSyntax());

			String className = serviceClass.getName();
			String prxHelperClassName = className + "PrxHelper";

			ClassLoader cl = serviceClass.getClassLoader();

			Class<?> prxHelperClass = cl.loadClass(prxHelperClassName);
			Method method = prxHelperClass.getMethod("checkedCast", Ice.ObjectPrx.class);

			Object prx = method.invoke(null, base);
			if (prx != null) {
				action.execute(prx);
			}

		} catch (Exception e) {
			logger.error(String.format("Exception occur when execute ice service to %s:%d", this.ip, this.port), e);
		} finally {
			try {
				ic.destroy();
			} catch (Exception e) {
			}
		}
	}

	public interface Action {
		void execute(Object prx);
	}

	String createConnectionSyntax() {
		String syntax = " -h " + this.ip + " -p " + this.port;
		if (this.ssl) {
			syntax = "ssl" + syntax;
		} else {
			syntax = "tcp" + syntax;
		}
		return syntax;
	}
}

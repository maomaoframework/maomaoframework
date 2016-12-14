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
package com.maomao.server.plugin.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Ice.Current;

import com.maomao.framework.service.MaoMaoService;
import com.maomao.framework.utils.Message;
import com.maomao.framework.utils.ReflectUtils;
import com.maomao.server.IMMServer;
import com.maomao.server.Main;
import com.maomao.server.plugin.PluginFactory;
import com.maomao.server.plugin.schedule.idl._ScheduleServiceDisp;

/**
 * Schedule Manager
 * 
 * @author maomao
 * 
 */
@MaoMaoService("ScheduleManager")
public class ScheduleServiceImpl extends _ScheduleServiceDisp {
	private static final long serialVersionUID = -9144460904877703016L;
	static Logger logger = LoggerFactory.getLogger(ScheduleServiceImpl.class);

	/**
	 * Regist a schedule task. Load the
	 */
	public String registSchedule(String connectionUrl, String serviceName, String cronExpress, boolean imediate,  Current __current) {
		IMMServer server = Main.getServer();

		try {
			PluginFactory pluginFactory = server.getPluginFactory();

			// check if the plugin exist.
			SchedulePlugin plugin = pluginFactory.getPlugin(SchedulePlugin.class);
			if (plugin != null) {
				// add schedule to executor
				plugin.addSchedule(connectionUrl, serviceName, cronExpress, imediate);
			}
			return Message.okMessage();
		} catch (Exception e) {
			logger.error("Error:", e);
			return Message.errorMessage(e.getMessage());
		}
	}

	/**
	 * execute a schedule
	 * 
	 * @return
	 */
	public String executeSchedule(String serviceName, Current __current) {
		try {
			// invoke task
			String[] p = serviceName.split(":");
			if (p.length == 2) {
				String className = p[0];
				String methodName = p[1];

				// execute method
				ClassLoader cl = Thread.currentThread().getContextClassLoader();
				Class<?> clazz = cl.loadClass(className);
				ReflectUtils.invok(clazz.newInstance(), methodName, null, null);
			}
			return Message.okMessage();
		} catch (Exception e) {
			logger.error("Error:", e);
			return Message.errorMessage(e.getMessage());
		}
	}
}

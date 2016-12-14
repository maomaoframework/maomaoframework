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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.maomao.framework.support.rpc.ice.IceClient;
import com.maomao.framework.support.rpc.ice.IceClient.Action;
import com.maomao.framework.support.schedule.MMSchedule;
import com.maomao.server.AppServer;
import com.maomao.server.IMMServer;
import com.maomao.server.Main;
import com.maomao.server.plugin.IPlugin;
import com.maomao.server.plugin.schedule.idl.ScheduleService;
import com.maomao.server.plugin.schedule.idl.ScheduleServicePrx;

/**
 * Schedule Plugin
 * 
 * @author maomao
 * 
 */
public class SchedulePlugin implements IPlugin {
	Logger logger = LoggerFactory.getLogger(SchedulePlugin.class);

	Map<String, Schedule> schedules = new HashMap<String, Schedule>();

	/**
	 * Call before start.
	 */
	@Override
	public void beforeStart() {
		// load all schedule implementation
		ApplicationContext context = Main.getServer().getApplicationContext();
		Map<String, Object> components = context.getBeansWithAnnotation(Component.class);
		Object obj;
		Class<?> clazz;
		String serviceName;
		String cronExpress;
		boolean imediate;
		MMSchedule anno;
		for (Entry<String, Object> entry : components.entrySet()) {
			obj = entry.getValue();
			clazz = obj.getClass();

			for (Method m : clazz.getDeclaredMethods()) {
				if (m.isAnnotationPresent(MMSchedule.class)) {
					// check method
					try {
						// add this schedule
						anno = m.getAnnotation(MMSchedule.class);
						serviceName = clazz.getName() + ":" + m.getName();
						cronExpress = anno.cron();
						imediate = anno.startImmediate();
						if (StringUtils.isEmpty(cronExpress)) {
							throw new Exception("Cannot find any cron express in your schedule : " + clazz.getName() + "." + m.getName());
						}

						notifyMMServer(serviceName, cronExpress, imediate);
					} catch (Exception e) {
						logger.error("Error occured when parse the schedule, ignore this error and try continue to start :", e);
					}
				}
			}
		}
	}

	/**
	 * Notify mmserver to load this task
	 */
	void notifyMMServer(final String serviceName, final String cronExpress, final boolean imediate) {
		final String localIp, serverIp;
		final int localPort, serverPort;
		boolean serverSsl;

		IMMServer server = Main.getServer();
		if (server instanceof AppServer) {
			AppServer appServer = (AppServer) server;
			serverIp = appServer.getServerIp();
			serverPort = appServer.getServerPort();
			serverSsl = appServer.isServerSsl();
			localIp = appServer.getIp();
			localPort = appServer.getPort();
		} else {
			localIp = serverIp = server.getServerConfiguration().getRpc().getIp();
			localPort = serverPort = server.getServerConfiguration().getRpc().getPort();
			serverSsl = server.getServerConfiguration().getRpc().isSsl();
		}

		IceClient ic = new IceClient(serverIp, serverPort, serverSsl);
		ic.invoke(ScheduleService.class, new Action() {
			@Override
			public void execute(Object prx) {
				ScheduleServicePrx servicePrx = (ScheduleServicePrx) prx;
				servicePrx.registSchedule(makeConnectionUrl(localIp, localPort, false), serviceName, cronExpress, imediate);
			}
		});
	}

	String makeConnectionUrl(String localIp, int port, boolean ssl) {
		return "mm://" + localIp + ":" + port;
	}

	/**
	 * add schedule
	 * 
	 * @param ip
	 * @param port
	 * @param serviceName
	 */
	public void addSchedule(String connectionUrl, String serviceName, String cronExpress, boolean imediate) throws Exception {
		// check if there's a same schedule. if so, ignore it.
		String name = connectionUrl + "/" + serviceName;
		Schedule schedule = schedules.get(name);
		if (schedule != null) {
			throw new Exception("Schedule already exist!");
		}

		schedule = new Schedule();
		schedule.setConnectionUrl(connectionUrl);
		schedule.setServiceName(serviceName);
		schedule.setCronExpression(cronExpress);
		schedule.setImediate(imediate);
		schedules.put(name, schedule);
		QuartzManager.getInstance().addJob(name, ScheduleJob.class, schedule.getCronExpression(), schedule.isImediate());
	}

	public Schedule getSchedule(String name) {
		return schedules.get(name);
	}
}

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

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.maomao.framework.support.rpc.ice.IceClient;
import com.maomao.framework.support.rpc.ice.IceClient.Action;
import com.maomao.server.Main;
import com.maomao.server.plugin.schedule.idl.ScheduleService;
import com.maomao.server.plugin.schedule.idl.ScheduleServicePrx;

/**
 * @author maomao
 * 
 */
public class ScheduleJob implements Job {
	public ScheduleJob() {
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String name = context.getJobDetail().getName();
		SchedulePlugin schedulePlugin = Main.getServer().getPluginFactory().getPlugin(SchedulePlugin.class);
		if (null != schedulePlugin) {
			final Schedule schedule = schedulePlugin.getSchedule(name);
			if (schedule != null) {
				IceClient iceClient = IceClient.createIceClient(schedule.getConnectionUrl());
				iceClient.invoke(ScheduleService.class, new Action() {
					@Override
					public void execute(Object prx) {
						ScheduleServicePrx scheduleServicePrx = (ScheduleServicePrx) prx;
						scheduleServicePrx.executeSchedule(schedule.getServiceName());
					}
				});
			}
		}
	}
}

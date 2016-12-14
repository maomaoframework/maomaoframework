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

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Quartz manager
 * 
 * @author maomao
 */
public class QuartzManager {
	private static QuartzManager instance;
	private static Logger logger = LoggerFactory.getLogger(QuartzManager.class);
	private static SchedulerFactory sf;
	private static String JOB_GROUP_NAME = "mmjob";
	private static String TRIGGER_GROUP_NAME = "mmtrigger";

	private QuartzManager() {
		sf = new StdSchedulerFactory();
	}

	public static QuartzManager getInstance() {
		if (null == instance)
			instance = new QuartzManager();
		return instance;
	}

	public void addJob(final String jobName, final Class<? extends Job> clazz, final String time, final boolean startImmediate) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// start work after 30 seconds.
				try {
					Thread.sleep(30 * 1000);
					JobKey jobKey = JobKey.jobKey(jobName, JOB_GROUP_NAME);
					JobDetail job = JobBuilder.newJob(clazz).withIdentity(jobKey).storeDurably().build();

					Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, TRIGGER_GROUP_NAME).withSchedule(CronScheduleBuilder.cronSchedule(time))
							.build();

					Scheduler scheduler = sf.getScheduler();
					scheduler.start();
					scheduler.scheduleJob(job, trigger);

					if (startImmediate)
						scheduler.triggerJob(jobKey);
				} catch (Exception e) {
					logger.error("Error:", e);
				}
			}
		}).start();
	}
}

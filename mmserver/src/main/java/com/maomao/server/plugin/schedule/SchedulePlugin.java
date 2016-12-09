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

import java.util.Map;

import com.maomao.server.Main;
import com.maomao.server.event.ServerEventAnno;


/**
 * Schedule Plugin
 * 
 * @author maomao
 * 
 */
public class SchedulePlugin implements IPlugin {
	Map<String, Object> schedules;
	
	/**
	 * Call before start.
	 */
	@Override
	public void beforeStart() {
//		schedules = Main.getServer().getApplicationContext().getBeansWithAnnotation(Task.class);
		
		// schedules都属于哪个应用的
	}

	/**
	 * Call 
	 */
	@Override
	public void afterStart() {
		// TODO Auto-generated method stub
	}
}

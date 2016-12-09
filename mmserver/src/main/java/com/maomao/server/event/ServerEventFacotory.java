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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maomao.server.Main;

public class ServerEventFacotory {
	static ServerEventFacotory instance;

	public static ServerEventFacotory getInstance() {
		if (null == instance) {
			instance = new ServerEventFacotory();
			instance.init();
		}
		return instance;
	}

	Map<ServerEventAnno.Type, List<ServerEvent>> serverEvents = new HashMap<ServerEventAnno.Type, List<ServerEvent>>();
	Collection<ServerEvent> eventsList = new ArrayList<ServerEvent>();

	void init() {
		// find serverEvent anno
		Map<String, Object> objects = Main.getServer().getApplicationContext().getBeansWithAnnotation(ServerEventAnno.class);
		if (objects != null) {
			Collection<Object> events = objects.values();
			for (Object o : events) {
				ServerEventAnno anno = o.getClass().getAnnotation(ServerEventAnno.class);

				List<ServerEvent> ens = serverEvents.get(anno.type());
				if (ens == null) {
					ens = new ArrayList<ServerEvent>();
					serverEvents.put(anno.type(), ens);
				}
				ens.add((ServerEvent) o);
				eventsList.add((ServerEvent) o);
			}
		}
	}

	public ServerEvent getEvent(Class<?> clazz) {
		for (ServerEvent e : eventsList) {
			if (e.getClass() == clazz) {
				return e;
			}
		}
		return null;
	}

	public List<ServerEvent> getStartEvents() {
		return serverEvents.get(ServerEventAnno.Type.SERVER_STARTUP);
	}
}

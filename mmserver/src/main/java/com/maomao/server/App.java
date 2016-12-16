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
package com.maomao.server;

import java.util.ArrayList;
import java.util.List;

/**
 * App
 * 
 * @author maomao
 * 
 */
public class App {
	// package name
	String pk;

	String appid;

	String name;

	String description;

	String email;

	String developer;

	String versionLabel;

	Integer version;

	String docBase;

	List<AppInstance> instances = new ArrayList<AppInstance>();

	public String getPk() {
		return pk;
	}

	public void setPk(String pk) {
		this.pk = pk;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDeveloper() {
		return developer;
	}

	public void setDeveloper(String developer) {
		this.developer = developer;
	}

	public String getVersionLabel() {
		return versionLabel;
	}

	public void setVersionLabel(String versionLabel) {
		this.versionLabel = versionLabel;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}


	public List<AppInstance> getInstances() {
		return instances;
	}

	public void setInstances(List<AppInstance> instances) {
		this.instances = instances;
	}

	public String getDocBase() {
		return docBase;
	}

	public void setDocBase(String docBase) {
		this.docBase = docBase;
	}

	/**
	 * Get app instance by ip and port
	 * 
	 * @param ip
	 * @param port
	 */
	public AppInstance getInstance(String ip, int port) {
		for (AppInstance instance : this.instances) {
			if (instance.getIp().equals(ip) && instance.getPort() == port) {
				return instance;
			}
		}
		return null;
	}
	
	public AppInstance getInstance(String instanceId){
		for (AppInstance instance : this.instances) {
			if (instanceId.equals(instance.getIp() + ":" + instance.getPort())){ 
				return instance;
			}
		}
		return null;
	}

	/**
	 * add isntance
	 * 
	 * @param instance
	 */
	public void addAppInstance(AppInstance instance) {
		if (instances != null) {
			for (AppInstance a : instances) {
				if (a.getIp().equals(instance.getIp()) && a.getPort() == instance.getPort())
					throw new RuntimeException("Instance exist.");
			}
		}
		this.instances.add(instance);
	}

	/**
	 * get the instance index 
	 * 
	 * @param ip
	 * @param port
	 * @return
	 */
	public int getIndex(String instanceId) {
		int size = this.instances.size();
		AppInstance instance;
		for (int i = 0; i < size; i++) {
			instance = this.instances.get(i);
			if (instanceId.equals(instance.getIp() + ":" + instance.getPort())) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * remove all instance
	 */
	public void removeInstances (){
		this.instances.clear();
		this.instances = null;
	}
}

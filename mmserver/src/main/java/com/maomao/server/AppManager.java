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

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.maomao.framework.utils.FileUtils;
import com.maomao.startup.Bootstrap;
import com.thoughtworks.xstream.XStream;

/**
 * App Manager
 * 
 * @author maomao
 * 
 */
public class AppManager {
	static Logger logger = LoggerFactory.getLogger(AppManager.class);

	// the configuration
	static final String enviromentConfig = "conf/app-context.xml";
	static final String appXml = "app.xml";

	List<App> apps;

	// app location
	Map<String, File> appDocBase = new HashMap<String, File>();

	// app-context.xml
	File enviromentXml;

	public AppManager() {
		enviromentXml = new File(Main.getServerBase(), enviromentConfig);
	}

	public void init() throws Exception {
		logger.info("Parsing app-context.xml  ...");
		
		// load app resources
		loadAppResources();

		// load app-context.xml
		loadAppEnviroment();

		buildApps();
		logger.info("Parsing finished.");
	}

	/**
	 * load app resources
	 */
	void loadAppResources() {
		File f = new File(Bootstrap.getHdpHome());
		File appsFolder = new File(f, "apps");

		File[] files = appsFolder.listFiles();
		for (File moduleFile : files) {
			appDocBase.put(moduleFile.getName(), moduleFile);
		}
	}

	/**
	 * load app-context.xml
	 */
	@SuppressWarnings("unchecked")
	void loadAppEnviroment() {
		// if app-context.xml doesnot exist ,then create a blank file
		if (!this.enviromentXml.exists()) {
			createBlankEnviromentXml();
		}

		XStream x = new XStream();
		x.alias("apps", java.util.ArrayList.class);
		x.alias("app", App.class);
		x.alias("instance", AppInstance.class);
		x.alias("instances", java.util.ArrayList.class);
		apps = (List<App>) x.fromXML(this.enviromentXml);
	}

	/**
	 * create a blank app-context.xml
	 */
	void createBlankEnviromentXml() {
		String xml = "<apps/>";
		FileUtils.saveString2File(xml, enviromentXml);
	}

	/**
	 * build app enviroment
	 */
	void buildApps() throws Exception {
		// for every app, there must be a context in app-context.xml
		Iterator<App> iter = apps.iterator();
		App app;
		File docBase;
		while (iter.hasNext()) {
			app = iter.next();

			if (StringUtils.isEmpty(app.getDocBase()))
				docBase = new File(Main.getServerHome(), "apps/" + app.getAppid());
			else
				docBase = new File(app.getDocBase());

			// if docbase doesnot exist, then ignore this app
			if (!docBase.exists()) {
				logger.error("Cannot find app docbase : " + app.getAppid() + ". Ignore this app and continue to load another apps.");
				iter.remove();
				continue;
			}

			appDocBase.put(app.getAppid(), docBase);

			app.setDocBase(docBase.getCanonicalPath());

			// parse app.xml in the app directory
			parseAppModeXml(app, docBase);
		}
	}

	/**
	 * parse app.xml
	 * 
	 * @param file
	 * @throws Exception
	 */
	public static void parseAppModeXml(App app, File file) throws Exception {
		logger.info("　　　　reading app.xml in app docbase:" + file.getPath());

		URL url = null;
		if (file.isFile() && file.getName().endsWith(".jar")) {
			String appConfig = "jar:file:" + file.getCanonicalPath() + "!/" + appXml;
			url = new URL(appConfig);
		} else if (file.isDirectory()) {
			File confFile = new File(file, "app.xml");
			if (!confFile.exists()) {
				throw new Exception("Cannot find app.xml in : " + file.getCanonicalPath());
			}
			url = confFile.toURI().toURL();
		}

		if (url != null) {
			XStream xs = new XStream();
			xs.alias("mm-app", App.class);
			App appDesc = (App) xs.fromXML(url.openStream());

			// merge 
			app.setPk(appDesc.getPk());
			app.setAppid(appDesc.getAppid());
			app.setName(appDesc.getName());
			app.setDescription(appDesc.getDescription());
			app.setEmail(appDesc.getEmail());
			app.setDeveloper(appDesc.getDeveloper());
			app.setVersion(appDesc.getVersion());
			app.setVersionLabel(appDesc.getVersionLabel());
		}
	}

	public List<App> getApps() {
		return apps;
	}

	public void setApps(List<App> apps) {
		this.apps = apps;
	}

	/**
	 * Create a new app
	 * 
	 * @param appId
	 */
	public void createApp(String appId) {
		for (App app : apps) {
			if (app.getAppid().equals(appId)) {
				throw new RuntimeException("Appid exist");
			}
		}
		App app = new App();
		app.setAppid(appId);
		
		// set the default docbase folder
		app.setDocBase(Main.getServerHome() + File.separatorChar + "apps" + File.separatorChar + appId);
		this.apps.add(app);
		updateEnviroment();
	}

	/**
	 * get app
	 * 
	 * @param appId
	 * @return
	 */
	public App getApp(String appId) {
		for (App app : apps) {
			if (app.getAppid().equals(appId))
				return app;
		}
		return null;
	}

	/**
	 * update enviroment
	 */
	void updateEnviroment() {
		XStream x = new XStream();
		x.alias("apps", List.class);
		x.alias("app", App.class);
		x.alias("instance", AppInstance.class);
		x.alias("instances", java.util.ArrayList.class);
		String xml = x.toXML(apps);
		FileUtils.saveString2File(xml, this.enviromentXml);
	}

	/**
	 * remove instance
	 * 
	 * @param instanceId
	 */
	public void removeAppInstance(String appid, String instanceId) {
		App app = getApp(appid);
		AppInstance instance = app.getInstance(instanceId);
		if (null != instance)
			app.instances.remove(instance);
		updateEnviroment();
	}

	/**
	 * update instance and configuration
	 * 
	 * @appInstance -- the new appinstance
	 * @tobeUpdatedProperty -- the properties of the instance to be modified
	 */
	public void updateAppInstance(String appid, String instanceId, AppInstance appInstance, String[] tobeUpdatedProperty) throws Exception {
		App app = getApp(appid);
		AppInstance instance = app.getInstance(instanceId);
		boolean dirty = false;
		if (null != instance) {
			if (tobeUpdatedProperty.length > 0) {
				dirty = true;
				for (String property : tobeUpdatedProperty) {
					BeanUtils.setProperty(instance, property, BeanUtils.getProperty(appInstance, property));
				}
			}
		}

		if (dirty)
			updateEnviroment();
	}

	/**
	 * add instance
	 * 
	 * @param app
	 * @param appInstance
	 */
	public void addAppInstance(String appid, AppInstance appInstance) throws Exception {
		if (StringUtils.isEmpty(appInstance.getIp()) || appInstance.getPort() <= 0)
			throw new Exception("Must provide ip and port!");

		App app = getApp(appid);
		AppInstance ai = app.getInstance(appInstance.getIp(), appInstance.getPort());
		if (ai == null) {
			app.addAppInstance(appInstance);
			updateEnviroment();
		}
	}

	/**
	 * remove instance
	 */
	public void removeApp(String appid) {
		App app = getApp(appid);
		if (app != null) {
			app.instances.clear();
			app.instances = null;
		}
		this.apps.remove(app);

		updateEnviroment();
	}
}

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.maomao.framework.utils.FileUtils;
import com.maomao.framework.utils.JsonUtils;
import com.maomao.server.support.ssh.SSHConnection;
import com.maomao.server.support.ssh.SSHServer;
import com.maomao.server.util.ZipUtils;
import com.thoughtworks.xstream.XStream;
import com.trilead.ssh2.SCPClient;

/**
 * RemoteDeploy
 * 
 * @author maomao
 * 
 */
public class RemoteInstanceHelper {
	static Logger logger = LoggerFactory.getLogger(RemoteInstanceHelper.class);
	MMServer mmserver;
	
	public RemoteInstanceHelper(MMServer mmserver){
		this.mmserver = mmserver;
	}
	
	public static boolean testWorkingDirectory(SSHServer server, String workingDirectory) throws Exception {
		if (null == server)
			throw new Exception("Cannot find remote ssh server");

		SSHConnection conn = null;
		try {
			conn = connectToSsh(server);
			conn.exec("mkdir -p " + workingDirectory , System.out);
			return true;
		} catch(Exception e) {
			return false;
		} finally {
			if (null != conn) {
				conn.close();
			}
		}
	}

	/**
	 * start remoteServer
	 * 
	 * @param server
	 * @param instance
	 * @throws Exception
	 */
	public void start(SSHServer server, App app, AppInstance instance, boolean forceUpdate) throws Exception {
		if (null == server)
			throw new Exception("Cannot find remote ssh server");

		// try to ensure the remote instance is full of the copy with the server
		// and the app.
		deploy(server, app, instance, false);

		String remoteServerHome = instance.getWorkingDirectory() + File.separatorChar + Constants.ServerName_MMServer;

		SSHConnection conn = null;
		try {
			conn = connectToSsh(server);
			String serverIp = mmserver.getIp();
			int serverPort = mmserver.getPort();
			
			String params = "-a " + app.getAppid() + " -p " + instance.getPort() + " -i " + instance.getIp() + " -si=" + serverIp + " -sp=" + serverPort
					+ " -ssl=" + instance.ssl;
			conn.exec("sh " + remoteServerHome + "/bin/" + Constants.ServerPrefix + ".sh startapp " + params, System.out);
		} finally {
			if (null != conn) {
				conn.close();
			}
		}
	}

	/**
	 * Deploy instance to remote server.
	 * 
	 * @param server
	 * @param instance
	 * @throws Exception
	 */
	public void deploy(SSHServer server, App app, AppInstance instance, boolean forceUpdate) throws Exception {
		if (null == server)
			throw new Exception("Cannot find remote ssh server");

		// Check whether the server files exist on the remote server.
		String rootFolderPath = instance.getWorkingDirectory();
		String serverFolderPath = rootFolderPath + "/" + Constants.ServerName_MMServer;
		String appsFolderPath = serverFolderPath + "/apps";
		String appFolderPath = appsFolderPath + "/" + app.getAppid();
		String confFolderPath = serverFolderPath + "/conf";
		String appContextFilePath = serverFolderPath + "/conf/app-context.xml";

		SSHConnection conn = null;
		try {
			conn = connectToSsh(server);

			logger.info("Deploying ...");
			if (!existFile(conn, serverFolderPath)) {
				if (logger.isDebugEnabled())
					logger.debug("deploying server working copy to " + serverFolderPath + " ...");
				// Create remote server folder.
				deployServerWorkingCopy(conn, rootFolderPath, serverFolderPath);
			}

			if (!existFile(conn, appFolderPath)) {
				if (logger.isDebugEnabled())
					logger.debug("deploying app to remote server [" + app.getAppid() + "] ...");
				// If the app folder doesnot exist, deploy app working copy to
				// the remote server's apps folder.
				deployAppWorkingCopy(conn, serverFolderPath, app);
			}

			if (existFile(conn, appContextFilePath)) {
				if (logger.isDebugEnabled())
					logger.debug("updating remote app instance context file [" + app.getAppid() + "-" + instance.getIp() + ":" + instance.getPort() + "] ...");

				// Get the content of the app-context.xml.
				String contextFile = getAppContextContent(conn, appContextFilePath);

				// Create remote instance, if necessary.
				createRemoteInstance(conn, contextFile, app, instance, confFolderPath);
			}

			logger.info("Deploy finish!");

		} finally {
			if (null != conn) {
				conn.close();
			}
		}
	}

	static SSHConnection connectToSsh(SSHServer sshServer) throws Exception {
		String userName = sshServer.getAccount();
		String password = sshServer.getPassword();
		String host = sshServer.getIp();
		int port = sshServer.getPort();

		SSHConnection conn = null;
		int tryTimes = 5;
		while (true) {
			if (tryTimes <= 0)
				break;

			try {
				if ("0.0.0.0".equals(host)) {
					logger.error("Invalid inetaddress::0.0.0.0. Modify app-context.xml to set a valid ip.");
					throw new IOException("");
				}

				if (logger.isDebugEnabled())
					logger.debug("Connect to " + host + ":" + port);

				conn = new SSHConnection(host, port);
				conn.connect();
				conn.authenticateWithPassword(userName, password);
				logger.info("Remote server is connected!");
				break;
			} catch (IOException e) {
				logger.error("Failed to connect to remote server: " + e.getMessage());
				logger.error("Waiting for 5 seconds");
				tryTimes--;
				Thread.sleep(5000);
			}
		}

		if (null == conn)
			throw new Exception("Failed to connect to remote server :" + userName + "@" + host + ":" + port + ", identify with  " + password + ".");

		return conn;
	}

	/**
	 * check whether the server files exist on the remote server
	 * 
	 * @return
	 */
	static boolean existFile(SSHConnection conn, String remotePath) throws Exception {
		logger.debug("check  whether the server files exist on the remote server ...");
		int result = conn.exec("test -e " + remotePath, System.out);
		if (result == 1)
			return false;

		// try to put file to remote path
		try {
			SCPClient clt = conn.createSCPClient();
			clt.put("hello".getBytes(), "__hello__", remotePath);
			conn.exec("rm -f " + remotePath + "/__hello__", System.out);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Copy server working copy.
	 */
	static void deployServerWorkingCopy(SSHConnection conn, String rootFolderPath, String serverFolderPath) throws Exception {
		// 1. Create server folder.
		conn.exec("mkdir -p " + serverFolderPath, System.out);

		// 2. Create temp folder
		String remoteTempFolder = rootFolderPath + "/temp";
		String remoteZip = remoteTempFolder + "/remote.zip";

		conn.exec("mkdir -p " + remoteTempFolder, System.out);

		// 3. Update server working copy to remote server.
		SCPClient clt = conn.createSCPClient();

		String localDeployFile = Main.getServerHome() + File.separatorChar + "bin" + File.separatorChar + "remote.zip";
		clt.put(localDeployFile, "remote.zip", remoteTempFolder + "/", "0700");

		// 4. unzip file.
		conn.exec("unzip " + remoteZip + " -d " + serverFolderPath, System.out);

		// 5. remove temp files.
		conn.exec("rm -rf " + remoteTempFolder, System.out);
	}

	/**
	 * Copy app working copy to remote directory.
	 */
	static void deployAppWorkingCopy(SSHConnection conn, String serverFolder, App app) throws Exception {
		// 1. compress app source files.
		String appDocBase = app.getDocBase();
		File appFolder = new File(appDocBase);
		File parent = appFolder.getParentFile();
		File tempZipFile = new File(parent, app.getAppid() + ".zip");
		ZipUtils.compress(appFolder, tempZipFile);

		// 2. upload app zip file to remote server
		SCPClient clt = conn.createSCPClient();
		clt.put(tempZipFile.getCanonicalPath(), tempZipFile.getName(), serverFolder, "0700");

		// 3. unzip app resource files
		conn.exec("unzip " + serverFolder + "/" + tempZipFile.getName() + " -d " + serverFolder + "/apps", System.out);
		conn.exec("rm -rf " + serverFolder + "/" + tempZipFile.getName(), System.out);

		// 4. delete all temp files.
		FileUtils.removeFile(tempZipFile);
	}

	/**
	 * Get app-context.xml content
	 * 
	 * @return
	 */
	static String getAppContextContent(SSHConnection conn, String appContextFilePath) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		conn.exec("cat " + appContextFilePath, out);
		String content = new String(out.toByteArray());
		return content;
	}

	/**
	 * Create remote instance.
	 */
	@SuppressWarnings("unchecked")
	static void createRemoteInstance(SSHConnection conn, String appConfig, App app, AppInstance instance, String remoteFolder) throws Exception {
		XStream x = new XStream();
		x.alias("apps", java.util.ArrayList.class);
		x.alias("app", App.class);
		x.alias("instance", AppInstance.class);
		x.alias("instances", java.util.ArrayList.class);
		List<App> apps = (List<App>) x.fromXML(appConfig);

		App currentApp = null;
		boolean hasApp = false, hasInstance = false;
		if (apps != null) {
			for (App a : apps) {
				if (a.getAppid().equals(app.getAppid())) {
					hasApp = true;
					currentApp = a;

					List<AppInstance> instances = a.getInstances();
					if (instances != null) {
						for (AppInstance i : instances) {
							if (i.getIp().equals(instance.getIp()) && i.getPort() == instance.getPort()) {
								hasInstance = true;
							}
						}
					}
					break;
				}
			}
		}

		// create new app
		if (!hasApp) {
			JSONObject jo = JsonUtils.bean2JSONObject(app);
			jo.remove("instances");
			jo.put("docBase", "");
			currentApp = JsonUtils.JSONObject2Bean(jo, App.class);
			apps.add(currentApp);
		}

		// create new instance
		if (!hasInstance && null != currentApp) {
			JSONObject jo = JsonUtils.bean2JSONObject(instance);
			AppInstance newInstance = JsonUtils.JSONObject2Bean(jo, AppInstance.class);
			newInstance.setRemote(false);
			newInstance.setSshServer(null);
			newInstance.setWorkingDirectory(null);
			currentApp.addAppInstance(newInstance);
		}

		if (!hasApp || !hasInstance) {
			XStream nx = new XStream();
			nx.alias("apps", List.class);
			nx.alias("app", App.class);
			nx.alias("instance", AppInstance.class);
			nx.alias("instances", java.util.ArrayList.class);
			String xml = nx.toXML(apps);
			SCPClient clt = conn.createSCPClient();
			clt.put(xml.getBytes(), "app-context.xml", remoteFolder);
		}
	}
}

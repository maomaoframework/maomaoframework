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
package com.maomao.server.manager.impl;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Ice.Current;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.maomao.framework.configuration.SysConfiguration;
import com.maomao.framework.service.MaoMaoService;
import com.maomao.framework.utils.FileUtils;
import com.maomao.framework.utils.JsonUtils;
import com.maomao.framework.utils.Message;
import com.maomao.framework.utils.ReflectUtils;
import com.maomao.framework.utils.StringUtils;
import com.maomao.server.AppInstance;
import com.maomao.server.AppManager;
import com.maomao.server.HdpServer;
import com.maomao.server.IHdpServer;
import com.maomao.server.Main;
import com.maomao.server.manager.idl.App;
import com.maomao.server.manager.idl._AppServiceDisp;

/**
 * Manage app and instances.
 * 
 * @author maomao
 * 
 */
@SuppressWarnings("serial")
@MaoMaoService("AppService")
public class AppServiceImpl extends _AppServiceDisp {
	static Logger logger = LoggerFactory.getLogger(AppServiceImpl.class);

	/**
	 * Stop server.
	 */
	@Override
	public void stopServer(Current __current) {
		IHdpServer server = Main.getServer();
		if (!server.supportManager())
			return;

		try {
			logger.info("Server is stopping ...");
			AppManager appManager = (AppManager) ReflectUtils.doGetMethod(server, "appManager");
			List<com.maomao.server.App> apps = appManager.getApps();
			for (com.maomao.server.App a : apps) {
				List<AppInstance> instances = a.getInstances();
				try {
					for (AppInstance instance : instances) {
						if (instance.getRunningStatus() == 1)
							ReflectUtils.invok(server, "shutdownAppInstance", new Class[] { instance.getClass() }, new Object[] { instance });
					}
				} catch (Exception e) {
					logger.error("Instance server may be already stoped! ");
				}
			}

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(5 * 1000);
						logger.info("Server stoped successed!");
						System.exit(0);
					} catch (Exception e) {
						System.exit(0);
					}
				}
			}).start();
		} catch (Exception e) {
			logger.error("Error: stop server error!", e);
		}
	}

	@Override
	public List<App> loadApps(Current __current) {
		IHdpServer server = Main.getServer();
		if (!server.supportManager())
			return null;

		try {
			AppManager appManager = (AppManager) ReflectUtils.doGetMethod(server, "appManager");
			List<com.maomao.server.App> apps = appManager.getApps();

			List<App> result = new ArrayList<App>();
			App app;
			for (com.maomao.server.App a : apps) {
				app = new App();
				app.pk = a.getPk();
				app.appid = a.getAppid();
				app.description = a.getDescription();
				app.developer = a.getDeveloper();
				app.email = a.getEmail();
				app.name = a.getName();
				app.pk = a.getPk();
				app.versionLabel = a.getVersionLabel();
				app.version = a.getVersion();
				result.add(app);
			}

			return result;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Stop app instance.
	 */
	@Override
	public String stopAppInstance(String appId, String instanceId, Current __current) {
		IHdpServer server = Main.getServer();
		if (!server.supportManager())
			return null;

		try {
			AppManager appManager = (AppManager) ReflectUtils.doGetMethod(server, "appManager");
			final com.maomao.server.App app = appManager.getApp(appId);
			if (app != null) {
				String[] args = instanceId.split(":");
				AppInstance instance = app.getInstance(args[0], Integer.parseInt(args[1]));
				if (instance != null) {
					ReflectUtils.invok(server, "shutdownAppInstance", new Class[] { instance.getClass() }, new Object[] { instance });
				} else {
					return Message.errorMessage("Stop failed because the instance doesnot exist.");
				}
			}
			return Message.okMessage();
		} catch (Exception e) {
			return Message.errorMessage(e.getMessage());
		}
	}

	/**
	 * Restart app instance.
	 */
	@Override
	public String restartAppInstance(String appId, String instanceId, Current __current) {
		IHdpServer server = Main.getServer();
		if (!server.supportManager())
			return null;

		try {
			AppManager appManager = (AppManager) ReflectUtils.doGetMethod(server, "appManager");
			final com.maomao.server.App app = appManager.getApp(appId);

			if (app != null) {
				String[] args = instanceId.split(":");
				AppInstance instance = app.getInstance(args[0], Integer.parseInt(args[1]));
				if (instance != null) {
					ReflectUtils.invok(server, "restartAppInstance", new Class[] { app.getClass(), instance.getClass() }, new Object[] { app, instance });
				} else {
					return Message.errorMessage("Restart failed because the instance doesnot exist.");
				}
			}
			return Message.okMessage();
		} catch (Exception e) {
			return Message.errorMessage(e.getMessage());
		}
	}

	/**
	 * Start app instance.
	 */
	@Override
	public String startAppInstance(String appId, String instanceId, Current __current) {
		IHdpServer server = Main.getServer();
		if (!server.supportManager())
			return null;

		try {
			AppManager appManager = (AppManager) ReflectUtils.doGetMethod(server, "appManager");
			final com.maomao.server.App app = appManager.getApp(appId);
			if (app != null) {
				String[] args = instanceId.split(":");
				AppInstance instance = app.getInstance(args[0], Integer.parseInt(args[1]));
				if (instance != null && instance.getRunningStatus() == 0) {
					if (instance.isRemote()) {
						// Start remote instance.
						ReflectUtils.invok(server, "startRemoteAppInstance", new Class[] { app.getClass(), instance.getClass() },
								new Object[] { app, instance });
					} else {
						// Start local instance.
						ReflectUtils
								.invok(server, "startLocalAppInstance", new Class[] { app.getClass(), instance.getClass() }, new Object[] { app, instance });
					}
				}
			}
			return Message.okMessage();
		} catch (Exception e) {
			e.printStackTrace();
			return Message.errorMessage(e.getMessage());
		}
	}

	/**
	 * Remove app instance.
	 */
	@Override
	public String removeAppInstance(String appId, String instanceId, Current __current) {
		IHdpServer server = Main.getServer();
		if (!server.supportManager())
			return null;

		HdpServer hdpServer = (HdpServer) server;

		try {
			// Stop the instance.
			stopAppInstance(appId, instanceId, __current);

			// Remove instance configuration.
			hdpServer.getAppManager().removeAppInstance(appId, instanceId);
			return null;
		} catch (Exception e) {
			return Message.errorMessage(e.getMessage());
		}
	}
	
	
	/**
	 * Create an app
	 * @return
	 */
	public String createApp(String appId, Current __current) {
		IHdpServer server = Main.getServer();
		if (!server.supportManager())
			return null;

		HdpServer hdpServer = (HdpServer) server;

		try {
			com.maomao.server.App app = hdpServer.getAppManager().getApp(appId);
			if (app != null) {
				return Message.errorMessage(String.format("The app %s is already exist, cannot re-create the same app.", appId));

			} else {
				hdpServer.getAppManager().createApp(appId);
			}
			return Message.okMessage("App has been created!");
		} catch (Exception e) {
			return Message.errorMessage(e.getMessage());
		}
	}
	

	/**
	 * Remove app
	 */
	@Override
	public String removeApp(String appId, Current __current) {
		IHdpServer server = Main.getServer();
		if (!server.supportManager())
			return null;

		HdpServer hdpServer = (HdpServer) server;

		try {
			com.maomao.server.App app = hdpServer.getAppManager().getApp(appId);
			if (app != null) {
				List<AppInstance> instances = app.getInstances();
				if (instances != null && instances.size() > 0) {
					for (AppInstance instance : instances) {
						stopAppInstance(appId, instance.getIp() + ":" + instance.getPort(), __current);
					}
				}

				// Remove the app's resource files.
				File appsFolder = new File(Main.getServerHomeFolder(), "apps");
				File appFolder = new File(appsFolder, app.getAppid());
				FileUtils.removeFile(appFolder);

				hdpServer.getAppManager().removeApp(appId);
			}
			return Message.okMessage("App has been removed!");
		} catch (Exception e) {
			return Message.errorMessage(e.getMessage());
		}
	}

	/**
	 * Stop all instances of the app
	 */
	@Override
	public String stopApp(String appId, Current __current) {
		IHdpServer server = Main.getServer();
		if (!server.supportManager())
			return null;

		HdpServer hdpServer = (HdpServer) server;

		try {
			com.maomao.server.App app = hdpServer.getAppManager().getApp(appId);
			if (app != null) {
				List<AppInstance> instances = app.getInstances();
				if (instances != null && instances.size() > 0) {
					for (AppInstance instance : instances) {
						stopAppInstance(appId, instance.getIp() + ":" + instance.getPort(), __current);
					}
				}
			}
			return Message.okMessage("All apps have been stoped!");
		} catch (Exception e) {
			return Message.errorMessage(e.getMessage());
		}
	}

	/**
	 * Start all the instances of the app.
	 */
	@Override
	public String startApp(String appId, Current __current) {
		IHdpServer server = Main.getServer();
		if (!server.supportManager())
			return null;

		HdpServer hdpServer = (HdpServer) server;

		try {
			com.maomao.server.App app = hdpServer.getAppManager().getApp(appId);
			if (app != null) {
				List<AppInstance> instances = app.getInstances();
				if (instances != null && instances.size() > 0) {
					for (AppInstance instance : instances) {
						if (instance.getRunningStatus() == 0) {
							startAppInstance(appId, instance.getIp() + ":" + instance.getPort(), __current);
						}
					}
				}
			}
			return Message.okMessage("All instances have been started.");
		} catch (Exception e) {
			e.printStackTrace();
			return Message.errorMessage(e.getMessage());
		}
	}

	/**
	 * Stop all the instances of the app.
	 */
	@Override
	public String restartApp(String appId, Current __current) {
		IHdpServer server = Main.getServer();
		if (!server.supportManager())
			return null;

		HdpServer hdpServer = (HdpServer) server;

		try {
			com.maomao.server.App app = hdpServer.getAppManager().getApp(appId);
			if (app != null) {
				List<AppInstance> instances = app.getInstances();
				if (instances != null && instances.size() > 0) {
					for (AppInstance instance : instances) {
						restartAppInstance(appId, instance.getIp() + ":" + instance.getPort(), __current);
					}
				}
			}
			return Message.okMessage("All app have been restarted!");
		} catch (Exception e) {
			return Message.errorMessage(e.getMessage());
		}
	}

	/**
	 * Create a instance.
	 */
	@Override
	public String createAppInstance(String appId, String appInstanceJson, Current __current) {
		IHdpServer server = Main.getServer();
		if (!server.supportManager())
			return null;

		HdpServer hdpServer = (HdpServer) server;

		try {
			com.maomao.server.App app = hdpServer.getAppManager().getApp(appId);
			if (app != null) {

				// change to bean
				AppInstance newInstance = JsonUtils.String2Bean(appInstanceJson, AppInstance.class);
				
				// valid remote instance workingDirectory
				newInstance.validate();
					
				hdpServer.getAppManager().addAppInstance(appId, newInstance);
				
				if (newInstance.isRemote()) {
					// If it is a remote instance, then deploy the app.
					hdpServer.deployRemoteAppInstance(app, newInstance);
				}

				// If the instance is started ,then start it after reset the
				// configuration file.
				if (newInstance.isEnable()) {
					startAppInstance(appId, newInstance.getIp() + ":" + newInstance.getPort(), __current);
				}
			} else {
				return Message.errorMessage(String.format("Cannot create instance, beacause there's no app with id:%s,found!", appId));
			}
			return Message.okMessage("Instance created!");
		} catch (Exception e) {
			return Message.errorMessage(e.getMessage());
		}
	}

	/**
	 * Update the instance of the App
	 */
	@Override
	public String updateAppInstance(String appId, String instanceId, String appInstanceJson, Current __current) {
		IHdpServer server = Main.getServer();
		if (!server.supportManager())
			return null;

		HdpServer hdpServer = (HdpServer) server;

		try {
			com.maomao.server.App app = hdpServer.getAppManager().getApp(appId);
			if (app != null) {
				AppInstance appInstance = app.getInstance(instanceId);
				if (null != appInstance) {

					// Retrive the json object.
					JSONObject jo = JsonUtils.String2JSONObject(appInstanceJson);
					String[] property = jo.keySet().toArray(new String[jo.size()]);
					if (property != null && property.length > 0) {
						boolean isStart = appInstance.getRunningStatus() == 1;

						// change to bean
						AppInstance newInstance = JsonUtils.String2Bean(appInstanceJson, AppInstance.class);

						// stop the current app
						if (isStart)
							stopAppInstance(appId, appInstance.getIp() + ":" + appInstance.getPort(), __current);

						hdpServer.getAppManager().updateAppInstance(appId, instanceId, newInstance, property);

						// If the instance is started, just start it after reset
						// the configuration file.
						if (isStart)
							startAppInstance(appId, appInstance.getIp() + ":" + appInstance.getPort(), __current);
					}
				}
			}
			return Message.okMessage();
		} catch (Exception e) {
			return Message.errorMessage(e.getMessage());
		}
	}

	@Override
	public String getHdpServerInfo(Current __current) {
		IHdpServer server = Main.getServer();
		if (!server.supportManager())
			return null;

		try {
			AppManager appManager = (AppManager) ReflectUtils.doGetMethod(server, "appManager");
			List<com.maomao.server.App> apps = appManager.getApps();

			JSONArray ja = new JSONArray();
			for (com.maomao.server.App app : apps) {
				JSONObject jo = new JSONObject();
				jo.put("appId", app.getAppid());

				List<AppInstance> instances = app.getInstances();
				JSONArray jaInstance = new JSONArray();
				for (AppInstance instance : instances) {
					JSONObject joInstance = new JSONObject();
					joInstance.put("ip", instance.getIp());
					joInstance.put("port", instance.getPort());
					joInstance.put("runningStatus", instance.getRunningStatus());
					joInstance.put("enable", instance.isEnable());
					jaInstance.add(joInstance);
				}
				jo.put("instances", jaInstance);
				ja.add(jo);
			}
			return ja.toString();
		} catch (Exception e) {
			return Message.errorMessage(e.getMessage());
		}
	}

	@Override
	public String syncServerInfo(Current __current) {
		try {
			String serverIp = SysConfiguration.getProperty("hdp.server.ip");
			if (StringUtils.isEmpty(serverIp)) {
				serverIp = getRealIpAddress();
			}
			return Message.okMessage(new String[] { "serverIp", "serverHttpPort" }, new String[] { serverIp });
		} catch (Exception e) {
		}
		return Message.error();
	}

	static String getRealIpAddress() {
		Collection<InetAddress> i = getAllHostAddress();
		if (i != null) {
			String ip;
			for (InetAddress address : i) {
				if (address instanceof Inet4Address) {
					ip = address.getHostAddress();
					if (!ip.contains("127.0.0.1") && !ip.contains("localhost")) {
						return ip;
					}
				}
			}
		}
		return "127.0.0.1";
	}

	static Collection<InetAddress> getAllHostAddress() {
		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			Collection<InetAddress> addresses = new ArrayList<InetAddress>();

			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = networkInterfaces.nextElement();
				Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
				while (inetAddresses.hasMoreElements()) {
					InetAddress inetAddress = inetAddresses.nextElement();
					addresses.add(inetAddress);
				}
			}

			return addresses;
		} catch (SocketException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * App synchronize notify
	 */
	@Override
	public String appSyncStatus(String jsonInfo, String ip, int port, Current __current) {
		return null;
	}

	/**
	 * App stop notify.
	 */
	@Override
	public String appStopNotify(String appId, String ip, int port, Current __current) {
		IHdpServer server = Main.getServer();
		if (!server.supportManager())
			return null;

		HdpServer hdpServer = (HdpServer) server;

		try {
			com.maomao.server.App app = hdpServer.getAppManager().getApp(appId);
			AppInstance instance = app.getInstance(ip, port);
			if (instance != null) {
				instance.setRunningStatus(AppInstance.STATUS_STOP);
			}
			return Message.okMessage();
		} catch (Exception e) {
			return Message.error();
		}
	}

	/**
	 * App start notify
	 */
	@Override
	public String appStartupNotify(String appId, String ip, int port, Current __current) {
		IHdpServer server = Main.getServer();
		if (!server.supportManager())
			return null;

		HdpServer hdpServer = (HdpServer) server;

		try {
			com.maomao.server.App app = hdpServer.getAppManager().getApp(appId);
			AppInstance instance = app.getInstance(ip, port);
			if (instance != null) {
				instance.setRunningStatus(AppInstance.STATUS_RUNNING);

				logger.info("Received the notify of the instance" + instance.getIp() + ":" + instance.getPort() + ".");
			}
			return Message.okMessage();
		} catch (Exception e) {
			return Message.error();
		}
	}

	@Override
	public void forceAppInstanceShutdown(int seconds, Current __current) {
		try {
			if (seconds <= 0) {
				seconds = 5;
			}

			// Start a thread to shutdown this instance
			final int sc = seconds;
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						logger.info("Instance will be shutdown in " + sc + "seconds ...");
						Thread.sleep(sc * 1000);
						logger.info("Instance has been shutdown.");
						System.exit(0);
					} catch (Exception e) {
						System.exit(0);
					}
				}
			}).start();
		} catch (Exception e) {
		}
	}

	@Override
	public void forceAppInstanceRestart(int seconds, Current __current) {
	}

}

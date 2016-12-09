package com.maomao.server.support.ssh;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.maomao.framework.utils.FileUtils;
import com.maomao.framework.utils.key.Key;
import com.maomao.server.Main;
import com.thoughtworks.xstream.XStream;

/**
 * SSH server configuration
 * 
 * @author maomao
 * 
 */
public class SSHServerManager {
	static SSHServerManager instance = null;
	List<SSHServer> servers = new ArrayList<SSHServer>();
	File configFile;

	public static SSHServerManager getInstance() {
		if (instance == null) {
			instance = new SSHServerManager();
			instance.init();
		}
		return instance;
	}

	private SSHServerManager() {
		File confFolder = new File(Main.getServerBaseFolder(), "conf");
		configFile = new File(confFolder, "ssh_servers.xml");
	}

	void init() {
		File configFile = getConfigFile();
		if (!configFile.exists()) {
			createBlankConfigFile();
		}
		readConfigFile(configFile);
	}

	File getConfigFile() {
		return configFile;
	}

	void readConfigFile(File configFile) {
		servers.clear();
		servers = new ArrayList<SSHServer>();

		FileInputStream in = null;
		try {
			in = new FileInputStream(configFile);
			parseConfigFile(in);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception e) {

			}
		}
	}

	/**
	 * parse xml
	 * 
	 * @param in
	 */
	@SuppressWarnings("unchecked")
	public void parseConfigFile(InputStream in) {
		XStream x = new XStream();
		x.alias("servers", java.util.ArrayList.class);
		x.alias("server", SSHServer.class);

		servers = (List<SSHServer>) x.fromXML(in);
	}

	/**
	 * update xml
	 */
	void updateConfigFile() {
		XStream x = new XStream();
		x.alias("servers", List.class);
		x.alias("server", SSHServer.class);
		String xml = x.toXML(servers);
		FileUtils.saveString2File(xml, getConfigFile());
	}

	/**
	 * create blank xml
	 */
	public void createBlankConfigFile() {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><servers></servers>";
		FileUtils.saveString2File(xml, getConfigFile());
	}

	/**
	 * add server
	 */
	public void addSSHServer(SSHServer server) {
		// check if server is already exist.
		for (SSHServer s : servers) {
			if (server.getIp().equals(s.getIp()) && server.getPort() == s.getPort()) {
				return;
			}
		}
		
		server.setKey(Key.key());
		this.servers.add(server);
		updateConfigFile();
	}

	/**
	 * delete ssh server
	 */
	public void removeSSHServer(String key) {
		Iterator<SSHServer> iter = servers.iterator();
		SSHServer server;
		boolean dirty = false;
		while (iter.hasNext()) {
			server = iter.next();
			if (server.getKey().equals(key)) {
				iter.remove();
				dirty = true;
				break;
			}
		}
		if (dirty)
			updateConfigFile();
	}

	/**
	 * update ssh server
	 */
	public void updateSSHServer(String key, SSHServer server) {
		boolean dirty = false;
		for (SSHServer oldServer : servers) {
			if (oldServer.getKey().equals(server.getKey())) {
				oldServer.setIp(server.getIp());
				oldServer.setPort(server.getPort());
				oldServer.setName(server.getName());
				oldServer.setPassword(server.getPassword());
				oldServer.setAccount(server.getAccount());
				dirty = true;
				break;
			}
		}
		if (dirty)
			updateConfigFile();
	}

	public List<SSHServer> getServers() {
		return servers;
	}

	public void setServers(List<SSHServer> servers) {
		this.servers = servers;
	}

	/**
	 * get server by id
	 * 
	 * @param id
	 * @return
	 */
	public SSHServer getServerById(String id) {
		for (SSHServer s : this.servers) {
			if (s.getKey().equals(id)) {
				return s;
			}
		}
		return null;
	}
}

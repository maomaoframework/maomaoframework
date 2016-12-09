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

import com.maomao.framework.utils.StringUtils;
import com.maomao.server.support.ssh.SSHServer;
import com.maomao.server.support.ssh.SSHServerManager;

/**
 * App instance
 * 
 * @author maomao
 * 
 */
public class AppInstance {
	public static final int STATUS_STOP = 0;
	public static final int STATUS_RUNNING = 1;

	String ip;
	int port;
	String jvm;
	boolean ssl;

	// enable auto startup
	boolean enable = true;

	// 0 - stop, 1-running
	int runningStatus = 0;

	// is remote app instance
	boolean remote = false;

	// ssh server key
	String sshServer;

	// the remote directory
	String workingDirectory;

	long launchTimeoutInMillis;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getJvm() {
		return jvm;
	}

	public void setJvm(String jvm) {
		this.jvm = jvm;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public int getRunningStatus() {
		return runningStatus;
	}

	public void setRunningStatus(int runningStatus) {
		this.runningStatus = runningStatus;
	}

	public boolean isRemote() {
		return remote;
	}

	public void setRemote(boolean remote) {
		this.remote = remote;
	}

	public String getSshServer() {
		return sshServer;
	}

	public void setSshServer(String sshServer) {
		this.sshServer = sshServer;
	}

	public String getWorkingDirectory() {
		return workingDirectory;
	}

	public void setWorkingDirectory(String workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	public long getLaunchTimeoutInMillis() {
		return launchTimeoutInMillis;
	}

	public void setLaunchTimeoutInMillis(long launchTimeoutInMillis) {
		this.launchTimeoutInMillis = launchTimeoutInMillis;
	}

	public boolean isSsl() {
		return ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}
	
	public void validate() throws Exception {
		if (StringUtils.isEmptyAfterTrim(getIp()))
			throw new Exception("Ip is invalid.");
		if (getPort() <= 0)
			throw new Exception("Port is invalid.");
		if (isRemote()) {
			if (StringUtils.isEmptyAfterTrim(getWorkingDirectory()))
				throw new Exception("No provide workingDirectory for remote instance.");
			if (StringUtils.isEmpty(getSshServer()))
				throw new Exception("No provide sshServer for remote instance.");
			
			// valid workingDirectory exist
			SSHServer sshServer = SSHServerManager.getInstance().getServerById(getSshServer());
			if (sshServer == null) 
				throw new Exception("Cannot find ssh server with key:" + getSshServer());
			
			if (!RemoteInstanceHelper.testWorkingDirectory(sshServer, getWorkingDirectory()))
				throw new Exception("The workingDirectory :" + getWorkingDirectory() + " doesnot exist on the remote server");
		}
	}
}

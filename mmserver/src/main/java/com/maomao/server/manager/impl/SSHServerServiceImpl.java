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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Ice.Current;

import com.maomao.framework.service.MaoMaoService;
import com.maomao.framework.utils.JsonUtils;
import com.maomao.framework.utils.Message;
import com.maomao.framework.utils.StringUtils;
import com.maomao.server.Constants;
import com.maomao.server.manager.idl._SSHServerServiceDisp;
import com.maomao.server.support.ssh.SSHServer;
import com.maomao.server.support.ssh.SSHServerManager;

/**
 * Remote server manager
 * 
 * @author maomao
 * 
 */
@SuppressWarnings("serial")
@MaoMaoService("SSHServerService")
public class SSHServerServiceImpl extends _SSHServerServiceDisp {
	static Logger logger = LoggerFactory.getLogger(SSHServerServiceImpl.class);

	/**
	 * return remote servers 
	 */
	@Override
	public String loadServers(Current __current) {
		try {
			List<SSHServer> servers = SSHServerManager.getInstance().getServers();
			return Message.okMessage(servers);
		} catch (Exception e) {
			return Message.error();
		}
	}

	/**
	 * create a remote server
	 */
	@Override
	public String addServer(String serverJson, Current __current) {
		try {
			SSHServer server = JsonUtils.String2Bean(serverJson, SSHServer.class);
			if (server.getPort() <= 0) {
				logger.error(String.format("No provide remote server ssh port! %d will be used as the default port!", Constants.SSH_PORT_DEFAULT));
				server.setPort(Constants.SSH_PORT_DEFAULT);
			}
			if (StringUtils.isEmptyAfterTrim(server.getAccount())){
				logger.error(String.format("No provide remote server account! %s will be used as the default account!", Constants.SSH_ACCOUNT_DEFAULT));
				server.setAccount(Constants.SSH_ACCOUNT_DEFAULT);
			}
			
			SSHServerManager.getInstance().addSSHServer(server);
			return Message.okMessage(server);
		} catch (Exception e) {
			e.printStackTrace();
			return Message.error();
		}
	}

	@Override
	public String updateServer(String key, String serverJson, Current __current) {
		try {
			SSHServer server = JsonUtils.String2Bean(serverJson, SSHServer.class);
			SSHServerManager.getInstance().updateSSHServer(key, server);
			return Message.okMessage();
		} catch (Exception e) {
			return Message.error();
		}
	}

	@Override
	public String removeServer(String key, Current __current) {
		try {
			SSHServerManager.getInstance().removeSSHServer(key);
			return Message.okMessage();
		} catch (Exception e) {
			return Message.error();
		}
	}
}

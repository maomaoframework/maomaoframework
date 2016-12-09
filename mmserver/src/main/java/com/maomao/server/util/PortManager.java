package com.maomao.server.util;

import java.io.IOException;
import java.net.ServerSocket;

import com.maomao.framework.configuration.SysConfiguration;

public class PortManager {
	static int CURRENT_PORT = -1;

	public static int getAvaliablePort() {
		if (CURRENT_PORT < 0) {
			CURRENT_PORT = Integer.parseInt(SysConfiguration.getProperty("rpc.port"));
		}

		CURRENT_PORT++;

		for (int i = CURRENT_PORT; i < 65535; i++) {
			ServerSocket ss = null;
			try {
				ss = new ServerSocket(i);
				CURRENT_PORT = i;
				break;
			} catch (IOException e) {
			} finally {
				try {
					if (ss != null) {
						ss.close();
					}
				} catch (Exception e) {

				}
			}
		}
		return CURRENT_PORT;
	}
}

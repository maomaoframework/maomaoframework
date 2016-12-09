package com.maomao.server.support.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.trilead.ssh2.ChannelCondition;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.Session;

public class SSHConnection extends Connection {

	public SSHConnection(String hostname, int port) {
		super(hostname, port);
	}

	public SSHConnection(String hostname) {
		super(hostname);
	}
	
	
	public int exec(String command, OutputStream output) throws IOException, InterruptedException {
		Session session = openSession();
		try {
			session.execCommand(command);
			PumpThread t1 = new PumpThread(session.getStdout(), output);
			t1.start();
			PumpThread t2 = new PumpThread(session.getStderr(), output);
			t2.start();
			session.getStdin().close();
			t1.join();
			t2.join();

			session.waitForCondition(ChannelCondition.EXIT_STATUS, 3000);
			Integer r = session.getExitStatus();
			if (r != null)
				return r.intValue();
			return -1;
		} finally {
			session.close();
		}
	}

	private static final class PumpThread extends Thread {
		private final InputStream in;
		private final OutputStream out;

		public PumpThread(InputStream in, OutputStream out) {
			super("pump thread");
			this.in = in;
			this.out = out;
		}

		public void run() {
			byte[] buf = new byte[1024];
			try {
				while (true) {
					int len = in.read(buf);
					if (len < 0) {
						in.close();
						return;
					}
					out.write(buf, 0, len);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}

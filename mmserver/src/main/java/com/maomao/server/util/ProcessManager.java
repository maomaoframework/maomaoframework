package com.maomao.server.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessManager {
	private static Logger logger = LoggerFactory.getLogger(ProcessManager.class);
	Map<String, Proc> processes;
	private static ProcessManager instance;

	public static ProcessManager getInstance() {
		if (instance == null) {
			instance = new ProcessManager();
			instance._init_();
		}
		return instance;
	}

	private ProcessManager() {

	}

	void _init_() {
		processes = new HashMap<String, Proc>();
	}

	/**
	 * Create process
	 */
	public void createProcess(String sid, String command) throws Exception {
		Process process = Runtime.getRuntime().exec(command);
		Proc proc = new Proc();
		proc.sid = sid;
		proc.process = process;
		proc.outwork = new Thread(new ProcessOutputWork(process));
		proc.errorwork = new Thread(new ProcessErrorWork(process));

		processes.put(sid, proc);
		proc.outwork.start();
	}

	/**
	 * shutdown process
	 */
	public void close() {
		for (Entry<String, Proc> entry : processes.entrySet()) {
			entry.getValue().process.destroy();
		}
	}

	void printOutput(Process ps) {
		OutputStream fos = ps.getOutputStream();
		PrintStream printStream = new PrintStream(fos);
		printStream.print("\n");
		printStream.flush(); 

		InputStream ios = null;
		BufferedReader br = null;
		String s;
		try {
			ios = ps.getInputStream();
			br = new BufferedReader(new InputStreamReader(ios));
			while ((s = br.readLine()) != null) {
				logger.info(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (ios != null)
					ios.close();
			} catch (Exception e) {
			}
		}
	}

	void printError(Process ps) {
		OutputStream fos = ps.getOutputStream();
		PrintStream printStream = new PrintStream(fos);
		printStream.print("\n");
		printStream.flush();

		InputStream ios = null;
		BufferedReader br = null;
		String s;
		try {
			ios = ps.getErrorStream();
			br = new BufferedReader(new InputStreamReader(ios));
			while ((s = br.readLine()) != null) {
				logger.error(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (ios != null)
					ios.close();
			} catch (Exception e) {
			}
		}
	}

	class Proc {
		String sid;
		Process process;
		Thread outwork;
		Thread errorwork;
	}

	/**
	 * Process output
	 * 
	 * @author maomao
	 * 
	 */
	class ProcessOutputWork implements Runnable {
		Process process;

		public ProcessOutputWork(Process process) {
			this.process = process;
		}

		@Override
		public void run() {
			printOutput(process);
		}
	}
	class ProcessErrorWork implements Runnable {
		Process process;

		public ProcessErrorWork(Process process) {
			this.process = process;
		}

		@Override
		public void run() {
			printError(process);
		}
	}
}

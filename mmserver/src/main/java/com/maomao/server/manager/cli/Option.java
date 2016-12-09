package com.maomao.server.manager.cli;

public class Option {
	String argName;
	String shortOpt;
	String longOpt;
	boolean hasArg;
	String desc;

	Option(String shortOpt) {
		this.shortOpt = shortOpt;
	}

	public static Option builder(String s) {
		return new Option(s);
	}

	public Option longOpt(String s) {
		this.longOpt = s;
		return this;
	}

	public Option argName(String s) {
		this.argName = s;
		return this;
	}

	public Option hasArg() {
		this.hasArg = true;
		return this;
	}

	public Option hasArg(boolean hasArg) {
		this.hasArg = hasArg;
		return this;
	}

	public Option desc(String s) {
		this.desc = s;
		return this;
	}

	public Option build() {
		return this;
	}
}

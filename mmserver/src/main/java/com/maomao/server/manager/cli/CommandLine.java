package com.maomao.server.manager.cli;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.util.StringUtils;

/**
 * Command Line to execute parse.
 * 
 * @author maomao
 * 
 */
public class CommandLine {
	public List<Option> options = new ArrayList<Option>();
	Result result;

	public void addOption(Option option) {
		this.options.add(option);
	}

	public void parse(String[] args) throws Exception {
		result = new Result();
		result.command = args[0].trim();
		result.command = result.command.replaceAll("-", "");
		result.value = makeOptValue(args);
		for (Option o : options) {
			if (o.argName.equals(result.command) || o.longOpt.equals(result.command) || o.shortOpt.equals(result.command)) {
				result.option = o;
				break;
			}
		}

		if (null == result.option) {
			throw new Exception("Unknown command.");
		}
	}

	private String makeOptValue(String[] args) {
		String[] dest = new String[args.length - 1];
		System.arraycopy(args, 1, dest, 0, dest.length);
		return StringUtils.arrayToDelimitedString(dest, " ");
	}

	public void printHelp() {
		PrintWriter pw = new PrintWriter(System.out);

		// initialise the string buffer
		StringBuffer buff = new StringBuffer();

		// temp variable
		Option option;

		// iterate over the options
		for (Iterator<Option> i = options.iterator(); i.hasNext();) {
			// get the next Option
			option = (Option) i.next();

			if (option.shortOpt != null) {
				buff.append("-").append(option.shortOpt);
			} else {
				buff.append("--").append(option.longOpt);
			}

			// if the Option has a value
			if (option.hasArg && !StringUtils.isEmpty(option.argName)) {
				buff.append(" <").append(option.argName).append(">");
			}
			
			buff.append(option.desc);
			buff.append("\n");
			if (i.hasNext()) {
				buff.append(" ");
			}
		}

		pw.println(buff.toString());
		pw.flush();
	}

	public class Result {
		String command;
		String value;
		Option option;
	}
}

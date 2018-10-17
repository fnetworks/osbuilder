package org.fnet.osbuilder.util;

import java.io.Console;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class ConsoleInterface {

	private interface ConsoleAccessor {

		void printFormatted(String fmt, Object... args);

		String readLine();

		default String readLine(String promptFmt, Object... args) {
			printFormatted(promptFmt, args);
			return readLine();
		}

		char[] readPassword();

		default char[] readPassword(String promptFmt, Object... args) {
			printFormatted(promptFmt, args);
			return readPassword();
		}

	}

	private static class SystemConsoleAccessor implements ConsoleAccessor {

		private Console console;

		public SystemConsoleAccessor(Console console) {
			this.console = console;
		}

		@Override
		public void printFormatted(String fmt, Object... args) {
			console.printf(fmt, args);
		}

		@Override
		public String readLine() {
			return console.readLine();
		}

		@Override
		public String readLine(String promptFmt, Object... args) {
			return console.readLine(promptFmt, args);
		}

		@Override
		public char[] readPassword() {
			return console.readPassword();
		}

		@Override
		public char[] readPassword(String promptFmt, Object... args) {
			return console.readPassword(promptFmt, args);
		}
	}

	private static class DefaultConsoleAccessor implements ConsoleAccessor {

		private PrintStream output;
		private Scanner input;

		public DefaultConsoleAccessor(PrintStream output, Scanner input) {
			this.output = output;
			this.input = input;
		}

		public DefaultConsoleAccessor(PrintStream output, InputStream input) {
			this.output = output;
			this.input = new Scanner(input);
		}

		@Override
		public void printFormatted(String fmt, Object... args) {
			output.printf(fmt, args);
		}

		@Override
		public String readLine() {
			return input.nextLine();
		}

		@Override
		public char[] readPassword() {
			return input.nextLine().toCharArray();
		}
	}

	private ConsoleAccessor console;

	public ConsoleInterface() {
		if (System.console() != null) {
			this.console = new SystemConsoleAccessor(System.console());
		} else {
			this.console = new DefaultConsoleAccessor(System.out, System.in);
		}
	}
	public String ask(String prompt, boolean repeatIfEmpty) {
		String response;
		do response = console.readLine("%s: ", prompt).trim();
		while (repeatIfEmpty && response.isEmpty());
		return response;
	}

	public String ask(String prompt, String defaultValue) {
		String response;
		if (defaultValue == null)
			response = console.readLine("%s: ", prompt);
		else
			response = console.readLine("%s [%s]: ", prompt, defaultValue);
		response = response.trim();
		if (response.isEmpty())
			return defaultValue;
		return response;
	}

	public boolean askYesNo(String prompt, boolean defaultValue) {
		String question = String.format("%s [%s]: ", prompt, defaultValue ? "Y/n" : "y/N");
		while (true) {
			String response = console.readLine(question).trim();
			if (response.isEmpty())
				return defaultValue;
			switch (response.toLowerCase()) {
				case "true":
				case "y":
					return true;
				case "false":
				case "n":
					return false;
			}
		}
	}

	public boolean askYesNo(String prompt) {
		String question = String.format("%s [y/n]: ", prompt);
		while (true) {
			String response = console.readLine(question).trim();
			switch (response.toLowerCase()) {
				case "true":
				case "y":
					return true;
				case "false":
				case "n":
					return false;
			}
		}
	}

}

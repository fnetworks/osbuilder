package org.fnet.osbuilder.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringFormatter {

	private final Pattern formatPattern;

	/**
	 * Constructs a {@link StringFormatter} with a custom pattern.
	 * The pattern has to contain a named group called {@code varName}, e.g. {@code {(?<varName>[a-zA-Z])}}.
	 * @param formatPattern the pattern which detects variable placeholders
	 */
	public StringFormatter(Pattern formatPattern) {
		this.formatPattern = formatPattern;
	}

	/**
	 * Constructs a {@link StringFormatter} with a default pattern.
	 * The pattern replaces all variables surrounded with {@code {}},
	 * and allows for lower and upper case letters, numbers, dashes and underscores for variable names.
	 */
	public StringFormatter() {
		this.formatPattern = Pattern.compile("\\$\\{(?<varName>[a-zA-Z0-9.\\-_]+)}");
	}

	public String format(String s, Map<String, String> params) {
		StringBuilder builder = new StringBuilder();
		Matcher matcher = formatPattern.matcher(s);
		while (matcher.find()) {
			String replacement = params.get(matcher.group("varName"));
			if (replacement == null)
				throw new NullPointerException("Missing value for key " + matcher.group("varName"));
			matcher.appendReplacement(builder, replacement);
		}
		matcher.appendTail(builder);
		return builder.toString();
	}

}

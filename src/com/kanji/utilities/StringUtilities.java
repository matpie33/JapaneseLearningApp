package com.kanji.utilities;

import java.util.Arrays;
import java.util.Collection;

public class StringUtilities {

	private static final String COMMA_SPACE = ", ";
	private static final String COLON_SPACE = ": ";

	public static String joinPropertyAndValue(String property, String value) {
		return value.isEmpty() ? "" : property + COLON_SPACE + value;
	}

	public static String joinPropertyValuePairs(String... propertyValuePairs) {
		return concatenateStrings(Arrays.asList(propertyValuePairs));
	}

	public static String concatenateStrings(Collection<String> strings) {
		StringBuilder builder = new StringBuilder();
		for (String string : strings) {
			if (string.isEmpty()) {
				continue;
			}
			builder.append(string);
			builder.append(COMMA_SPACE);
		}
		if (builder.length() > 0) {
			return builder.toString().substring(0, builder.length() - 2);
		}
		else {
			return "";
		}
	}

	public static String putInNewLine(String expression) {
		return "\n" + expression;
	}

}

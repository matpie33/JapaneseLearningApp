package com.kanji.utilities;

import java.util.List;

public class StringConcatenator {

	public static String concatenateStrings (List<String> strings){
		StringBuilder builder = new StringBuilder();
		for (String string: strings){
			if (string.isEmpty()){
				continue;
			}
			builder.append(string);
			builder.append(", ");
		}
		return builder.toString().substring(0, builder.length()-2);
	}

}

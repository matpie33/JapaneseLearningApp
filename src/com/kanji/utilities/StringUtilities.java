package com.kanji.utilities;

import java.util.List;

public class StringUtilities {

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

	public static boolean characterIsKanji(char character){
		return Character.UnicodeBlock.of(character) == Character.UnicodeBlock.
				CJK_UNIFIED_IDEOGRAPHS;
	}

	public static boolean characterIsKana(char character){
		return Character.UnicodeBlock.of(character) == Character.UnicodeBlock.
				HIRAGANA || Character.UnicodeBlock.of(character) ==
				Character.UnicodeBlock.KATAKANA ;
	}

	public static boolean wordIsInKana (String word){
		for (char c: word.toCharArray()){
			if (!characterIsKana(c)){
				return false;
			}
		}
		return true;
	}

	public static boolean wordIsInKanji (String word){
		boolean anyKanji = false;
		for (char c: word.toCharArray()){
			if (characterIsKanji(c)){
				anyKanji = true;
			}
			if (!characterIsKanji(c) && !characterIsKana(c)){
				return false;
			}
		}
		return anyKanji;
	}

}

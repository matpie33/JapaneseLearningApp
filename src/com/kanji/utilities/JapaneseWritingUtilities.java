package com.kanji.utilities;

import com.kanji.constants.strings.Prompts;

import java.util.Set;

public class JapaneseWritingUtilities {

	public static boolean isInputEmpty(String inputText, boolean isKana) {
		String defaultValue = getDefaultValueForWriting(isKana);
		return inputText.isEmpty() || inputText.equals(defaultValue);
	}

	public static boolean areKanjiWritingsEmpty(Set<String> kanjiWritings) {
		return kanjiWritings.isEmpty() || (kanjiWritings.size() == 1
				&& isInputEmpty(kanjiWritings.iterator().next(), false));
	}

	public static boolean isInputValid(String inputText, boolean isKana) {
		return isKana ? wordIsInKana(inputText) : wordIsInKanji(inputText);
	}

	public static String getDefaultValueForWriting(boolean isKanaWriting) {
		return isKanaWriting ? Prompts.KANA_TEXT : Prompts.KANJI_TEXT;
	}

	public static boolean characterIsKanji(char character) {
		return Character.UnicodeBlock.of(character) == Character.UnicodeBlock.
				CJK_UNIFIED_IDEOGRAPHS || character=='々';
	}

	public static boolean characterIsKana(char character) {
		return Character.UnicodeBlock.of(character) == Character.UnicodeBlock.
				HIRAGANA || Character.UnicodeBlock.of(character)
				== Character.UnicodeBlock.KATAKANA;
	}

	public static boolean wordIsInKana(String word) {
		word = word.trim();
		for (char c : word.toCharArray()) {
			if (!characterIsKana(c)) {
				return false;
			}
		}
		return true;
	}

	public static boolean wordIsInKanji(String word) {
		boolean anyKanji = false;
		word = word.trim();
		for (char c : word.toCharArray()) {
			if (characterIsKanji(c)) {
				anyKanji = true;
			}
			if (!characterIsKanji(c) && !characterIsKana(c)) {
				return false;
			}
		}
		return anyKanji;
	}
}

package com.kanji.utilities;

import com.kanji.constants.enums.TypeOfJapaneseWriting;
import com.kanji.constants.enums.TypeOfWordForRepeating;
import com.kanji.constants.strings.Prompts;

import java.util.Set;

public class JapaneseWritingUtilities {

	public static boolean isKanaEmpty(String kanaValue) {
		return isInputEmpty(kanaValue, TypeOfJapaneseWriting.KANA);
	}

	public static boolean isInputEmpty(String inputText,
			TypeOfJapaneseWriting typeOfJapaneseWriting) {
		String defaultValue = getDefaultValueForWriting(typeOfJapaneseWriting);
		return inputText.isEmpty() || inputText.equals(defaultValue);
	}

	public static boolean areKanjiWritingsEmpty(Set<String> kanjiWritings) {
		return kanjiWritings.isEmpty() || (kanjiWritings.size() == 1
				&& isInputEmpty(kanjiWritings.iterator()
											 .next(),
				TypeOfJapaneseWriting.KANJI));
	}

	public static boolean isInputValid(String inputText,
			TypeOfJapaneseWriting typeOfJapaneseWriting) {
		if (typeOfJapaneseWriting.equals(TypeOfJapaneseWriting.KANA)) {
			return wordIsInKana(inputText);
		}
		else if (typeOfJapaneseWriting.equals(TypeOfJapaneseWriting.KANJI)) {
			return wordIsInKanji(inputText);
		}
		else {
			return wordIsInKana(inputText) || wordIsInKanji(inputText);
		}
	}

	public static String getDefaultValueForWriting(
			TypeOfJapaneseWriting typeOfJapaneseWriting) {
		if (typeOfJapaneseWriting.equals(TypeOfJapaneseWriting.KANA)) {
			return Prompts.KANA_TEXT;
		}
		else if (typeOfJapaneseWriting.equals(TypeOfJapaneseWriting.KANJI)) {
			return Prompts.KANJI_TEXT;
		}
		else {
			return Prompts.KANA_OR_KANJI_TEXT;
		}
	}

	public static boolean characterIsKanji(char character) {
		return Character.UnicodeBlock.of(character) == Character.UnicodeBlock.
				CJK_UNIFIED_IDEOGRAPHS || character == '々';
	}

	private static boolean characterIsKana(char character) {
		return Character.UnicodeBlock.of(character) == Character.UnicodeBlock.
				HIRAGANA || Character.UnicodeBlock.of(character)
				== Character.UnicodeBlock.KATAKANA;
	}

	private static boolean wordIsInKana(String word) {
		word = word.trim();
		for (char c : word.toCharArray()) {
			if (!characterIsKana(c)) {
				return false;
			}
		}
		return true;
	}

	private static boolean wordIsInKanji(String word) {
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

	public static String getTextForTypeOfWordForRepeating(
			TypeOfWordForRepeating typeOfWordForRepeating) {
		return typeOfWordForRepeating.equals(TypeOfWordForRepeating.KANJIS) ?
				Prompts.KANJI :
				Prompts.JAPANESE_WORD;
	}

}

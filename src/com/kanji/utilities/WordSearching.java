package com.kanji.utilities;

import com.kanji.enums.WordSearchOptions;

import java.text.Normalizer;

public class WordSearching {

	private static String removeDiacritics(String word) {
		word = Normalizer.normalize(word, Normalizer.Form.NFD)
				.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		word = word.replace("ł", "l").replace("Ł", "L");
		return word;
	}

	public static boolean doesWordContainSearchedWord(String word, String searched,
			WordSearchOptions options) {
		System.out.println(options);
		word = removeDiacritics(word);
		searched = removeDiacritics(searched);
		switch (options) {
		case BY_WORD:
			return doesPhraseContainSearchedWords(word, searched);
		case BY_FULL_EXPRESSION:
			return doesPhraseEqualToSearchedWords(word, searched);
		default:
			return doesPhraseContainSearchedCharacterChain(word, searched);
		}
	}

	private static boolean doesPhraseContainSearchedWords(String phrase, String searched) {
		return phrase.toLowerCase().matches(".*\\b" + searched.toLowerCase() + "\\b.*");
	}

	private static boolean doesPhraseEqualToSearchedWords(String phrase, String searched) {
		return phrase.equalsIgnoreCase(searched);
	}

	private static boolean doesPhraseContainSearchedCharacterChain(String phrase, String characterChain) {
		return phrase.toLowerCase().contains(characterChain.toLowerCase());
	}

}

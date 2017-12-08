package com.kanji.listSearching;

import com.kanji.enums.SearchOptions;
import com.kanji.listElements.JapaneseWordInformation;

import java.text.Normalizer;

public class JapaneseWordMeaningChecker implements PropertyManager<String, JapaneseWordInformation> {
	//TODO I hate to create a class which is veeery similar to each other for every word element
	private SearchOptions options;

	public JapaneseWordMeaningChecker() {
		this(SearchOptions.BY_FULL_EXPRESSION);
	}

	public JapaneseWordMeaningChecker(SearchOptions options) {
		this.options = options;
	}

	@Override
	public void replaceValueOfProperty(String wordInKanji, JapaneseWordInformation wordInformation) {
		wordInformation.setWordMeaning(wordInKanji);
	}

	@Override
	public boolean isPropertyFound(String wordInKanji, JapaneseWordInformation wordInformation) {
		return wordInformation != null && doesWordContainSearchedWord(
				removeDiacritics(wordInformation.getWordMeaning()), removeDiacritics(wordInKanji), options);
	}

	private String removeDiacritics(String word) {
		word = Normalizer.normalize(word, Normalizer.Form.NFD)
				.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		word = word.replace("ł", "l").replace("Ł", "L");
		return word;
	}

	private boolean doesWordContainSearchedWord(String word, String searched,
			SearchOptions options) {
		switch (options) {
		case BY_WORD:
			return doesPhraseContainSearchedWords(word, searched);
		case BY_FULL_EXPRESSION:
			return doesPhraseEqualToSearchedWords(word, searched);
		default:
			return doesPhraseContainSearchedCharacterChain(word, searched);
		}
	}

	private boolean doesPhraseContainSearchedWords(String phrase, String searched) {
		return phrase.toLowerCase().matches(".*\\b" + searched.toLowerCase() + "\\b.*");
	}

	private boolean doesPhraseEqualToSearchedWords(String phrase, String searched) {
		return phrase.equalsIgnoreCase(searched);
	}

	private boolean doesPhraseContainSearchedCharacterChain(String phrase, String characterChain) {
		return phrase.toLowerCase().contains(characterChain.toLowerCase());
	}

	@Override
	public String convertStringToProperty(String valueToConvert) {
		return valueToConvert;
	}

}

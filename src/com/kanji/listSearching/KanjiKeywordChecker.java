package com.kanji.listSearching;

import java.text.Normalizer;

import com.kanji.Row.KanjiInformation;

public class KanjiKeywordChecker implements PropertyManager<String, KanjiInformation> {

	private SearchOptions options;

	public KanjiKeywordChecker() {
		this(SearchOptions.BY_FULL_EXPRESSION);
	}

	public KanjiKeywordChecker(SearchOptions options) {
		this.options = options;
	}

	@Override
	public void replaceValueOfProperty(String keyWord, KanjiInformation kanjiWord) {
		kanjiWord.setKanjiKeyword(keyWord);
	}

	@Override
	public boolean isPropertyFound(String kanjiKeyWord, KanjiInformation kanjiInformation) {
		return kanjiInformation != null && doesWordContainSearchedWord(
				kanjiInformation.getKanjiKeyword(), removeDiacritics(kanjiKeyWord), options);
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

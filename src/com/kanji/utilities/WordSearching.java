package com.kanji.utilities;

import com.kanji.constants.enums.WordSearchOptions;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElements.ListElement;
import com.kanji.model.ListRow;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.*;

public class WordSearching {

	private static Map<Character, Character> polishDiacriticsMap;

	static {
		polishDiacriticsMap = new HashMap<>();
		polishDiacriticsMap.put('ą', 'a');
		polishDiacriticsMap.put('ć', 'c');
		polishDiacriticsMap.put('ż', 'z');
		polishDiacriticsMap.put('ź', 'z');
		polishDiacriticsMap.put('ł', 'l');
		polishDiacriticsMap.put('ó', 'o');
		polishDiacriticsMap.put('ń', 'n');
		polishDiacriticsMap.put('ś', 's');
		polishDiacriticsMap.put('Ą', 'A');
		polishDiacriticsMap.put('Ć', 'C');
		polishDiacriticsMap.put('Ż', 'Z');
		polishDiacriticsMap.put('Ź', 'Z');
		polishDiacriticsMap.put('Ł', 'L');
		polishDiacriticsMap.put('Ó', 'O');
		polishDiacriticsMap.put('Ń', 'N');
		polishDiacriticsMap.put('Ś', 'S');
	}

	private static String removeDiacriticsAndCapitalLetters(String word) {
		for (Map.Entry<Character, Character> letterAndReplacement : polishDiacriticsMap
				.entrySet()) {
			word = word.replace(letterAndReplacement.getKey(),
					letterAndReplacement.getValue()).toLowerCase();
		}
		return word;
	}

	public static boolean doesAnyOfTheWordsContainSearchedWord(
			Set<String> words, String searched, WordSearchOptions options) {
		for (String word : words) {
			if (doesWordContainSearchedWord(word, searched, options)) {
				return true;
			}
		}
		return false;
	}

	public static boolean doesWordContainSearchedWord(String word,
			String searched, WordSearchOptions options) {
		word = removeDiacriticsAndCapitalLetters(word);
		searched = removeDiacriticsAndCapitalLetters(searched);
		switch (options) {
		case BY_WORD:
			return doesPhraseContainSearchedWords(word, searched);
		case BY_FULL_EXPRESSION:
			return doesPhraseEqualToSearchedWords(word, searched);
		default:
			return doesPhraseContainSearchedCharacterChain(word, searched);
		}
	}

	private static boolean doesPhraseContainSearchedWords(String phrase,
			String searched) {
		return phrase.toLowerCase()
				.matches(".*\\b" + searched.toLowerCase() + "\\b.*");
	}

	private static boolean doesPhraseEqualToSearchedWords(String phrase,
			String searched) {
		return phrase.equalsIgnoreCase(searched);
	}

	private static boolean doesPhraseContainSearchedCharacterChain(
			String phrase, String characterChain) {
		return phrase.toLowerCase().contains(characterChain.toLowerCase());
	}

	public static <Word extends ListElement> List<ListRow<Word>> filterWords(
			List<ListRow<Word>> allWords, String filterText,
			ListElementPropertyManager<?, Word> propertyManagerForInput) {

		List<ListRow<Word>> filteredWords = new ArrayList<>();
		for (ListRow<Word> listRow : allWords) {
			Word word = listRow.getWord();
			String listWordPropertyValue = removeDiacriticsAndCapitalLetters(
					propertyManagerForInput.getPropertyValue(word));
			filterText = removeDiacriticsAndCapitalLetters(filterText);

			String[] wordsInListProperty = splitWords(listWordPropertyValue);
			String[] filterWords = splitWords(filterText);

			int filterWordToCheck = 0;
			for (String meaningWord : wordsInListProperty) {
				if (meaningWord.contains(filterWords[filterWordToCheck])) {
					filterWordToCheck++;
					if (filterWordToCheck > filterWords.length - 1) {
						break;
					}
				}

			}
			if (filterWordToCheck == filterWords.length) {
				filteredWords.add(listRow);
			}

		}

		return filteredWords;

	}

	private static String[] splitWords(String word) {
		CharsetEncoder charsetEncoder = Charset.forName("US-ASCII")
				.newEncoder();
		if (charsetEncoder.canEncode(word)){
			return word.split("\\W+");
		}
		else{
			return word.split(" ");
		}


	}

}

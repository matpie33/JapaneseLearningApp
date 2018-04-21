package com.kanji.utilities;

import com.kanji.constants.enums.AdditionalInformationTag;
import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.enums.VerbConjugationType;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.exception.IncorrectJapaneseWordsListInputException;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.ListElement;
import com.kanji.model.DuplicatedJapaneseWordInformation;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JapaneseWordsFileReader {

	private final String WORD_IN_KANJI_HEADER = "Kanji";
	private final String WORD_IN_KANA_HEADER = "Kana";
	private final String WORD_MEANING_HEADER = "Znaczenia";
	private final String REPEATED_RANGES_HEADER = "Powtarzane";
	private final String SEPARATOR = ",\n";
	private final String VERB_ENDINGS = "[うつるくぐぬぶむす]";

	private BufferedReader in;
	private List<String> wordsInKanjiList = new ArrayList<>();
	private List<String> wordsInKanaList = new ArrayList<>();
	private List<String> meaningsList = new ArrayList<>();
	private List<String> repeatingRangesList = new ArrayList<>();

	private List<JapaneseWord> newWords = new ArrayList<>();
	private List<DuplicatedJapaneseWordInformation> duplicatedWords = new ArrayList<>();

	public void readFiles(File[] files)
			throws IOException, IncorrectJapaneseWordsListInputException {
		for (File file : files) {
			readFile(file);
		}
		checkForErrors();
		changeStringsToJapaneseWordInformation();

	}

	private void changeStringsToJapaneseWordInformation() {
		for (int i = 0; i < wordsInKanaList.size(); i++) {
			JapaneseWord japaneseWord = JapaneseWord.getInitializer()
					.initializeElement();
			String stringInKana = wordsInKanaList.get(i);
			String stringInKanji = wordsInKanjiList.get(i);
			String meaning = meaningsList.get(i);
			char[] particlesToCheck = new char[] { 'に', 'と', 'を' };
			stringInKana = checkIfWordTakesParticles(stringInKana, japaneseWord,
					particlesToCheck);
			stringInKanji = checkIfWordTakesParticles(stringInKanji,
					japaneseWord, particlesToCheck);
			meaning = checkIfWordTakesParticles(meaning, japaneseWord,
					particlesToCheck);
			//TODO can we avoid making 3 calls?

			if (stringInKanji.contains("-")) {
				stringInKanji = stringInKanji.replace("-", "");
			}
			//TODO refactor - use regexes (separator + particle) where separator
			// is (+ , 、) and particle is (と、する、に）etc
			PartOfSpeech partOfSpeech = null;

			if (stringInKana.contains("+な") || stringInKanji.contains("+な")
					|| meaning.contains(("+ な"))
					|| lastCharacterOf(stringInKanji) == '的') {
				stringInKana = removeFromString(stringInKana, "+な");
				stringInKanji = removeFromString(stringInKanji, "+な");
				meaning = removeFromString(meaning, "+ な");
				partOfSpeech = PartOfSpeech.NA_ADJECTIVE;
			}
			if (stringInKana.contains("+する") || stringInKana.contains("、する")
					|| stringInKanji.contains("+する") || stringInKanji
					.contains("、する") || (stringInKana.contains("rzeczownik")
					&& !stringInKana.contains("rzeczownik +")) || stringInKana
					.contains("+の")) {
				stringInKana = removeFromString(stringInKana, "+する",
						"rzeczownik");
				stringInKanji = removeFromString(stringInKanji, "+する",
						"rzeczownik");
				stringInKana = removeFromString(stringInKana, "、する",
						"rzeczownik");
				stringInKanji = removeFromString(stringInKanji, "、する",
						"rzeczownik");
				if (partOfSpeech == null) {
					partOfSpeech = PartOfSpeech.NOUN;
				}

			}
			else if (("" + lastCharacterOfKanjiOrKanaIfKanjiIsEmpty(
					stringInKanji.split("、|,")[0],
					stringInKana.split("、|,")[0])).matches(VERB_ENDINGS)
					|| getLast2CharactersOfWord(stringInKana).equals("ない")) {
				partOfSpeech = PartOfSpeech.VERB;
			}
			else if (lastCharacterOfKanjiOrKanaIfKanjiIsEmpty(stringInKanji,
					stringInKana) == 'い' && !getLast2CharactersOfWord(
					stringInKana).equals("ない")) {
				//TODO maybe classify ない as expression or create another part of speech like:
				// '' imieslow czasownikowy''
				partOfSpeech = PartOfSpeech.I_ADJECTIVE;
			}
			else if (JapaneseWritingUtilities
					.characterIsKanji(lastCharacterOf(stringInKanji))) {
				partOfSpeech = PartOfSpeech.NOUN;
			}
			else {
				partOfSpeech = PartOfSpeech.EXPRESSION;
			}

			setAlternativeWritings(japaneseWord, stringInKanji, stringInKana,
					partOfSpeech);
			japaneseWord.setMeaning(meaning);

			int potentialDuplicateIndex = searchForElementInList(newWords,
					japaneseWord);
			if (potentialDuplicateIndex != -1) {
				duplicatedWords
						.add(new DuplicatedJapaneseWordInformation(japaneseWord,
								potentialDuplicateIndex));
			}
			else {
				newWords.add(japaneseWord);
			}

		}
	}

	private int searchForElementInList(List<? extends ListElement> list,
			ListElement o) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals(o)) {
				return i;
			}
		}
		return -1;
	}

	private String checkIfWordTakesParticles(String wordRepresentation,
			JapaneseWord japaneseWord, char... particles) {
		for (char particle : particles) {
			wordRepresentation = checkForTakingParticleAndModifyString(
					wordRepresentation, particle, japaneseWord);
		}
		return wordRepresentation;
	}

	private String checkForTakingParticleAndModifyString(
			String wordRepresentation, char particle,
			JapaneseWord japaneseWord) {
		if (wordRepresentation.contains("+" + particle) || wordRepresentation
				.contains("+ " + particle)) {
			if (particle == 'と' && wordRepresentation.contains("ところ")) {
				return wordRepresentation;
			}
			wordRepresentation = wordRepresentation.replace("+", "");
			wordRepresentation = wordRepresentation.replace(particle + "", "");
			wordRepresentation = wordRepresentation.replace(",", "");
			japaneseWord.addAditionalInformation(
					AdditionalInformationTag.TAKING_PARTICLE, "" + particle);
		}

		return wordRepresentation;
	}

	private String getLast2CharactersOfWord(String word) {
		int wordLength = word.length();
		return wordLength > 2 ?
				word.substring(wordLength - 2, wordLength) :
				word;
	}

	private void setAlternativeWritings(JapaneseWord japaneseWord,
			String stringInKanji, String stringInKana,
			PartOfSpeech partOfSpeech) {
		String[] alternativeKanjiWritings = splitWordByComma(stringInKanji);
		String[] alternativeKanaWritings = splitWordByComma(stringInKana);

		japaneseWord.setPartOfSpeech(partOfSpeech);
		addWritings(extractAndSetAlternativeWritingsAndConjugationType(
				alternativeKanaWritings, japaneseWord, partOfSpeech),

				extractAndSetAlternativeWritingsAndConjugationType(
						alternativeKanjiWritings, japaneseWord, partOfSpeech),
				japaneseWord);
		//TODO maybe we could do it as a one call
	}

	private void addWritings(String[] kanaWritings, String[] kanjiWritings,
			JapaneseWord japaneseWord) {
		if (kanaWritings.length == kanjiWritings.length) {
			for (int i = 0; i < kanaWritings.length; i++) {
				japaneseWord.addWritings(kanaWritings[i], kanjiWritings[i]);
			}
		}
		else if (kanjiWritings.length > kanaWritings.length) {
			int difference = kanaWritings.length - kanjiWritings.length;
			List<String> kanjiWritingsForKanaWriting = new ArrayList<>();
			for (int i = 0; i < difference; i++) {
				kanjiWritingsForKanaWriting.add(kanjiWritings[i]);
			}
			japaneseWord.addWritings(kanaWritings[0],
					kanjiWritingsForKanaWriting.toArray(new String[] {}));
			for (int i = 1; i < kanaWritings.length; i++) {
				japaneseWord.addWritings(kanaWritings[i],
						kanjiWritings[i + difference]);
			}
		}
		else if (kanjiWritings.length < kanaWritings.length) {
			if (kanjiWritings.length > 1) {
				throw new IllegalStateException("Should not happen");
			}
			for (String s : kanaWritings) {
				japaneseWord.addWritings(s, kanjiWritings[0]);
			}
		}
	}

	private String[] extractAndSetAlternativeWritingsAndConjugationType(
			String[] splittedWords, JapaneseWord japaneseWord,
			PartOfSpeech partOfSpeech) {
		if (splittedWords.length == 1) {
			return splittedWords;
		}
		List<String> alternativeWritings = new ArrayList<>();
		String wordToCompare = splittedWords[0];
		alternativeWritings.add(wordToCompare);
		char lastCharacterOfWordToCompare = lastCharacterOf(wordToCompare);
		boolean isLastCharacterKanji = JapaneseWritingUtilities
				.characterIsKanji(lastCharacterOfWordToCompare);
		boolean foundVerbConjugationType = false;
		for (int i = 1; i < splittedWords.length; i++) {
			String nextWord = splittedWords[i];
			if (nextWord.equals("な")) {
				continue;
			}
			char lastCharacterOfNextWord = lastCharacterOf(nextWord);
			if (partOfSpeech.equals(PartOfSpeech.VERB) && !isLastCharacterKanji
					&& lastCharacterOfNextWord
					!= lastCharacterOfWordToCompare) {
				if (!foundVerbConjugationType) {
					japaneseWord.addAditionalInformation(
							AdditionalInformationTag.VERB_CONJUGATION,
							determineVerbConjugationType(
									nextWord.charAt(nextWord.length() - 2))
									.getDisplayedText());
					foundVerbConjugationType = true;
				}
			}
			else {
				alternativeWritings.add(nextWord);
			}
		}
		//TODO maybe not use list at all then
		return alternativeWritings.toArray(new String[] {});
	}

	private VerbConjugationType determineVerbConjugationType(
			char oneBeforeLastCharacterOfVerb) {
		if (oneBeforeLastCharacterOfVerb == 'っ') {
			return VerbConjugationType.GODAN;
		}
		else {
			return VerbConjugationType.ICHIDAN;
		}
	}

	private String[] splitWordByComma(String stringToCheck) {
		stringToCheck = stringToCheck.replace(" ", "");
		if (stringToCheck.contains(",")) {
			return stringToCheck.split(",");
		}
		else if (stringToCheck.contains("、")) {
			return stringToCheck.split("、");
		}
		else {
			return new String[] { stringToCheck };
		}
	}

	private String removeFromString(String baseString,
			String... partsToRemove) {
		for (String part : partsToRemove) {
			baseString = baseString.replace(part, "");
		}
		return baseString.replace(",", "").
				replace("-", "").trim();
	}

	private char lastCharacterOfKanjiOrKanaIfKanjiIsEmpty(String wordInKanji,
			String wordInKana) {
		return wordInKanji.isEmpty() ?
				lastCharacterOf(wordInKana) :
				lastCharacterOf(wordInKanji);
	}

	private char lastCharacterOf(String word) {
		return word.isEmpty() ? '0' : word.charAt(word.length() - 1);
	}

	private void checkForErrors()
			throws IncorrectJapaneseWordsListInputException {
		String message = "";
		if (wordsInKanjiList.isEmpty()) {
			message += ExceptionsMessages.
					WORDS_IN_KANJI_LIST_NOT_GIVEN + SEPARATOR;
		}
		if (wordsInKanaList.isEmpty()) {
			message += ExceptionsMessages.
					WORDS_IN_KANA_LIST_NOT_GIVEN + SEPARATOR;
		}
		if (meaningsList.isEmpty()) {
			message += ExceptionsMessages.
					WORDS_MEANING_LIST_NOT_GIVEN + SEPARATOR;
		}
		if (message.isEmpty() && (wordsInKanaList.size() != meaningsList.size()
				|| wordsInKanjiList.size() != wordsInKanaList.size())) {
			message += ExceptionsMessages.
					INCORRECT_JAPANESE_WORDS_LISTS_SIZES + SEPARATOR;
		}
		if (!message.isEmpty()) {
			message = message
					.substring(0, message.length() - SEPARATOR.length());
			throw new IncorrectJapaneseWordsListInputException(message);
		}
	}

	private void readFile(File file) throws IOException {
		in = new BufferedReader(
				new InputStreamReader(new FileInputStream(file), "UTF8"));
		String line = in.readLine();
		List<String> listToFill;
		switch (line) {
		case WORD_IN_KANA_HEADER:
			listToFill = wordsInKanaList;
			break;
		case WORD_IN_KANJI_HEADER:
			listToFill = wordsInKanjiList;
			break;
		case WORD_MEANING_HEADER:
			listToFill = meaningsList;
			break;
		case REPEATED_RANGES_HEADER:
			listToFill = repeatingRangesList;
			break;
		default:
			throw new RuntimeException(
					"Invalid header in list or no header at all: " + line);
		}
		while ((line = in.readLine()) != null) {
			listToFill.add(line.trim()
					.replace("＋", "+")); //TODO more complicated for kanji word
		}
		in.close();

	}

	public List<JapaneseWord> getNewWords() {
		return newWords;
	}

	public List<DuplicatedJapaneseWordInformation> getDuplicatedWords() {
		return duplicatedWords;
	}

}

package com.kanji.utilities;

import com.kanji.constants.enums.AdditionalInformationTag;
import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.enums.VerbConjugationType;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.exception.IncorrectJapaneseWordsListInputException;
import com.kanji.list.listElements.JapaneseWordInformation;

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

	public List<JapaneseWordInformation> readFiles(File[] files)
			throws IOException, IncorrectJapaneseWordsListInputException {
		for (File file : files) {
			readFile(file);
		}
		checkForErrors();
		return changeStringsToJapaneseWordInformation();

	}

	private List<JapaneseWordInformation> changeStringsToJapaneseWordInformation() {
		List<JapaneseWordInformation> list = new ArrayList<>();
		for (int i = 0; i < wordsInKanaList.size(); i++) {
			JapaneseWordInformation japaneseWordInformation = JapaneseWordInformation
					.getInitializer().initializeElement();
			String stringInKana = wordsInKanaList.get(i);
			String stringInKanji = wordsInKanjiList.get(i);
			String meaning = meaningsList.get(i);
			char[] particlesToCheck = new char[] { 'に', 'と', 'を' };
			stringInKana = checkIfWordTakesParticles(stringInKana, japaneseWordInformation,
					particlesToCheck);
			stringInKanji = checkIfWordTakesParticles(stringInKanji, japaneseWordInformation,
					particlesToCheck);
			meaning = checkIfWordTakesParticles(meaning, japaneseWordInformation, particlesToCheck);
			//TODO can we avoid making 3 calls?

			if (stringInKanji.contains("-")) {
				stringInKanji = stringInKanji.replace("-", "");
			}
			PartOfSpeech partOfSpeech;
			if (stringInKana.contains("+する") || stringInKanji.contains("+する") || (
					stringInKana.contains("rzeczownik") && !stringInKana.contains("rzeczownik +"))
					|| stringInKana.contains("+の")) {
				stringInKana = removeFromString(stringInKana, "+する", "rzeczownik");
				stringInKanji = removeFromString(stringInKanji, "+する", "rzeczownik");
				partOfSpeech = PartOfSpeech.NOUN;
			}
			else if (stringInKana.contains("+な") || stringInKanji.contains("+な") || meaning
					.contains(("+ な")) || lastCharacterOf(stringInKanji) == '的') {
				stringInKana = removeFromString(stringInKana, "+な");
				stringInKanji = removeFromString(stringInKanji, "+な");
				meaning = removeFromString(meaning, "+ な");
				partOfSpeech = PartOfSpeech.NA_ADJECTIVE;
			}
			else if (("" + lastCharacterOfKanjiOrKanaIfKanjiIsEmpty(stringInKanji, stringInKana))
					.matches(VERB_ENDINGS) || getLast2CharactersOfWord(stringInKana).equals("ない")) {
				partOfSpeech = PartOfSpeech.VERB;
			}
			else if (lastCharacterOfKanjiOrKanaIfKanjiIsEmpty(stringInKanji, stringInKana) == 'い'
					&& !getLast2CharactersOfWord(stringInKana).equals("ない")) {
				//TODO maybe classify ない as expression or create another part of speech like:
				// '' imieslow czasownikowy''
				partOfSpeech = PartOfSpeech.I_ADJECTIVE;
			}
			else if (StringUtilities.characterIsKanji(lastCharacterOf(stringInKanji))) {
				partOfSpeech = PartOfSpeech.NOUN;
			}
			else {
				partOfSpeech = PartOfSpeech.EXPRESSION;
			}
			setAlternativeWritings(japaneseWordInformation, stringInKanji, stringInKana,
					partOfSpeech);
			japaneseWordInformation.setWordMeaning(meaning);
			list.add(japaneseWordInformation);
			System.out.print(japaneseWordInformation);
		}
		return list;
	}

	private String checkIfWordTakesParticles(String wordRepresentation,
			JapaneseWordInformation japaneseWordInformation, char... particles) {
		for (char particle : particles) {
			wordRepresentation = checkForTakingParticleAndModifyString(wordRepresentation, particle,
					japaneseWordInformation);
		}
		return wordRepresentation;
	}

	private String checkForTakingParticleAndModifyString(String wordRepresentation, char particle,
			JapaneseWordInformation japaneseWordInformation) {
		if (wordRepresentation.contains("+" + particle) || wordRepresentation
				.contains("+ " + particle)) {
			if (particle == 'と' && wordRepresentation.contains("ところ")) {
				return wordRepresentation;
			}
			wordRepresentation = wordRepresentation.replace("+", "");
			wordRepresentation = wordRepresentation.replace(particle + "", "");
			wordRepresentation = wordRepresentation.replace(",", "");
			japaneseWordInformation
					.addAditionalInformation(AdditionalInformationTag.TAKING_PARTICLE,
							"" + particle);
		}

		return wordRepresentation;
	}

	private String getLast2CharactersOfWord(String word) {
		int wordLength = word.length();
		return wordLength > 2 ? word.substring(wordLength - 2, wordLength) : word;
	}

	private void setAlternativeWritings(JapaneseWordInformation japaneseWordInformation,
			String stringInKanji, String stringInKana, PartOfSpeech partOfSpeech) {
		String[] alternativeKanjiWritings = splitWordByComma(stringInKanji);
		String[] alternativeKanaWritings = splitWordByComma(stringInKana);

		japaneseWordInformation.setPartOfSpeech(partOfSpeech);
		addWritings(extractAndSetAlternativeWritingsAndConjugationType(alternativeKanaWritings,
				japaneseWordInformation, partOfSpeech),

				extractAndSetAlternativeWritingsAndConjugationType(alternativeKanjiWritings,
						japaneseWordInformation, partOfSpeech), japaneseWordInformation);
		//TODO maybe we could do it as a one call
	}

	private void addWritings(String[] kanaWritings, String[] kanjiWritings,
			JapaneseWordInformation japaneseWordInformation) {
		if (kanaWritings.length == kanjiWritings.length) {
			for (int i = 0; i < kanaWritings.length; i++) {
				japaneseWordInformation.addWritings(kanaWritings[i], kanjiWritings[i]);
			}
		}
		else if (kanjiWritings.length > kanaWritings.length) {
			int difference = kanaWritings.length - kanjiWritings.length;
			List<String> kanjiWritingsForKanaWriting = new ArrayList<>();
			for (int i = 0; i < difference; i++) {
				kanjiWritingsForKanaWriting.add(kanjiWritings[i]);
			}
			japaneseWordInformation.addWritings(kanaWritings[0],
					kanjiWritingsForKanaWriting.toArray(new String[] {}));
			for (int i = 1; i < kanaWritings.length; i++) {
				japaneseWordInformation.addWritings(kanaWritings[i], kanjiWritings[i + difference]);
			}
		}
		else if (kanaWritings.length < kanjiWritings.length) {
			//TODO finish and retest
		}
	}

	private String[] extractAndSetAlternativeWritingsAndConjugationType(String[] splittedWords,
			JapaneseWordInformation japaneseWordInformation, PartOfSpeech partOfSpeech) {
		if (splittedWords.length == 1) {
			return splittedWords;
		}
		List<String> alternativeWritings = new ArrayList<>();
		String wordToCompare = splittedWords[0];
		alternativeWritings.add(wordToCompare);
		char lastCharacterOfWordToCompare = lastCharacterOf(wordToCompare);
		boolean isLastCharacterKanji = StringUtilities
				.characterIsKanji(lastCharacterOfWordToCompare);
		boolean foundVerbConjugationType = false;
		for (int i = 1; i < splittedWords.length; i++) {
			String nextWord = splittedWords[i];
			char lastCharacterOfNextWord = lastCharacterOf(nextWord);
			if (partOfSpeech.equals(PartOfSpeech.VERB) && !isLastCharacterKanji
					&& lastCharacterOfNextWord != lastCharacterOfWordToCompare) {
				if (!foundVerbConjugationType) {
					japaneseWordInformation
							.addAditionalInformation(AdditionalInformationTag.VERB_CONJUGATION,
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

	private VerbConjugationType determineVerbConjugationType(char oneBeforeLastCharacterOfVerb) {
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

	private String removeFromString(String baseString, String... partsToRemove) {
		for (String part : partsToRemove) {
			baseString = baseString.replace(part, "");
		}
		return baseString.replace(",", "").
				replace("-", "").trim();
	}

	private char lastCharacterOfKanjiOrKanaIfKanjiIsEmpty(String wordInKanji, String wordInKana) {
		return wordInKanji.isEmpty() ? lastCharacterOf(wordInKana) : lastCharacterOf(wordInKanji);
	}

	private char lastCharacterOf(String word) {
		return word.isEmpty() ? '0' : word.charAt(word.length() - 1);
	}

	private void checkForErrors() throws IncorrectJapaneseWordsListInputException {
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
			message = message.substring(0, message.length() - SEPARATOR.length());
			throw new IncorrectJapaneseWordsListInputException(message);
		}
	}

	private void readFile(File file) throws IOException {
		in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
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
			throw new RuntimeException("Invalid header in list or no header at all: " + line);
		}
		while ((line = in.readLine()) != null) {
			listToFill.add(line.trim().replace("＋", "+")); //TODO more complicated for kanji word
		}
		in.close();

	}

}

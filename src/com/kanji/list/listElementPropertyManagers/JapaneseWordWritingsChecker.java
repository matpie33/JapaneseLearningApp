package com.kanji.list.listElementPropertyManagers;

import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.model.KanaAndKanjiStrings;
import com.kanji.utilities.StringUtilities;

import javax.swing.text.JTextComponent;
import java.util.*;

public class JapaneseWordWritingsChecker extends WordSearchOptionsHolder
		implements
		ListElementPropertyManager<List<KanaAndKanjiStrings>, JapaneseWordInformation> {
	//TODO I hate to create a class which is veeery similar to each other for every word element
	private String errorDetails = "";
	private boolean addingWord; //TODO rename in other places too (from kanaRequired)
	private static final String DEFAULT_KANJI_INPUT = Prompts.KANJI_TEXT;
	private JapaneseWriting japaneseWritingToCheck;
	private boolean kanaChecker;
	private String previousValue;

	public JapaneseWordWritingsChecker(JapaneseWriting japaneseWritingToCheck,
			boolean addingWord, boolean kanaChecker, String previousValue) {
		this.addingWord = addingWord;
		this.japaneseWritingToCheck = japaneseWritingToCheck;
		this.kanaChecker = kanaChecker;
		this.previousValue = previousValue;
	}

	@Override
	public String getInvalidPropertyReason() {
		return errorDetails;
	}

	@Override
	public boolean isPropertyFound(
			List<KanaAndKanjiStrings> kanaAndKanjiStrings,
			JapaneseWordInformation wordInformation) {

		for (KanaAndKanjiStrings kanaAndKanjiStrings1 : kanaAndKanjiStrings) {
			boolean japaneseWordContainsTheseWritings = false;
			for (Map.Entry<String, Set<String>> kanaToKanjis : wordInformation
					.getKanaToKanjiWritingsMap().entrySet()) {
				Set<String> kanjiWritings = kanaAndKanjiStrings1.getKanji();
				String kanaWriting = kanaAndKanjiStrings1.getKana();
				if (kanaWritingsAreEqualAndKanjiWritingsContainAllOtherKanjiWritings(
						kanaWriting, kanaToKanjis.getKey(), kanjiWritings,
						kanaToKanjis.getValue())) {
					japaneseWordContainsTheseWritings = true;
					break;
				}
			}
			if (!japaneseWordContainsTheseWritings) {
				return false;
			}
		}
		return true;
	}

	private boolean isKanaWritingEmpty(String kanaWriting) {
		return kanaWriting.isEmpty() || kanaWriting.equals(Prompts.KANA_TEXT);
	}

	private boolean areKanjiWritingsEmpty(Set<String> kanjiWritings) {
		return kanjiWritings.isEmpty() || (kanjiWritings.size() == 1
				&& kanjiWritings.iterator().next().equals(Prompts.KANJI_TEXT));
	}

	private boolean kanaWritingsAreEqualAndKanjiWritingsContainAllOtherKanjiWritings(
			String searchedKana, String existingWordKana,
			Set<String> searchedKanji, Set<String> existingKanjiWritings) {
		//TODO move the logic checking if textfield is empty (default value or no value) to one place and use it everywhere, now its scattered

		if (isKanaWritingEmpty(searchedKana)) {
			return areKanjisSame(searchedKanji, existingKanjiWritings);
		}
		else {
			if (searchedKana.equals(existingWordKana)) {
				return areKanjisSame(searchedKanji, existingKanjiWritings);
			}
			else {
				return false;
			}
		}

	}

	private boolean areKanjisSame(Set<String> searchedKanji,
			Set<String> existingKanjiWritings) {
		if (addingWord) {
			if (areKanjiWritingsEmpty(searchedKanji)) {
				return areKanjiWritingsEmpty(existingKanjiWritings);
			}
			else {
				return existingKanjiWritings.containsAll(searchedKanji)
						|| searchedKanji.containsAll(existingKanjiWritings);
			}
		}
		else {
			if (areKanjiWritingsEmpty(searchedKanji)) {
				return true;
			}
			else {
				return existingKanjiWritings.containsAll(searchedKanji);
			}
		}

	}

	@Override
	public List<KanaAndKanjiStrings> validateInputAndConvertToProperty(
			JTextComponent valueToConvert) {
		errorDetails = "";
		boolean kanaModified = false;
		boolean invalidInput = false;
		if (kanaChecker) {
			kanaModified = true;
			String kanaText = valueToConvert.getText();

			if (addingWord && !StringUtilities.wordIsInKana(kanaText)) {
				errorDetails += String
						.format(ExceptionsMessages.KANA_WRITING_INCORRECT,
								kanaText);
				invalidInput = true;
			}
			japaneseWritingToCheck.setKanaWriting(kanaText);
		}
		else {
			kanaModified = false;
			String kanjiText = valueToConvert.getText();
			if (!isKanjiInputEmpty(kanjiText) && !StringUtilities
					.wordIsInKanji(kanjiText)) {
				errorDetails += String
						.format(ExceptionsMessages.KANJI_WRITING_INCORRECT,
								kanjiText);
				invalidInput = true;
			}
			else{
				Set<String> duplicateCheck = new HashSet<>(
						japaneseWritingToCheck.getKanjiWritings());
				duplicateCheck.remove(previousValue);

				if (!duplicateCheck.add(kanjiText)) {
					errorDetails = ExceptionsMessages.DUPLICATED_KANJI_WRITING_WITHIN_ROW;
					return null;
				}
				japaneseWritingToCheck.setKanjiWritings(duplicateCheck);
			}

		}
		if (invalidInput) {
			return null;
		}

		return Arrays.asList(new KanaAndKanjiStrings(
				japaneseWritingToCheck.getKanaWriting(),
				japaneseWritingToCheck.getKanjiWritings(),
				valueToConvert.getText(), kanaModified));

	}

	private boolean isKanjiInputEmpty(String kanjiInput) {
		return kanjiInput.isEmpty() || kanjiInput.equals(DEFAULT_KANJI_INPUT);
	}

	@Override
	public void setProperty(JapaneseWordInformation japaneseWordInformation,
			List<KanaAndKanjiStrings> kanaAndKanjiStringsList) {
		for (KanaAndKanjiStrings kanjiStringsForGivenKana : kanaAndKanjiStringsList) {
			Set<String> kanjiWritings = kanjiStringsForGivenKana.getKanji();
			for (String s : kanjiWritings) {
				if (isKanjiInputEmpty(s)) {
					kanjiWritings.remove(s);
				}
			}
			String kanaWriting = kanjiStringsForGivenKana.getKana();
			japaneseWordInformation.addWritings(kanaWriting,
					kanjiWritings.toArray(new String[] {}));
		}

	}

	@Override
	public void replaceProperty(JapaneseWordInformation propertyHolder,
			List<KanaAndKanjiStrings> oldValueList,
			List<KanaAndKanjiStrings> newValueList) {
		if (oldValueList.size() > 1 || newValueList.size() > 1) {
			throw new IllegalArgumentException(
					"Only 1 value can be changed at a time");
		}
		KanaAndKanjiStrings oldValue = oldValueList.get(0);
		KanaAndKanjiStrings newValue = newValueList.get(0);
		if (oldValue.getModifiedValue().equals(newValue.getModifiedValue())) {
			return;
		}
		Set<String> newKanjiWritings = newValue.getKanji();
		if (newValue.isKanaModified()) {
			propertyHolder.getKanaToKanjiWritingsMap()
					.remove(oldValue.getModifiedValue());
		}
		propertyHolder.getKanaToKanjiWritingsMap()
				.put(newValue.getKana(), newKanjiWritings);
	}

}

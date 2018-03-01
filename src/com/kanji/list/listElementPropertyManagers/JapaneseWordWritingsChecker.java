package com.kanji.list.listElementPropertyManagers;

import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listRows.panelCreators.JapaneseWordPanelCreator;
import com.kanji.model.KanaAndKanjiStrings;
import com.kanji.utilities.StringUtilities;

import javax.swing.text.JTextComponent;
import java.util.*;

public class JapaneseWordWritingsChecker extends WordSearchOptionsHolder
		implements
		ListElementPropertyManager<List<KanaAndKanjiStrings>, JapaneseWordInformation> {
	//TODO I hate to create a class which is veeery similar to each other for every word element
	private JapaneseWordPanelCreator japaneseWordPanelCreator;
	private String errorDetails = "";
	private boolean addingWord; //TODO rename in other places too (from kanaRequired)
	private static final String DEFAULT_KANJI_INPUT = Prompts.KANJI_TEXT;

	public JapaneseWordWritingsChecker(
			JapaneseWordPanelCreator japaneseWordPanelCreator,
			boolean addingWord) {
		this.japaneseWordPanelCreator = japaneseWordPanelCreator;
		this.addingWord = addingWord;
	}

	public void setJapaneseWordPanelCreator(JapaneseWordPanelCreator creator) {
		this.japaneseWordPanelCreator = creator;
	}

	@Override public String getInvalidPropertyReason() {
		return errorDetails;
	}

	@Override public boolean isPropertyFound(
			List<KanaAndKanjiStrings> kanaAndKanjiStrings,
			JapaneseWordInformation wordInformation) {

		for (KanaAndKanjiStrings kanaAndKanjiStrings1 : kanaAndKanjiStrings) {
			boolean japaneseWordContainsTheseWritings = false;
			for (Map.Entry<String, List<String>> kanaToKanjis : wordInformation
					.getKanaToKanjiWritingsMap().entrySet()) {
				List<String> kanjiWritings = kanaAndKanjiStrings1.getKanji();
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

	private boolean areKanjiWritingsEmpty(List<String> kanjiWritings) {
		return kanjiWritings.isEmpty() || (kanjiWritings.size() == 1
				&& kanjiWritings.get(0).equals(Prompts.KANJI_TEXT));
	}

	private boolean kanaWritingsAreEqualAndKanjiWritingsContainAllOtherKanjiWritings(
			String searchedKana, String existingWordKana,
			List<String> searchedKanji, List<String> existingKanjiWritings) {
		//TODO move the logic checking if textfield is empty (default value or no value) to one place and use it everywhere, now its scattered

		if (!addingWord && searchedKana.equals(existingWordKana)
				&& areKanjiWritingsEmpty(searchedKanji)) {
			return true;
		}
		else if (isKanaWritingEmpty(searchedKana) && !areKanjiWritingsEmpty(
				searchedKanji)) {
			return !existingKanjiWritings.isEmpty() && (
					searchedKanji.containsAll(existingKanjiWritings)
							|| existingKanjiWritings
							.containsAll(searchedKanji));
		}
		else if (!isKanaWritingEmpty(searchedKana) && !areKanjiWritingsEmpty(
				searchedKanji)) {
			return searchedKana.equals(existingWordKana)
					&& !existingKanjiWritings.isEmpty() && (
					searchedKanji.containsAll(existingKanjiWritings)
							|| existingKanjiWritings
							.containsAll(searchedKanji));
		}
		else
			return false;

	}

	@Override
	public List<KanaAndKanjiStrings> validateInputAndConvertToProperty(
			JTextComponent valueToConvert) {
		errorDetails = "";
		for (Map.Entry<JTextComponent, List<JTextComponent>> kanaToKanjiWritingsTextFields : japaneseWordPanelCreator
				.getKanaToKanjiWritingsTextComponents().entrySet()) {
			boolean kanaModified = false;
			boolean invalidInput = false;
			boolean foundTextfield = false;
			if (kanaToKanjiWritingsTextFields.getKey().equals(valueToConvert)) {
				kanaModified = true;
				foundTextfield = true;
				String kanaText = valueToConvert.getText();

				if (addingWord && !StringUtilities.wordIsInKana(kanaText)) {
					errorDetails += String
							.format(ExceptionsMessages.KANA_WRITING_INCORRECT,
									kanaText);
					invalidInput = true;
				}
			}
			else if (kanaToKanjiWritingsTextFields.getValue()
					.contains(valueToConvert)) {
				kanaModified = false;
				foundTextfield = true;
				String kanjiText = valueToConvert.getText();
				if (!isKanjiInputEmpty(kanjiText) && !StringUtilities
						.wordIsInKanji(kanjiText)) {
					errorDetails += String
							.format(ExceptionsMessages.KANJI_WRITING_INCORRECT,
									kanjiText);
					invalidInput = true;
				}
			}
			if (invalidInput) {
				return null;
			}
			Set<String> duplicateCheck = new HashSet<>();
			for (JTextComponent textComponent : kanaToKanjiWritingsTextFields
					.getValue()) {
				if (!duplicateCheck.add(textComponent.getText())) {
					errorDetails = ExceptionsMessages.DUPLICATED_KANJI_WRITING_WITHIN_ROW;
					return null;
				}
			}
			if (foundTextfield) {
				return Arrays.asList(new KanaAndKanjiStrings(
						kanaToKanjiWritingsTextFields.getKey(),
						kanaToKanjiWritingsTextFields.getValue(),
						valueToConvert.getText(), kanaModified));
			}
		}

		return null;
	}

	private boolean isKanjiInputEmpty(String kanjiInput) {
		return kanjiInput.isEmpty() || kanjiInput.equals(DEFAULT_KANJI_INPUT);
	}

	@Override
	public void setProperty(JapaneseWordInformation japaneseWordInformation,
			List<KanaAndKanjiStrings> kanaAndKanjiStringsList) {
		for (KanaAndKanjiStrings kanjiStringsForGivenKana : kanaAndKanjiStringsList) {
			List<String> kanjiWritings = kanjiStringsForGivenKana.getKanji();
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
		List<String> newKanjiWritings = newValue.getKanji();
		if (newValue.isKanaModified()) {
			propertyHolder.getKanaToKanjiWritingsMap()
					.remove(oldValue.getModifiedValue());
		}
		propertyHolder.getKanaToKanjiWritingsMap()
				.put(newValue.getKana(), newKanjiWritings);
	}

}

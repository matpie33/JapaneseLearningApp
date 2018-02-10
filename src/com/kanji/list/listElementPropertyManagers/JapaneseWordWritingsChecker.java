package com.kanji.list.listElementPropertyManagers;

import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listRows.panelCreators.JapaneseWordPanelCreator;
import com.kanji.model.KanaAndKanjiStrings;
import com.kanji.utilities.StringUtilities;

import javax.swing.text.JTextComponent;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JapaneseWordWritingsChecker extends WordSearchOptionsHolder
		implements
		ListElementPropertyManager<KanaAndKanjiStrings, JapaneseWordInformation> {
	//TODO I hate to create a class which is veeery similar to each other for every word element
	private JapaneseWordPanelCreator japaneseWordPanelCreator;
	private String errorDetails = "";

	public JapaneseWordWritingsChecker(
			JapaneseWordPanelCreator japaneseWordPanelCreator) {
		this.japaneseWordPanelCreator = japaneseWordPanelCreator;
	}

	@Override public String getInvalidPropertyReason() {
		return errorDetails;
	}

	@Override
	public boolean isPropertyFound(KanaAndKanjiStrings kanaAndKanjiStrings,
			JapaneseWordInformation wordInformation) {
		List<String> kanjiWritings = kanaAndKanjiStrings.getKanji();
		String kanaWriting = kanaAndKanjiStrings.getKana();

		for (Map.Entry<String, List<String>> kanaToKanjis : wordInformation
				.getKanaToKanjiWritingsMap().entrySet()) {
			if (kanaToKanjis.getKey().equals(kanaWriting) && (kanjiWritings
					.isEmpty() && kanaToKanjis.getValue().isEmpty()) || (!kanjiWritings.isEmpty() && (
					kanaToKanjis.getValue().containsAll(kanjiWritings)
							|| kanjiWritings
							.containsAll(kanaToKanjis.getValue())))) {
				return true;
			}
		}
		return false;
	}

	@Override public KanaAndKanjiStrings validateInputAndConvertToProperty(
			JTextComponent valueToConvert) {
		errorDetails = "";
		for (Map.Entry<JTextComponent, List<JTextComponent>> kanaToKanjiWritingsTextFields : japaneseWordPanelCreator
				.getKanaToKanjiWritingsTextComponents().entrySet()) {
			boolean kanaModified = false;
			boolean foundTextField = false;
			boolean invalidInput = false;
			if (kanaToKanjiWritingsTextFields.getKey().equals(valueToConvert)) {
				kanaModified = true;
				foundTextField = true;
				String kanaText = valueToConvert.getText();
				if (!StringUtilities.wordIsInKana(kanaText)) {
					errorDetails += String
							.format(ExceptionsMessages.KANA_WRITING_INCORRECT,
									kanaText);
					invalidInput = true;
				}
			}
			else if (kanaToKanjiWritingsTextFields.getValue()
					.contains(valueToConvert)) {
				kanaModified = false;
				foundTextField = true;
				String kanjiText = valueToConvert.getText();
				if (!kanjiText.isEmpty() && !StringUtilities
						.wordIsInKanji(kanjiText)) {
					errorDetails += String
							.format(ExceptionsMessages.KANJI_WRITING_INCORRECT,
									kanjiText);
					invalidInput = true;
				}
			}
			if (!foundTextField) {
				continue;
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
			if (foundTextField) {
				return new KanaAndKanjiStrings(
						kanaToKanjiWritingsTextFields.getKey(),
						kanaToKanjiWritingsTextFields.getValue(),
						valueToConvert.getText(), kanaModified);
			}

		}
		return null;
	}
	//TODO add validation

	@Override
	public void setProperty(JapaneseWordInformation japaneseWordInformation,
			KanaAndKanjiStrings kanaAndKanjiStrings) {
		List<String> kanjiWritings = kanaAndKanjiStrings.getKanji();
		String kanaWriting = kanaAndKanjiStrings.getKana();
		japaneseWordInformation.addWritings(kanaWriting,
				kanjiWritings.toArray(new String[] {}));
	}

	@Override
	public void replaceProperty(JapaneseWordInformation propertyHolder,
			KanaAndKanjiStrings oldValue, KanaAndKanjiStrings newValue) {
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

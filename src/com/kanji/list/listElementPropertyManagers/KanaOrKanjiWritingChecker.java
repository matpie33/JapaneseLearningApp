package com.kanji.list.listElementPropertyManagers;

import com.guimaker.list.ListElementPropertyManager;
import com.kanji.constants.enums.TypeOfJapaneseWriting;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.utilities.JapaneseWritingUtilities;

import javax.swing.text.JTextComponent;

public class KanaOrKanjiWritingChecker
		implements ListElementPropertyManager<String, JapaneseWord> {

	private String errorDetails = "";

	@Override
	public String getInvalidPropertyReason() {
		return errorDetails;
	}

	@Override
	public boolean isPropertyFound(String property, JapaneseWord wordToCheck,
			JapaneseWord propertyHolder) {
		return false;
	}

	@Override
	public String getPropertyValue(JapaneseWord japaneseWord) {
		StringBuilder writingsText = new StringBuilder();
		for (JapaneseWriting japaneseWriting : japaneseWord.getWritings()) {
			writingsText.append(convertPolishNLetterToJapanese(
					japaneseWriting.getKanaWriting()));
			japaneseWriting.getKanjiWritings()
						   .forEach(kanji -> writingsText.append(" " + kanji));
		}
		return writingsText.toString();
	}


	private String convertPolishNLetterToJapanese(String text) {
		text = text.replace('ｎ', 'ん');
		return text;
	}

	@Override
	public boolean validateInput(JTextComponent textInput,
			JapaneseWord writing) {
		if (JapaneseWritingUtilities.isInputValid(textInput.getText(),
				TypeOfJapaneseWriting.KANA)
				|| JapaneseWritingUtilities.isInputValid(textInput.getText(),
				TypeOfJapaneseWriting.KANJI)) {
			return true;
		}
		else {
			errorDetails = String.format(
					ExceptionsMessages.KANA_OR_KANJI_WRITING_INCORRECT,
					textInput.getText());
			return false;
		}
	}

	@Override
	public String getPropertyDefinedException(String property) {
		throw new IllegalStateException("We don't compare kanji "
				+ "between words. We compare only full writings");
	}

	@Override
	public void setProperty(JapaneseWord writing, String newValue,
			String previousValue) {
		throw new RuntimeException("not implemented");
	}

}

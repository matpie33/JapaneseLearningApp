package com.kanji.list.listElementPropertyManagers;

import com.guimaker.list.ListElementPropertyManager;
import com.kanji.constants.enums.TypeOfJapaneseWriting;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.utilities.JapaneseWritingUtilities;

import javax.swing.text.JTextComponent;

public class KanaWritingChecker
		implements ListElementPropertyManager<String, JapaneseWriting> {

	private String errorDetails = "";

	@Override
	public String getInvalidPropertyReason() {
		return errorDetails;
	}

	@Override
	public boolean isPropertyFound(String property, JapaneseWriting wordToCheck,
			JapaneseWriting propertyHolder) {
		return false;
	}

	@Override
	public String getPropertyValue(JapaneseWriting writing) {
		return writing.getKanaWriting();
	}

	@Override
	public boolean validateInput(JTextComponent textInput,
			JapaneseWriting writing) {
		String text = textInput.getText();
		text = convertPolishNLetterToJapanese(text);
		if (JapaneseWritingUtilities.isInputValid(text,
				TypeOfJapaneseWriting.KANA)) {
			return true;
		}
		else {
			errorDetails = String.format(
					ExceptionsMessages.KANA_WRITING_INCORRECT, text);
			return false;
		}
	}

	@Override
	public String convertToProperty(JTextComponent input) {
		String text = input.getText();
		text = convertPolishNLetterToJapanese(text);
		input.setText(text);
		return text;
	}

	private String convertPolishNLetterToJapanese(String text) {
		text = text.replace('ｎ', 'ん');
		return text;
	}

	@Override
	public String getPropertyDefinedException(String property) {
		throw new IllegalStateException("We don't compare kanji "
				+ "between words. We compare only full writings");
	}

	@Override
	public void setProperty(JapaneseWriting writing, String newValue,
			String previousValue) {
		writing.setKanaWriting(newValue);
	}
}

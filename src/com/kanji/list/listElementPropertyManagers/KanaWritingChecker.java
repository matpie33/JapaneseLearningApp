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
	public String validateInputAndConvertToProperty(JTextComponent textInput,
			JapaneseWriting writing) {
		if (JapaneseWritingUtilities.isInputValid(textInput.getText(),
				TypeOfJapaneseWriting.KANA)) {
			return textInput.getText();
		}
		else {
			errorDetails = String.format(
					ExceptionsMessages.KANA_WRITING_INCORRECT,
					textInput.getText());
		}
		return null;
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

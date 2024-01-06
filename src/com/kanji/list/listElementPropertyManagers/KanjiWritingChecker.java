package com.kanji.list.listElementPropertyManagers;

import com.guimaker.list.ListElementPropertyManager;
import com.kanji.constants.enums.TypeOfJapaneseWriting;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.utilities.JapaneseWritingUtilities;

import javax.swing.text.JTextComponent;

public class KanjiWritingChecker
		implements ListElementPropertyManager<String, JapaneseWriting> {

	private String errorMessage;

	@Override
	public String getInvalidPropertyReason() {
		return errorMessage;
	}

	@Override
	public boolean isPropertyFound(String property, JapaneseWriting wordToCheck,
			JapaneseWriting propertyHolder) {
		return false;
	}

	@Override
	public String getPropertyValue(JapaneseWriting writing) {
		throw new RuntimeException("Not impelemented");
	}

	@Override
	public boolean validateInput(JTextComponent textInput,
			JapaneseWriting writing) {
		if (JapaneseWritingUtilities.isInputValid(textInput.getText(),
				TypeOfJapaneseWriting.KANJI)) {
			if (writing.getKanjiWritings()
					   .contains(textInput.getText())) {
				errorMessage = String.format(
						ExceptionsMessages.DUPLICATED_KANJI_WRITING_WITHIN_ROW,
						textInput.getText());
				return false;
			}
			return true;
		}
		else {
			errorMessage = String.format(
					ExceptionsMessages.KANJI_WRITING_INCORRECT,
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
	public void setProperty(JapaneseWriting writing, String newValue,
			String previousValue) {
		writing.getKanjiWritings()
			   .remove(previousValue != null ? previousValue : "");
		if (!newValue.isEmpty()) {
			writing.getKanjiWritings()
				   .add(newValue);
		}
	}
}

package com.kanji.list.listElementPropertyManagers;

import com.guimaker.list.ListElementPropertyManager;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.list.listElements.Kanji;

import javax.swing.text.JTextComponent;

public class KanjiIdChecker
		implements ListElementPropertyManager<Integer, Kanji> {

	@Override
	public String getInvalidPropertyReason() {
		return "tekst powinien zawierać tylko cyfry";
	}

	@Override
	public boolean isPropertyFound(Integer property, Kanji wordToCheck,
			Kanji propertyHolder) {
		return wordToCheck.getId() == property;
	}

	@Override
	public boolean validateInput(JTextComponent textComponent,
			Kanji propertyHolder) {
		String valueToConvert = textComponent.getText();
		return isIdValidNumber(valueToConvert);
	}

	@Override
	public Integer convertToProperty(JTextComponent textComponent) {
		return textComponent.getText()
							.isEmpty() ?
				0 :
				Integer.parseInt(textComponent.getText());
	}

	@Override
	public String getPropertyValue(Kanji kanji) {
		return "" + kanji.getId();
	}

	@Override
	public void setProperty(Kanji kanji, Integer newValue,
			Integer previousValue) {
		kanji.setId(newValue);
	}

	private boolean isIdValidNumber(String number) {
		boolean valid = number.matches("\\d+");
		return valid;
	}

	@Override
	public String getPropertyDefinedException(Integer id) {
		return String.format(ExceptionsMessages.DUPLICATED_ID_EXCEPTION, id);
	}
}

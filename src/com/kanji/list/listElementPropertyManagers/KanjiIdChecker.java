package com.kanji.list.listElementPropertyManagers;

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
	public boolean isPropertyFound(Integer property,
			Kanji kanji) {
		return kanji.getId() == property;
	}

	@Override
	public Integer validateInputAndConvertToProperty(
			JTextComponent textComponent) {
		String valueToConvert = textComponent.getText();
		boolean isValidNumber = isIdValidNumber(valueToConvert);
		Integer convertedValue = null;
		if (isValidNumber) {
			convertedValue = Integer.parseInt(valueToConvert);
		}
		return convertedValue;
	}

	@Override
	public void setProperty(Kanji kanji,
			Integer propertyValue) {
		kanji.setId(propertyValue);
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

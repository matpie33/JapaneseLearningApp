package com.kanji.list.listElementPropertyManagers;

import com.kanji.list.listElements.KanjiInformation;

import javax.swing.text.JTextComponent;

public class KanjiIdChecker
		implements ListElementPropertyManager<Integer, KanjiInformation> {

	@Override public String getInvalidPropertyReason() {
		return "tekst powinien zawierać tylko cyfry";
	}

	@Override public boolean isPropertyFound(Integer property,
			KanjiInformation kanjiInformation) {
		return kanjiInformation.getKanjiID() == property;
	}

	@Override public Integer validateInputAndConvertToProperty(
			JTextComponent textComponent) {
		String valueToConvert = textComponent.getText();
		boolean isValidNumber = isIdValidNumber(valueToConvert);
		Integer convertedValue = null;
		if (isValidNumber) {
			convertedValue = Integer.parseInt(valueToConvert);
		}
		return convertedValue;
	}

	@Override public void setProperty(KanjiInformation kanjiInformation,
			Integer propertyValue) {
		kanjiInformation.setKanjiID(propertyValue);
	}

	private boolean isIdValidNumber(String number) {
		boolean valid = number.matches("\\d+");
		return valid;
	}

}

package com.kanji.listSearching;

import com.kanji.listElements.KanjiInformation;
import com.kanji.strings.ExceptionsMessages;

import javax.swing.text.JTextComponent;

public class KanjiIdChecker implements PropertyManager<Integer, KanjiInformation> {

	@Override
	public boolean isPropertyFound(Integer property, KanjiInformation kanjiInformation) {
		return kanjiInformation.getKanjiID() == property;
	}

	@Override
	public void replaceValueOfProperty(Integer kanjiId, KanjiInformation kanjiToReplace) {
		kanjiToReplace.setKanjiID(kanjiId);
	}

	@Override
	public Integer convertStringToProperty(String valueToConvert) {
		return Integer.parseInt(valueToConvert);
	}

	@Override
	public boolean tryToReplacePropertyWithValueFromInput (JTextComponent input,
			KanjiInformation propertyHolder){
		boolean isValidNumber =isIdValidNumber(input.getText());
		if (isValidNumber) {
			int number = Integer.parseInt(input.getText());
			propertyHolder.setKanjiID(number);
		}
		return isValidNumber;

	}

	private boolean isIdValidNumber(String number) {
		boolean valid = number.matches("\\d+");
		return valid;
	}

}

package com.kanji.list.listElementPropertyManagers;

import com.kanji.list.listElements.KanjiInformation;

import javax.swing.text.JTextComponent;

public class KanjiIdChecker implements ListElementPropertyManager<Integer, KanjiInformation> {

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
		boolean isValidNumber =isIdValidNumber(valueToConvert);
		Integer convertedValue = null;
		if (isValidNumber) {
			convertedValue = Integer.parseInt(valueToConvert);
		}
		return convertedValue;
	}

	@Override
	public void setPropertyValue(KanjiInformation kanjiInformation, Integer propertyValue) {
		kanjiInformation.setKanjiID(propertyValue);
	}

	private boolean isIdValidNumber(String number) {
		boolean valid = number.matches("\\d+");
		return valid;
	}

}

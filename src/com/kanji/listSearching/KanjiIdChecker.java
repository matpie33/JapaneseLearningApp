package com.kanji.listSearching;

import com.kanji.listElements.KanjiInformation;

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

}

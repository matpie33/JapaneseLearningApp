package com.kanji.listSearching;

import com.kanji.Row.KanjiInformation;

public class KanjiIdChecker implements PropertyManager<Integer, KanjiInformation> {

	@Override
	public boolean isPropertyFound(Integer property, KanjiInformation stringHolder) {
		return stringHolder.getKanjiID() == property;
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

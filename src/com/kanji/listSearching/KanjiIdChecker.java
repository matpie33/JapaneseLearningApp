package com.kanji.listSearching;

import com.kanji.Row.KanjiInformation;

public class KanjiIdChecker implements PropertyChecker<Integer, KanjiInformation> {

	@Override
	public boolean isPropertyFound(Integer property, KanjiInformation stringHolder) {
		return stringHolder.getKanjiID() == property;
	}

}

package com.kanji.list.listElementPropertyManagers;

import com.kanji.list.listElements.KanjiInformation;
import com.kanji.utilities.WordSearching;

import javax.swing.text.JTextComponent;

public class KanjiKeywordChecker extends WordSearchOptionsHolder implements
		ListElementPropertyManager<String, KanjiInformation> {

	@Override
	public void replaceValueOfProperty(String keyWord, KanjiInformation kanjiWord) {
		kanjiWord.setKanjiKeyword(keyWord);
	}

	@Override
	public boolean isPropertyFound(String kanjiKeyWord, KanjiInformation kanjiInformation) {
		return kanjiInformation != null && WordSearching.doesWordContainSearchedWord(
				kanjiInformation.getKanjiKeyword(), kanjiKeyWord, getWordSearchOptions());
	}

	@Override
	public String convertStringToProperty(String valueToConvert) {
		return valueToConvert;
	}

	@Override
	public void setPropertyValue(KanjiInformation kanjiInformation, String propertyValue) {
		kanjiInformation.setKanjiKeyword(propertyValue);
	}



}

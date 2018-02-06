package com.kanji.list.listElementPropertyManagers;

import com.kanji.list.listElements.KanjiInformation;
import com.kanji.utilities.WordSearching;

import javax.swing.text.JTextComponent;

public class KanjiKeywordChecker extends WordSearchOptionsHolder
		implements ListElementPropertyManager<String, KanjiInformation> {

	@Override public String getInvalidPropertyReason() {
		return "tekst powinien zawierać tylko łacińskie znaki";
	}

	@Override
	public boolean isPropertyFound(String kanjiKeyWord, KanjiInformation kanjiInformation) {
		return kanjiInformation != null && WordSearching
				.doesWordContainSearchedWord(kanjiInformation.getKanjiKeyword(), kanjiKeyWord,
						getWordSearchOptions());
	}

	@Override public String validateInputAndConvertToProperty(JTextComponent valueToConvert) {

		return valueToConvert.getText();
	}

	@Override public void setProperty(KanjiInformation kanjiInformation, String propertyValue) {
		kanjiInformation.setKanjiKeyword(propertyValue);
	}

}

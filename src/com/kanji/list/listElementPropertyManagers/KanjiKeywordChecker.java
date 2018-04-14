package com.kanji.list.listElementPropertyManagers;

import com.kanji.list.listElements.Kanji;
import com.kanji.utilities.WordSearching;

import javax.swing.text.JTextComponent;

public class KanjiKeywordChecker extends WordSearchOptionsHolder
		implements ListElementPropertyManager<String, Kanji> {

	@Override
	public String getInvalidPropertyReason() {
		return "tekst powinien zawierać tylko łacińskie znaki";
	}

	@Override
	public boolean isPropertyFound(String kanjiKeyWord,
			Kanji kanji) {
		return kanji != null && WordSearching
				.doesWordContainSearchedWord(kanji.getKeyword(),
						kanjiKeyWord, getWordSearchOptions());
	}

	@Override
	public String validateInputAndConvertToProperty(
			JTextComponent valueToConvert) {

		return valueToConvert.getText();
	}

	@Override
	public void setProperty(Kanji kanji,
			String propertyValue) {
		kanji.setKanjiKeyword(propertyValue);
	}

}

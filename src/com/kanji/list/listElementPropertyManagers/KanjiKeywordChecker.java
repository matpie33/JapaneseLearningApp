package com.kanji.list.listElementPropertyManagers;

import com.guimaker.list.ListElementPropertyManager;
import com.guimaker.utilities.WordSearching;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.list.listElements.Kanji;

import javax.swing.text.JTextComponent;

public class KanjiKeywordChecker extends WordSearchOptionsHolder
		implements ListElementPropertyManager<String, Kanji> {

	@Override
	public String getInvalidPropertyReason() {
		return "tekst powinien zawierać tylko łacińskie znaki";
	}

	@Override
	public boolean isPropertyFound(String kanjiKeyWord, Kanji kanji) {
		return kanji != null && WordSearching.doesWordContainSearchedWord(
				kanji.getKeyword(), kanjiKeyWord, getWordSearchOptions());
	}

	@Override
	public String validateInputAndConvertToProperty(
			JTextComponent valueToConvert, Kanji propertyHolder) {

		return valueToConvert.getText();
	}

	@Override
	public String getPropertyValue(Kanji kanji) {
		return kanji.getKeyword();
	}

	@Override
	public void setProperty(Kanji kanji, String newValue,
			String previousValue) {
		kanji.setKanjiKeyword(newValue);
	}

	@Override
	public String getPropertyDefinedException(String keyword) {
		return String.format(ExceptionsMessages.DUPLICATED_KEYWORD_EXCEPTION,
				keyword);
	}
}

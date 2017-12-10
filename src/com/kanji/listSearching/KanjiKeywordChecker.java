package com.kanji.listSearching;

import com.kanji.enums.WordSearchOptions;
import com.kanji.listElements.KanjiInformation;
import com.kanji.utilities.WordSearching;

import javax.swing.text.JTextComponent;

public class KanjiKeywordChecker extends WordSearchOptionsHolder implements PropertyManager<String, KanjiInformation> {

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
	public boolean tryToReplacePropertyWithValueFromInput (JTextComponent input,
			KanjiInformation propertyHolder) {
		propertyHolder.setKanjiKeyword(input.getText());
		return true; //TODO validate if needed
	}

}

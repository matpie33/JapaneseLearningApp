package com.kanji.list.listElementPropertyManagers;

import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.utilities.WordSearching;

public class JapaneseWordKanjiChecker extends WordSearchOptionsHolder implements
		ListElementPropertyManager<String, JapaneseWordInformation> {
	//TODO I hate to create a class which is veeery similar to each other for every word element

	@Override
	public void replaceValueOfProperty(String wordInKanji, JapaneseWordInformation wordInformation) {
		wordInformation.setWritingsInKanji(new String[] {wordInKanji});
	}

	@Override
	public boolean isPropertyFound(String wordInKanji, JapaneseWordInformation wordInformation) {
		return wordInformation != null && WordSearching.doesWordContainSearchedWord(
				wordInformation.getWritingsInKanji()[0],wordInKanji, getWordSearchOptions());
	}

	@Override
	public String convertStringToProperty(String valueToConvert) {
		return valueToConvert;
	}
	//TODO add validation if needed

	@Override public void setPropertyValue(JapaneseWordInformation japaneseWordInformation,
			String propertyValue) {
		japaneseWordInformation.setWritingsInKanji(new String [] {propertyValue});
	}

}

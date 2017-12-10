package com.kanji.list.listElementPropertyManagers;

import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.utilities.WordSearching;

import javax.swing.text.JTextComponent;

public class JapaneseWordKanjiChecker extends WordSearchOptionsHolder implements
		ListElementPropertyManager<String, JapaneseWordInformation> {
	//TODO I hate to create a class which is veeery similar to each other for every word element

	@Override
	public void replaceValueOfProperty(String wordInKanji, JapaneseWordInformation wordInformation) {
		wordInformation.setWordInKanji(wordInKanji);
	}

	@Override
	public boolean isPropertyFound(String wordInKanji, JapaneseWordInformation wordInformation) {
		return wordInformation != null && WordSearching.doesWordContainSearchedWord(
				wordInformation.getWordInKanji(),wordInKanji, getWordSearchOptions());
	}

	@Override
	public String convertStringToProperty(String valueToConvert) {
		return valueToConvert;
	}

	@Override
	public boolean tryToReplacePropertyWithValueFromInput (JTextComponent input,
			JapaneseWordInformation propertyHolder){
		propertyHolder.setWordInKanji(input.getText());
		return true; //TODO add validation if needed
	}

}

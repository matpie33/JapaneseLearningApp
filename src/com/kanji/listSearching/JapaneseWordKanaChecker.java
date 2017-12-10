package com.kanji.listSearching;

import com.kanji.listElements.JapaneseWordInformation;
import com.kanji.utilities.WordSearching;

import javax.swing.text.JTextComponent;

public class JapaneseWordKanaChecker extends WordSearchOptionsHolder implements PropertyManager<String, JapaneseWordInformation> {
	//TODO I hate to create a class which is veeery similar to each other for every word element

	@Override
	public void replaceValueOfProperty(String wordInKanji, JapaneseWordInformation wordInformation) {
		wordInformation.setWordInKana(wordInKanji);
	}

	@Override
	public boolean isPropertyFound(String wordInKanji, JapaneseWordInformation wordInformation) {
		return wordInformation != null && WordSearching.doesWordContainSearchedWord(
				wordInformation.getWordInKana(), wordInKanji, getWordSearchOptions());
	}

	@Override
	public String convertStringToProperty(String valueToConvert) {
		return valueToConvert;
	}

	@Override
	public boolean tryToReplacePropertyWithValueFromInput (JTextComponent input,
			JapaneseWordInformation propertyHolder){
		propertyHolder.setWordInKana(input.getText());
		return true; //TODO add validation
	}

}

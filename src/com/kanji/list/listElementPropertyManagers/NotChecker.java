package com.kanji.list.listElementPropertyManagers;

import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listElements.ListElement;
import com.kanji.utilities.WordSearching;

import javax.swing.text.JTextComponent;

public class NotChecker extends WordSearchOptionsHolder implements
		ListElementPropertyManager<PartOfSpeech, JapaneseWordInformation> {
	//TODO I hate to create a class which is veeery similar to each other for every word element

	@Override
	public void replaceValueOfProperty(PartOfSpeech wordInKanji, JapaneseWordInformation wordInformation) {
	}

	@Override
	public boolean isPropertyFound(PartOfSpeech wordInKanji, JapaneseWordInformation wordInformation) {
		return false;
	}

	@Override
	public PartOfSpeech convertStringToProperty(String valueToConvert) {

		return PartOfSpeech.getPartOfSpeachByPolishMeaning(valueToConvert);
	}

	@Override public void setPropertyValue(JapaneseWordInformation japaneseWordInformation,
			PartOfSpeech propertyValue) {
		japaneseWordInformation.setPartOfSpeech(propertyValue);
	}

}

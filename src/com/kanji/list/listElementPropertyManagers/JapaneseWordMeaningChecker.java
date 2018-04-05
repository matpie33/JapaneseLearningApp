package com.kanji.list.listElementPropertyManagers;

import com.kanji.constants.enums.WordSearchOptions;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.utilities.WordSearching;

import javax.swing.text.JTextComponent;

public class JapaneseWordMeaningChecker extends WordSearchOptionsHolder
		implements ListElementPropertyManager<String, JapaneseWordInformation> {
	//TODO I hate to create a class which is veeery similar to each other for every word element

	public JapaneseWordMeaningChecker() {
		this(WordSearchOptions.BY_WORD_FRAGMENT);
	}

	public JapaneseWordMeaningChecker(WordSearchOptions options) {
		super(options);
	}

	@Override
	public String getInvalidPropertyReason() {
		return "TODO validacja";
	}

	@Override
	public boolean isPropertyFound(String wordInKanji,
			JapaneseWordInformation wordInformation) {
		return wordInformation != null && WordSearching
				.doesWordContainSearchedWord(wordInformation.getWordMeaning(),
						wordInKanji, getWordSearchOptions());
	}

	@Override
	public String validateInputAndConvertToProperty(
			JTextComponent valueToConvert) {

		return valueToConvert.getText();
	}

	@Override
	public void setProperty(JapaneseWordInformation japaneseWordInformation,
			String propertyValue) {
		japaneseWordInformation.setWordMeaning(propertyValue);
	}

}

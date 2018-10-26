package com.kanji.list.listElementPropertyManagers;

import com.guimaker.enums.WordSearchOptions;
import com.guimaker.list.ListElementPropertyManager;
import com.guimaker.utilities.WordSearching;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.list.listElements.JapaneseWord;

import javax.swing.text.JTextComponent;

public class JapaneseWordMeaningChecker extends WordSearchOptionsHolder
		implements ListElementPropertyManager<String, JapaneseWord> {

	public JapaneseWordMeaningChecker(WordSearchOptions options) {
		super(options);
	}

	@Override
	public String getInvalidPropertyReason() {
		return "TODO validacja";
	}

	@Override
	public boolean isPropertyFound(String wordInKanji,
			JapaneseWord wordInformation) {
		return wordInformation != null
				&& WordSearching.doesWordContainSearchedWord(
				wordInformation.getMeaning(), wordInKanji,
				getWordSearchOptions());
	}

	@Override
	public String getPropertyValue(JapaneseWord japaneseWord) {
		return japaneseWord.getMeaning();
	}

	@Override
	public String validateInputAndConvertToProperty(
			JTextComponent valueToConvert) {

		return valueToConvert.getText();
	}

	@Override
	public void setProperty(JapaneseWord japaneseWord, String propertyValue) {
		japaneseWord.setMeaning(propertyValue);
	}

	@Override
	public String getPropertyDefinedException(String property) {
		return String.format(ExceptionsMessages.DUPLICATED_WORD_MEANING,
				property);
	}
}

package com.kanji.list.listElementPropertyManagers;

import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.utilities.StringUtilities;

import javax.swing.text.JTextComponent;

public class JapaneseWordKanjiChecker extends WordSearchOptionsHolder implements
		ListElementPropertyManager<String, JapaneseWordInformation> {
	@Override public String getInvalidPropertyReason() {
		return "tekst powinien zawierać tylko hiraganę i przynajmniej jeden znak kanji";
	}

	@Override public boolean isPropertyFound(String property,
			JapaneseWordInformation japaneseWordInformation) {
		return false;
	}

	@Override public String convertTextInputToProperty(JTextComponent textInput) {
		if (!StringUtilities.wordIsInKanji(textInput.getText())){
			return null;
		}
		return textInput.getText();
	}

	@Override
	public void setProperty(JapaneseWordInformation japaneseWordInformation, String propertyValue) {

	}
}

package com.kanji.list.myList;

import com.kanji.list.listElements.ListElement;
import com.kanji.model.PropertyPostValidationData;

public interface InputValidationListener<Word extends ListElement> {

	public <WordProperty> void inputValidated(
			PropertyPostValidationData<WordProperty, Word> postValidationData);

}

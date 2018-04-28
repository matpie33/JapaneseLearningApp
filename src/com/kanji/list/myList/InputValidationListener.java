package com.kanji.list.myList;

import com.kanji.list.listElements.ListElement;

public interface InputValidationListener<Word extends ListElement> {

	public void inputValidated (boolean isValid, Word validatedWord);

}

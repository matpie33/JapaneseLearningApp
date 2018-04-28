package com.kanji.list.myList;

import com.kanji.constants.enums.InputGoal;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.ListElement;
import com.kanji.utilities.CommonListElements;

public interface ListRowCreator<Word extends ListElement> {

	public ListRowData createListRow(Word word,
			CommonListElements commonListElements, InputGoal inputGoal);

	public void addValidationListener(
			InputValidationListener<Word> inputValidationListener);
}

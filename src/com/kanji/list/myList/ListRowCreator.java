package com.kanji.list.myList;

import com.guimaker.enums.InputGoal;
import com.guimaker.list.listElements.ListElement;
import com.kanji.list.listeners.InputValidationListener;
import com.guimaker.utilities.CommonListElements;

public interface ListRowCreator<Word extends ListElement> {

	public ListRowData<Word> createListRow(Word word,
			CommonListElements commonListElements, InputGoal inputGoal);

	public void addValidationListener(
			InputValidationListener<Word> inputValidationListener);
}

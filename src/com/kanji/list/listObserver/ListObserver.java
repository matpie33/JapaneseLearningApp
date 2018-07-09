package com.kanji.list.listObserver;

import com.kanji.constants.enums.ListElementModificationType;
import com.kanji.list.listElements.ListElement;

public interface ListObserver<Word extends ListElement> {

	public void update(Word changedListElement,
			ListElementModificationType modificationType);
}

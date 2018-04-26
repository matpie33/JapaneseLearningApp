package com.kanji.list.myList;

import com.kanji.list.listElements.ListElement;
import com.kanji.utilities.CommonListElements;

public interface ListRowCreator<Word extends ListElement> {

	public ListRowData createListRow(Word word,
			CommonListElements commonListElements, boolean forSearchPanel);

}

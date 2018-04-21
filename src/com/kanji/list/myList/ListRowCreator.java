package com.kanji.list.myList;

import com.guimaker.panels.MainPanel;
import com.kanji.list.listElements.ListElement;
import com.kanji.utilities.CommonListElements;

public interface ListRowCreator<Word extends ListElement> {

	public MainPanel createListRow(Word word,
			CommonListElements commonListElements, boolean forSearchPanel);

	public ListRowData getRowData();
}

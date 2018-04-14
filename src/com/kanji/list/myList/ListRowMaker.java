package com.kanji.list.myList;

import com.guimaker.panels.MainPanel;
import com.kanji.list.listElements.ListElement;
import com.kanji.utilities.CommonListElements;

import java.util.Map;

public interface ListRowMaker<Word extends ListElement> {

	public ListRowData createListRow(Word word,
			CommonListElements commonListElements, boolean forSearchPanel);


}

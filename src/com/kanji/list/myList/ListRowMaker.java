package com.kanji.list.myList;

import com.guimaker.panels.MainPanel;
import com.kanji.utilities.CommonListElements;

public interface ListRowMaker<Word> {
	MainPanel createListRow(Word data, CommonListElements commonListElements);

}

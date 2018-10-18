package com.kanji.list.listElements;

import com.guimaker.list.listElements.ListElement;

public interface ListElementInitializer<Element extends ListElement> {
	public Element initializeElement();
}

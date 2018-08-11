package com.kanji.list.listObserver;

import com.kanji.list.listElements.ListElement;

public interface ObservableList<Word extends ListElement> {

	public void addListObserver(ListObserver<Word> observer);
}

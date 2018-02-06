package com.kanji.model;

import com.kanji.list.listElements.ListElement;

public class WordInMyListExistence<Word extends ListElement> {

	private boolean existsInList;
	private Word word;

	public WordInMyListExistence(boolean existsInList, Word word) {
		this.existsInList = existsInList;
		this.word = word;
	}

	public boolean exists() {
		return existsInList;
	}

	public Word getWord() {
		return word;
	}
}

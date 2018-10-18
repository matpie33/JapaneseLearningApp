package com.kanji.model;

import com.guimaker.list.listElements.ListElement;

public class WordInMyListExistence<Word extends ListElement> {

	private boolean existsInList;
	private Word word;
	private int oneBasedRowNumber;

	public WordInMyListExistence(boolean existsInList, Word word,
			int oneBasedRowNumber) {
		this.existsInList = existsInList;
		this.word = word;
		this.oneBasedRowNumber = oneBasedRowNumber;
	}

	public boolean exists() {
		return existsInList;
	}

	public Word getWord() {
		return word;
	}

	public int getOneBasedRowNumber() {
		return oneBasedRowNumber;
	}

	public void clearRowNumber() {
		oneBasedRowNumber = -1;
	}

}

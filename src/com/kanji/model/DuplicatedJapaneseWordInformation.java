package com.kanji.model;

import com.guimaker.list.ListElement;
import com.kanji.list.listElements.JapaneseWord;

public class DuplicatedJapaneseWordInformation implements ListElement {

	private JapaneseWord japaneseWord;
	private int duplicatedWordRowNumber;

	public DuplicatedJapaneseWordInformation(JapaneseWord japaneseWord,
			int duplicatedWordRowNumber) {
		this.japaneseWord = japaneseWord;
		this.duplicatedWordRowNumber = duplicatedWordRowNumber;
	}


	public JapaneseWord getJapaneseWord() {
		return japaneseWord;
	}

	public int getDuplicatedWordRowNumber() {
		return duplicatedWordRowNumber;
	}

	@Override
	public boolean isEmpty() {
		return japaneseWord == null || duplicatedWordRowNumber == 0;
	}

	@Override
	public String getDisplayedText() {
		throw new IllegalStateException(
				"Duplicated words are not used anymore");
	}
}

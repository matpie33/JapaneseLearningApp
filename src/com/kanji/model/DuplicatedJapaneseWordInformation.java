package com.kanji.model;

import com.kanji.list.listElements.JapaneseWord;
import com.guimaker.list.listElements.ListElement;

public class DuplicatedJapaneseWordInformation implements ListElement {

	private JapaneseWord japaneseWord;
	private int duplicatedWordRowNumber;
	public static final String MEANINGFUL_NAME = "Duplikat japońskiego słowa";

	public DuplicatedJapaneseWordInformation(JapaneseWord japaneseWord,
			int duplicatedWordRowNumber) {
		this.japaneseWord = japaneseWord;
		this.duplicatedWordRowNumber = duplicatedWordRowNumber;
	}

	@Override
	public String getMeaningfulName() {
		return MEANINGFUL_NAME;
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

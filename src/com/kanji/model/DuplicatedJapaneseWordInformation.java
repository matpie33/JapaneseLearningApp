package com.kanji.model;

import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listElements.ListElement;

public class DuplicatedJapaneseWordInformation implements ListElement {

	private JapaneseWordInformation japaneseWordInformation;
	private int duplicatedWordRowNumber;

	public DuplicatedJapaneseWordInformation(
			JapaneseWordInformation japaneseWordInformation,
			int duplicatedWordRowNumber) {
		this.japaneseWordInformation = japaneseWordInformation;
		this.duplicatedWordRowNumber = duplicatedWordRowNumber;
	}

	public JapaneseWordInformation getJapaneseWordInformation() {
		return japaneseWordInformation;
	}

	public int getDuplicatedWordRowNumber() {
		return duplicatedWordRowNumber;
	}

	@Override
	public boolean isSameAs(ListElement element) {
		return false; //TODO implement if needed
	}
}

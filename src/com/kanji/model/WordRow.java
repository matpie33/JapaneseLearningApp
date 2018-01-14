package com.kanji.model;

import com.kanji.list.listElements.KanjiInformation;
import com.kanji.list.listElements.ListElement;

import java.util.Objects;

public class WordRow<Element extends ListElement> {
	private KanjiInformation listElement;
	private int rowNumber;

	public WordRow(KanjiInformation listElement, int rowNumber) {
		this.listElement = listElement;
		this.rowNumber = rowNumber;
	}

	public int getRowNumber() {
		return rowNumber;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof WordRow == false) {
			return false;
		}
		WordRow row = (WordRow) o;
		return row.getListElement().equals(listElement);
	}

	@Override
	public int hashCode() {
		return Objects.hash(listElement.getKanjiID(),
				listElement.getKanjiKeyword());
	}

	public KanjiInformation getListElement() {
		return listElement;
	}
}

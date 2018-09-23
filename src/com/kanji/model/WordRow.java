package com.kanji.model;

import com.kanji.list.listElements.ListElement;

import java.util.Objects;

public class WordRow<Element extends ListElement> {
	private Element listElement;
	private int rowNumber;

	public WordRow(Element listElement, int rowNumber) {
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
		return Objects.hash(listElement);
	}

	public Element getListElement() {
		return listElement;
	}

	@Override
	public String toString (){
		return listElement.toString();
	}
}

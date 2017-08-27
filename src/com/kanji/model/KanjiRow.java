package com.kanji.model;

import java.util.Objects;

public class KanjiRow {
	private int kanjiId;
	private int rowNumber;

	public KanjiRow(int kanjiId, int rowNumber) {
		this.kanjiId = kanjiId;
		this.rowNumber = rowNumber;
	}

	public int getId() {
		return kanjiId;
	}

	public int getRowNumber() {
		return rowNumber;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof KanjiRow == false) {
			return false;
		}
		KanjiRow row = (KanjiRow) o;
		return row.getId() == kanjiId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(kanjiId);
	}

}

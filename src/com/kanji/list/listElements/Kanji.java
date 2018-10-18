package com.kanji.list.listElements;

import com.guimaker.list.listElements.ListElement;
import com.kanji.utilities.StringUtilities;

import java.io.Serializable;
import java.util.Objects;

public class Kanji implements Serializable, ListElement {

	private static final long serialVersionUID = 5172798853536032765L;
	private static final String KEYWORD = "słowo kluczowe";
	private static final String ID = "numer";
	private String keyword;
	private int id;
	public final static String MEANINGFUL_NAME = "Kanji";

	public Kanji(String keyword, int iD) {
		this.keyword = keyword;
		this.id = iD;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKanjiKeyword(String kanjiKeyword) {
		this.keyword = kanjiKeyword;
	}

	@Override
	public String getMeaningfulName() {
		return MEANINGFUL_NAME;
	}

	public int getId() {
		return id;
	}

	public void setId(int kanjiID) {
		this.id = kanjiID;
	}

	@Override
	public boolean equals(Object another) {
		if (!(another instanceof Kanji)) {
			return false;
		}
		Kanji kanji = (Kanji) another;
		return kanji.getKeyword().equals(keyword) && kanji.getId() == id;

	}

	@Override
	public int hashCode() {
		return Objects.hash(id, keyword);
	}

	@Override
	public String toString() {
		return "Keyword: " + keyword + " int: " + id;
	}

	public static ListElementInitializer<Kanji> getInitializer() {
		return () -> new Kanji("", 0);
	}

	@Override
	public boolean isEmpty() {
		return id == 0 || keyword.isEmpty();
	}

	@Override
	public String getDisplayedText() {
		return StringUtilities.joinPropertyValuePairs(
				StringUtilities.joinPropertyAndValue(KEYWORD, getKeyword()),
				StringUtilities
						.joinPropertyAndValue(ID, Integer.toString(getId())));
	}
}

package com.kanji.list.listElements;

import com.kanji.utilities.StringUtilities;

import java.io.Serializable;
import java.util.Objects;

public class Kanji implements Serializable, ListElement {

	private static final long serialVersionUID = 5172798853536032765L;
	private static final String KEYWORD = "słowo kluczowe";
	private static final String ID = "numer";
	private String keyword;
	private int id;

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

	//TODO it's probably beter to override equals and hashcode and use set instead of lists
	@Override
	public boolean isSameAs(ListElement element) {
		if (element instanceof Kanji) {
			return ((Kanji) element).getId() == id || ((Kanji) element)
					.getKeyword().equals(keyword);
		}
		return false;
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

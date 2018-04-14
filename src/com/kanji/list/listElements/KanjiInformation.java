package com.kanji.list.listElements;

import java.io.Serializable;
import java.util.Objects;

public class KanjiInformation implements Serializable, ListElement {

	private static final long serialVersionUID = 5172798853536032765L;
	private String keyword;
	private int id;

	public KanjiInformation(String keyword, int iD) {
		this.keyword = keyword;
		this.id = iD;
	}

	public String getKanjiKeyword() {
		return keyword;
	}

	public void setKanjiKeyword(String kanjiKeyword) {
		this.keyword = kanjiKeyword;
	}

	public int getKanjiID() {
		return id;
	}

	public void setKanjiID(int kanjiID) {
		this.id = kanjiID;
	}

	@Override
	public boolean equals(Object another) {
		if (!(another instanceof KanjiInformation)) {
			return false;
		}
		KanjiInformation kanjiInformation = (KanjiInformation) another;
		return kanjiInformation.getKanjiKeyword().equals(keyword)
				&& kanjiInformation.getKanjiID() == id;

	}

	@Override
	public int hashCode() {
		return Objects.hash(id, keyword);
	}

	@Override
	public String toString() {
		return "Keyword: " + keyword + " int: " + id;
	}

	public static ListElementInitializer<KanjiInformation> getInitializer() {
		return () -> new KanjiInformation("", 0);
	}

	//TODO it's probably beter to override equals and hashcode and use set instead of lists
	@Override
	public boolean isSameAs(ListElement element) {
		if (element instanceof KanjiInformation) {
			return ((KanjiInformation) element).getKanjiID() == id
					|| ((KanjiInformation) element).getKanjiKeyword()
					.equals(keyword);
		}
		return false;
	}

	@Override
	public boolean isEmpty() {
		return id == 0 || keyword.isEmpty();
	}
}

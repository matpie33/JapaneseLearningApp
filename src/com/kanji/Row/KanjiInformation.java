package com.kanji.Row;

import java.io.Serializable;
import java.util.Objects;

public class KanjiInformation implements Serializable {

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
	public boolean equals (Object another){
		if (!(another instanceof KanjiInformation)){
			return false;
		}
		KanjiInformation kanjiInformation = (KanjiInformation) another;
		return kanjiInformation.getKanjiKeyword().equals(keyword) && kanjiInformation.getKanjiID() == id;

	}

	@Override
	public int hashCode(){
		return Objects.hash(id, keyword);
	}

	@Override
	public String toString() {
		return "Keyword: " + keyword + " int: " + id;
	}

}

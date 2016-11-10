package com.kanji.Row;

import java.io.Serializable;

public class KanjiInformation implements Serializable {
	
	private String keyword;
	private int id;
	
	public KanjiInformation(String keyword, int iD){
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
	public String toString(){
		return "Keyword: "+keyword + " int: "+id;
	}

}

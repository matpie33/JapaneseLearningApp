package com.kanji.listElements;

import com.kanji.enums.ListElementType;
import com.kanji.listSearching.KanjiIdChecker;
import com.kanji.listSearching.KanjiKeywordChecker;
import com.kanji.strings.Labels;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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

	public static List<ListElementData> getElementsTypesAndLabels() {
		List <ListElementData> listElementData = new ArrayList<>();
		listElementData.add(new ListElementData(Labels.KANJI_KEYWORD_LABEL, new KanjiKeywordChecker(),
				ListElementType.STRING_LONG_WORD, Labels.COMBOBOX_OPTION_SEARCH_BY_KEYWORD));
		listElementData.add(new ListElementData(Labels.KANJI_ID_LABEL, new KanjiIdChecker(),
				ListElementType.NUMERIC_INPUT, Labels.COMBOBOX_OPTION_SEARCH_BY_KANJI_ID));
		return listElementData;
	}

	//TODO it's probably beter to override equals and hashcode and use set instead of lists
	@Override public boolean isSameAs(ListElement element) {
		if (element instanceof KanjiInformation){
			return ((KanjiInformation)element).getKanjiID() == id;
		}
		return false;
	}

}

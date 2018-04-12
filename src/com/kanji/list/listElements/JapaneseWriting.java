package com.kanji.list.listElements;

import com.kanji.constants.enums.PartOfSpeech;

import java.util.*;

public class JapaneseWriting implements ListElement {

	private String kanaWriting;
	private Set<String> kanjiWritings;

	public JapaneseWriting(String kanaWriting, Set<String> kanjiWritings) {
		this.kanaWriting = kanaWriting;
		this.kanjiWritings = kanjiWritings;
	}

	@Override
	public boolean isEmpty (){
		return kanaWriting.isEmpty() && kanjiWritings.isEmpty();
	}

	public String getKanaWriting() {
		return kanaWriting;
	}

	public void setKanaWriting (String kanaWriting){
		this.kanaWriting = kanaWriting;
	}

	public Set<String> getKanjiWritings() {
		return kanjiWritings;
	}

	public static ListElementInitializer<JapaneseWriting> getInitializer() {
		return () -> {
			Set<String> arrayList = new HashSet<>();
			arrayList.add("");
			return new JapaneseWriting("", arrayList);
		};
	}

	@Override
	public boolean isSameAs(ListElement element) {
		return false;
	}

	public void setKanjiWritings(Set<String> kanjiWritings) {
		this.kanjiWritings = kanjiWritings;
	}
}

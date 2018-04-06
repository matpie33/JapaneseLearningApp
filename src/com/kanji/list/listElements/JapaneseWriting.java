package com.kanji.list.listElements;

import com.kanji.constants.enums.PartOfSpeech;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JapaneseWriting implements ListElement {

	private String kanaWriting;
	private List<String> kanjiWritings;

	public JapaneseWriting(String kanaWriting, List<String> kanjiWritings) {
		this.kanaWriting = kanaWriting;
		this.kanjiWritings = kanjiWritings;
	}

	public boolean isEmpty (){
		return kanaWriting.isEmpty() && kanjiWritings.isEmpty();
	}

	public String getKanaWriting() {
		return kanaWriting;
	}

	public List<String> getKanjiWritings() {
		return kanjiWritings;
	}

	public static ListElementInitializer<JapaneseWriting> getInitializer() {
		return () -> new JapaneseWriting("", Arrays.asList(""));
	}

	@Override
	public boolean isSameAs(ListElement element) {
		return false;
	}
}

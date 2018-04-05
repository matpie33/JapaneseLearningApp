package com.kanji.list.listElements;

import com.kanji.constants.enums.PartOfSpeech;

import java.util.ArrayList;
import java.util.List;

public class JapaneseWriting implements ListElement {

	private String kanaWriting;
	private List<String> kanjiWritings;

	public JapaneseWriting(String kanaWriting, List<String> kanjiWritings) {
		this.kanaWriting = kanaWriting;
		this.kanjiWritings = kanjiWritings;
	}

	public String getKanaWriting() {
		return kanaWriting;
	}

	public List<String> getKanjiWritings() {
		return kanjiWritings;
	}

	public static ListElementInitializer<JapaneseWriting> getInitializer() {
		return () -> new JapaneseWriting("", new ArrayList<>());
	}

	@Override
	public boolean isSameAs(ListElement element) {
		return false;
	}
}

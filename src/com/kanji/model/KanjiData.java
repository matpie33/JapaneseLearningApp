package com.kanji.model;

import com.kanji.list.listElements.Kanji;

public class KanjiData {

	private String kanjiCharacter;
	private Kanji kanji;

	public KanjiData(String kanjiCharacter, Kanji kanji) {
		this.kanjiCharacter = kanjiCharacter;
		this.kanji = kanji;
	}

	public String getKanjiCharacter() {
		return kanjiCharacter;
	}

	public Kanji getKanji() {
		return kanji;
	}
}

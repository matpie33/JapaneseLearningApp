package com.kanji.context;

public class KanjiContext {

	private String kanjiCharacter;
	private int kanjiId;

	public KanjiContext(String kanjiCharacter, int kanjiId){
		this.kanjiCharacter = kanjiCharacter;
		this.kanjiId = kanjiId;
	}

	public String getKanjiCharacter() {
		return kanjiCharacter;
	}

	public int getKanjiId() {
		return kanjiId;
	}

	public static KanjiContext emptyContext (){
		return new KanjiContext("", 0);
	}

	public boolean isEmpty (){
		return kanjiCharacter.isEmpty() && kanjiId == 0;
	}

}

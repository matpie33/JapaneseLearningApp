package com.kanji.listElements;

public class JapaneseWordInformation {

	private String wordInKana;
	private String wordInKanji;
	private String wordMeaning;

	public JapaneseWordInformation (String wordInKana, String wordMeaning){

		this.wordInKana = wordInKana;
		this.wordMeaning = wordMeaning;
	}

	public JapaneseWordInformation (String wordInKanji, String wordInKana, String wordMeaning){
		this(wordInKana, wordMeaning);
		this.wordInKanji = wordInKanji;
	}

	public String getWordInKana() {
		return wordInKana;
	}

	public String getWordMeaning() {
		return wordMeaning;
	}

	public String getWordInKanji() {
		return wordInKanji;
	}

	public boolean hasKanjiWriting(){
		return wordInKanji != null;
	}

}

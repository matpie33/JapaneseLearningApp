package com.kanji.constants;

public enum SavingStatus {
	Saving("Zapisywanie") , Saved("Zapisano"), NoChanges("Brak zmian");
	private String text;
	private SavingStatus (String text){
		this.text=text;
	}
	public String getText(){
		return text;
	}
}

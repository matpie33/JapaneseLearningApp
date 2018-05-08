package com.kanji.constants.enums;

public enum JapaneseParticle {
	TO("To"), NI("Ni"), DE("De");

	private String displayedValue;

	private JapaneseParticle(String displayedValue) {
		this.displayedValue = displayedValue;
	}

	public String getDisplayedValue() {
		return displayedValue;
	}
}

package com.kanji.constants.enums;

public enum JapaneseParticle {
	TO("To"), NI("Ni"), DE("De"), EMPTY ("");

	private String displayedValue;

	private JapaneseParticle(String displayedValue) {
		this.displayedValue = displayedValue;
	}

	public String getDisplayedValue() {
		return displayedValue;
	}

	public static JapaneseParticle getByString(String value) {
		for (JapaneseParticle japaneseParticle : JapaneseParticle.values()) {
			if (japaneseParticle.getDisplayedValue().equals(value)) {
				return japaneseParticle;
			}
		}
		throw new IllegalArgumentException("Value not found: " + value);
	}

}

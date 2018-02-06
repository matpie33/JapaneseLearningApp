package com.kanji.constants.enums;

public enum PartOfSpeech {

	VERB("Czasownik"), NOUN("rzeczownik"), I_ADJECTIVE("i-przymiotnik"), NA_ADJECTIVE(
			"na-przymiotnik"), EXPRESSION("Wyrażenie");

	private String polishMeaning;

	private PartOfSpeech(String polishMeaning) {
		this.polishMeaning = polishMeaning;
	}

	public String getPolishMeaning() {
		return polishMeaning;
	}

	public static PartOfSpeech getPartOfSpeachByPolishMeaning(String polishMeaning) {
		for (PartOfSpeech partOfSpeech : values()) {
			if (partOfSpeech.getPolishMeaning().equals(polishMeaning)) {
				return partOfSpeech;
			}
		}
		throw new RuntimeException(
				"Could not find part of speech by polish meaning: " + polishMeaning);
	}

}

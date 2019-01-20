package com.kanji.constants.enums;

public enum EnglishTranslationDirection {
	TO_POLISH("eng", "pol"), TO_ENGLISH("pol", "eng");

	private String sourceLanguageAbbreviation;
	private String destionationLanguageAbbreviation;

	EnglishTranslationDirection(String sourceLanguageAbbreviation,
			String destionationLanguageAbbreviation) {
		this.sourceLanguageAbbreviation = sourceLanguageAbbreviation;
		this.destionationLanguageAbbreviation = destionationLanguageAbbreviation;
	}

	public String getSourceLanguageAbbreviation() {
		return sourceLanguageAbbreviation;
	}

	public String getDestionationLanguageAbbreviation() {
		return destionationLanguageAbbreviation;
	}
}

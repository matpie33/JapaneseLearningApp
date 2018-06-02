package com.kanji.constants.enums;

import com.kanji.constants.strings.Labels;

public enum PartOfSpeech {

	VERB("Czasownik", AdditionalInformationTag.VERB_CONJUGATION,
			new String[] { Labels.VERB_CONJUGATION_GODAN,
					Labels.VERB_CONJUGATION_ICHIDAN }), NOUN("rzeczownik",
			AdditionalInformationTag.TAKING_SURU,
			new String[] { Labels.NO, Labels.YES }), I_ADJECTIVE(
			"i-przymiotnik", AdditionalInformationTag.OTHER,
			new String[] {}), NA_ADJECTIVE("na-przymiotnik",
			AdditionalInformationTag.OTHER, new String[] {}), EXPRESSION(
			"Wyrażenie", AdditionalInformationTag.TAKING_SURU,
			new String[] { Labels.NO, Labels.YES });

	private String polishMeaning;

	private AdditionalInformationTag additionalInformationTag;
	private String[] possibleValues;

	private PartOfSpeech(String polishMeaning,
			AdditionalInformationTag additionalInformationTag,
			String[] possibleValues) {
		this.polishMeaning = polishMeaning;
		this.possibleValues = possibleValues;
		this.additionalInformationTag = additionalInformationTag;
	}

	public String getPolishMeaning() {
		return polishMeaning;
	}

	public static PartOfSpeech getPartOfSpeachByPolishMeaning(
			String polishMeaning) {
		for (PartOfSpeech partOfSpeech : values()) {
			if (partOfSpeech.getPolishMeaning().equals(polishMeaning)) {
				return partOfSpeech;
			}
		}
		throw new RuntimeException(
				"Could not find part of speech by polish meaning: "
						+ polishMeaning);
	}

	public String[] getPossibleValues() {
		return possibleValues;
	}

	public AdditionalInformationTag getAdditionalInformationTag() {
		return additionalInformationTag;
	}
}

package com.kanji.constants.enums;

import com.kanji.constants.strings.Labels;

public enum AdditionalInformationTag {

	VERB_CONJUGATION(
			Labels.VERB_CONJUGATION), TAKING_PARTICLE(Labels.TAKING_PARTICLE);

	private String tagLabel;

	private AdditionalInformationTag(String label) {
		tagLabel = label;
	}

}

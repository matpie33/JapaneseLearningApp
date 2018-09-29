package com.kanji.constants.enums;

import com.kanji.constants.strings.Labels;

public enum AdditionalInformationTag {

	VERB_CONJUGATION(Labels.VERB_CONJUGATION), TAKING_SURU(
			Labels.TAKING_SURU_SUFFIX), OTHER(
			Labels.ADDITIONAL_INFORMATION_GENERAL_TAG);

	private String tagLabel;

	private AdditionalInformationTag(String label) {
		tagLabel = label;
	}

	public String getLabel() {
		return tagLabel;
	}

}

package com.kanji.constants.enums;

import com.kanji.constants.strings.Labels;

public enum AdditionalInformationTag {

	//TODO alternative kanji/kana writing doesn't have to be additional information
	//TODO no.2: verb conjugation should only be allowed for verbs, etc.
	ALTERNATIVE_KANJI_WRITING(
			Labels.ALTERNATIVE_KANJI_WRITING), ALTERNATIVE_KANA_WRTING(
			Labels.ALTERNATIVE_KANA_WRITING), VERB_CONJUGATION(
			Labels.VERB_CONJUGATION), TAKING_PARTICLE(Labels.TAKING_PARTICLE);

	private String tagLabel;

	private AdditionalInformationTag(String label) {
		tagLabel = label;
	}

}

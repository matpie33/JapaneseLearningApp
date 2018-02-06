package com.kanji.constants.enums;

import com.kanji.constants.strings.Labels;

public enum VerbConjugationType {

	ICHIDAN(Labels.VERB_CONJUGATION_ICHIDAN), GODAN(Labels.VERB_CONJUGATION_GODAN);

	private String displayedText;

	private VerbConjugationType(String displayedText) {
		this.displayedText = displayedText;
	}

	public String getDisplayedText() {
		return displayedText;
	}

}

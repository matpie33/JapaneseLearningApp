package com.kanji.constants.enums;

public enum TypeOfWordForRepeating {
	KANJIS(ApplicationSaveableState.REVIEWING_PROBLEMATIC_KANJIS), JAPANESE_WORDS(
			ApplicationSaveableState.REVIEWING_PROBLEMATIC_KANJIS);

	private ApplicationSaveableState associatedSaveableState;



	private TypeOfWordForRepeating(ApplicationSaveableState
			associatedSaveableState) {

		this.associatedSaveableState = associatedSaveableState;
	}

	public ApplicationSaveableState getAssociatedSaveableState() {
		return associatedSaveableState;
	}

}

package com.kanji.constants.enums;

public enum TypeOfWordForRepeating {
	KANJIS(ApplicationSaveableState.REPEATING_KANJI), JAPANESE_WORDS(
			ApplicationSaveableState.REPEATING_JAPANESE_WORDS);

	private ApplicationSaveableState associatedSaveableState;



	private TypeOfWordForRepeating(ApplicationSaveableState
			associatedSaveableState) {

		this.associatedSaveableState = associatedSaveableState;
	}

	public ApplicationSaveableState getAssociatedSaveableState() {
		return associatedSaveableState;
	}

}

package com.kanji.constants.enums;

public enum TypeOfWordForRepeating {
	KANJIS(ApplicationSaveableState.REPEATING_KANJI,
			ApplicationSaveableState.REVIEWING_PROBLEMATIC_KANJIS), JAPANESE_WORDS(
			ApplicationSaveableState.REPEATING_JAPANESE_WORDS,
			ApplicationSaveableState.REVIEWING_PROBLEMATIC_JAPANESE_WORDS);

	private ApplicationSaveableState associatedRepeatingWordsState;
	private ApplicationSaveableState associatedReviewingWordsState;

	TypeOfWordForRepeating(
			ApplicationSaveableState associatedRepeatingWordsState,
			ApplicationSaveableState associatedReviewingWordsState) {
		this.associatedRepeatingWordsState = associatedRepeatingWordsState;
		this.associatedReviewingWordsState = associatedReviewingWordsState;
	}

	public ApplicationSaveableState getAssociatedRepeatingWordsState() {
		return associatedRepeatingWordsState;
	}

	public ApplicationSaveableState getAssociatedReviewingWordsState() {
		return associatedReviewingWordsState;
	}
}

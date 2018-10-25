package com.kanji.constants.enums;

import java.util.Arrays;

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

	public static TypeOfWordForRepeating withMeaningfulName(
			String meaningfulName) {
		return Arrays.stream(values())
				.filter(type -> type.getAssociatedRepeatingWordsState()
						.getMeaningfulName().equals(meaningfulName)).findFirst()
				.orElse(null);
	}

}

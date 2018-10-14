package com.kanji.constants.enums;

import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.Kanji;

public enum ApplicationSaveableState {
	//TODO make it a class with 2 fields
	REPEATING_KANJI(Kanji.MEANINGFUL_NAME,
			LearningState.REPEATING),
	REPEATING_JAPANESE_WORDS(
			JapaneseWord.MEANINGFUL_NAME, LearningState.REPEATING),

	REVIEWING_PROBLEMATIC_KANJIS(Kanji.MEANINGFUL_NAME,
			LearningState.REVIEWING),

	REVIEWING_PROBLEMATIC_JAPANESE_WORDS(JapaneseWord.MEANINGFUL_NAME,
			LearningState.REVIEWING);

	private String meaningfulName;
	private LearningState learningState;

	private ApplicationSaveableState(String meaningfulName,
			LearningState learningState) {
		this.meaningfulName = meaningfulName;
		this.learningState = learningState;
	}

	public String getMeaningfulName() {
		return meaningfulName;
	}

	public LearningState getLearningState() {
		return learningState;
	}
}


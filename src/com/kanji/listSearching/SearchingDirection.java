package com.kanji.listSearching;

public enum SearchingDirection {

	FORWARD(1), BACKWARD(-1);

	private int incrementationValue;

	SearchingDirection(int incrementValue) {
		incrementationValue = incrementValue;
	}

	public int getIncrementationValue() {
		return incrementationValue;
	}

}

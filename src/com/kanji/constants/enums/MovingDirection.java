package com.kanji.constants.enums;

public enum MovingDirection {

	FORWARD(1), BACKWARD(-1);

	private int incrementationValue;

	MovingDirection(int incrementValue) {
		incrementationValue = incrementValue;
	}

	public int getIncrementationValue() {
		return incrementationValue;
	}

}

package com.kanji.constants.enums;

public enum IncrementSign {
	MINUS(-1), PLUS(1);

	private int signValue;

	IncrementSign(int signValue) {
		this.signValue = signValue;
	}

	public int getSignValue() {
		return signValue;
	}
}

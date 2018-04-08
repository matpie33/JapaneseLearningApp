package com.kanji.constants.enums;

public enum JapanesePanelDisplayMode {
	EDIT(true), VIEW(true);

	private boolean kanaTextFieldRequired;

	private JapanesePanelDisplayMode(boolean kanaTextFieldRequired) {
		this.kanaTextFieldRequired = kanaTextFieldRequired;
	}

	public boolean isKanaTextFieldRequired() {
		return kanaTextFieldRequired;
	}
}

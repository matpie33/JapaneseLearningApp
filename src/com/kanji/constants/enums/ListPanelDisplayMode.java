package com.kanji.constants.enums;

public enum ListPanelDisplayMode {
	VIEW_AND_EDIT(true), ADD(true);

	private boolean kanaTextFieldRequired;

	private ListPanelDisplayMode(boolean kanaTextFieldRequired) {
		this.kanaTextFieldRequired = kanaTextFieldRequired;
	}

	public boolean isKanaTextFieldRequired() {
		return kanaTextFieldRequired;
	}
}

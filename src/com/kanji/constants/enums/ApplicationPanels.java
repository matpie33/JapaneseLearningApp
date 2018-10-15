package com.kanji.constants.enums;

public enum ApplicationPanels {

	STARTING_PANEL("Starting panel"), REPEATING_KANJI_PANEL(
			"Repeating kanji panel"), REPEATING_JAPANESE_WORDS_PANEL(
			"Repeating japanese words panel"), PROBLEMATIC_KANJI_PANEL(
			"Problematic kanji panel"), PROBLEMATIC_JAPANESE_WORDS_PANEL(
			"Problematic japanese words panel");
	private String panelName;

	ApplicationPanels(String panelName) {
		this.panelName = panelName;
	}

	public String getPanelName() {
		return panelName;
	}

}

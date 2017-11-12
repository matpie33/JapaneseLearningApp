package com.kanji.enums;

public enum ApplicationPanels {

	STARTING_PANEL("Starting panel"), REPEATING_PANEL("Repeating panel");
	private String panelName;

	ApplicationPanels(String panelName) {
		this.panelName = panelName;
	}

	public String getPanelName() {
		return panelName;
	}

}

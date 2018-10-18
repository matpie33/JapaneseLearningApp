package com.kanji.application;

import com.kanji.customPositioning.CustomPositioner;

public class ApplicationConfiguration {

	private CustomPositioner insertWordPanelPositioner;

	public CustomPositioner getInsertWordPanelPositioner() {
		return insertWordPanelPositioner;
	}

	public ApplicationConfiguration setInsertWordPanelPositioner(
			CustomPositioner insertWordPanelPositioner) {
		this.insertWordPanelPositioner =
				insertWordPanelPositioner;
		return this;
	}
}

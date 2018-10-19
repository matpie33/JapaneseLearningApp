package com.kanji.application;

import com.kanji.customPositioning.CustomPositioner;

import java.awt.*;

public class ApplicationConfiguration {

	private CustomPositioner insertWordPanelPositioner;
	private Color listRowHighlightColor;
	private Color listRowEditTemporarilyColor;

	public CustomPositioner getInsertWordPanelPositioner() {
		return insertWordPanelPositioner;
	}

	public ApplicationConfiguration setInsertWordPanelPositioner(
			CustomPositioner insertWordPanelPositioner) {
		this.insertWordPanelPositioner =
				insertWordPanelPositioner;
		return this;
	}

	public Color getListRowHighlightColor() {
		return listRowHighlightColor;
	}

	public ApplicationConfiguration setListRowHighlightColor(Color listRowHighlightColor) {
		this.listRowHighlightColor = listRowHighlightColor;
		return this;
	}

	public Color getListRowEditTemporarilyColor() {
		return listRowEditTemporarilyColor;
	}

	public ApplicationConfiguration setListRowEditTemporarilyColor(
			Color listRowEditTemporarilyColor) {
		this.listRowEditTemporarilyColor = listRowEditTemporarilyColor;
		return this;
	}
}

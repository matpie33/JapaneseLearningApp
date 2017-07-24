package com.kanji.actions;

import javax.swing.text.StyleConstants;

public enum TextAlignment {
	JUSTIFIED(StyleConstants.ALIGN_JUSTIFIED), CENTERED(StyleConstants.ALIGN_CENTER);
	private int styleConstant;

	private TextAlignment(int styleConstant) {
		this.styleConstant = styleConstant;
	}

	public int getStyleConstant() {
		return styleConstant;
	}
}

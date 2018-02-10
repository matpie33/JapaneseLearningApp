package com.kanji.constants.enums;

import javax.swing.*;

public enum SplitPaneOrientation {
	VERTICAL(JSplitPane.VERTICAL_SPLIT), HORIZONTAL(
			JSplitPane.HORIZONTAL_SPLIT);

	private int value;

	private SplitPaneOrientation(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}

package com.kanji.customPositioning;

import javax.swing.*;
import java.awt.*;

public class PositionerOnRightPartOfSplitPane implements CustomPositioner {

	private JSplitPane splitPane;

	public PositionerOnRightPartOfSplitPane(JSplitPane splitPane) {
		this.splitPane = splitPane;
	}

	@Override
	public Point getPosition() {
		return splitPane.getRightComponent().getLocationOnScreen();
	}
}

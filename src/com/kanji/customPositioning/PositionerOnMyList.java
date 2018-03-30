package com.kanji.customPositioning;

import javax.swing.*;
import java.awt.*;

public class PositionerOnMyList implements CustomPositioner {

	private JSplitPane splitPane;

	public PositionerOnMyList(JSplitPane splitPane) {
		this.splitPane = splitPane;
	}

	@Override
	public Point getPosition() {
		return splitPane.getRightComponent().getLocationOnScreen();
	}
}

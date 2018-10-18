package com.kanji.customPositioning;

import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.panelsAndControllers.panels.StartingPanel;

import javax.swing.*;
import java.awt.*;

public class PositionerOnRightPartOfSplitPane implements CustomPositioner {

	private StartingPanel startingPanel;
	private ApplicationController applicationController;

	public PositionerOnRightPartOfSplitPane(StartingPanel startingPanel,
			ApplicationController applicationController) {
		this.applicationController = applicationController;
		this.startingPanel = startingPanel;
	}

	@Override
	public Point getPosition() {
		JSplitPane splitPane = startingPanel.getSplitPaneFor(
				applicationController.getActiveWordsList()
						.getListElementClass());
		return splitPane.getRightComponent().getLocationOnScreen();
	}
}

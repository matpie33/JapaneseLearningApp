package com.kanji.customPositioning;

import com.guimaker.customPositioning.CustomPositioner;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.panelsAndControllers.controllers.StartingController;
import com.kanji.panelsAndControllers.panels.StartingPanel;

import javax.swing.*;
import java.awt.*;

public class PositionerOnRightPartOfSplitPane implements CustomPositioner {

	private StartingController startingController;
	private ApplicationController applicationController;

	public PositionerOnRightPartOfSplitPane(StartingController startingController,
			ApplicationController applicationController) {
		this.applicationController = applicationController;
		this.startingController = startingController;
	}

	@Override
	public Point getPosition() {
		JSplitPane splitPane = startingController.getSplitPaneFor(
				applicationController.getActiveWordsList()
						.getListElementClass());
		return splitPane.getRightComponent().getLocationOnScreen();
	}
}

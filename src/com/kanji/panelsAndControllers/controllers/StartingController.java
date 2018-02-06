package com.kanji.panelsAndControllers.controllers;

import com.kanji.panelsAndControllers.panels.StartingPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class StartingController {

	private StartingPanel startingPanel;

	public StartingController(StartingPanel panel) {
		startingPanel = panel;
	}

	public ChangeListener createTabChangeListener() {
		return new ChangeListener() {
			@Override public void stateChanged(ChangeEvent e) {
				JTabbedPane source = (JTabbedPane) e.getSource();
				startingPanel.updateWordTypeContext(source.getTitleAt(source.getSelectedIndex()));
			}
		};
	}
}

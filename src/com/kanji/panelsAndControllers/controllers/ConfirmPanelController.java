package com.kanji.panelsAndControllers.controllers;

import com.kanji.panelsAndControllers.panels.ConfirmPanel;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ConfirmPanelController {

	private ConfirmPanel confirmPanel;

	public ConfirmPanelController(ConfirmPanel confirmPanel) {
		this.confirmPanel = confirmPanel;
	}

	public AbstractAction createActionCloseDialog() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				confirmPanel.getDialog().setAccepted(false);
			}
		};
	}

	public AbstractAction createActionConfirm (){
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				confirmPanel.getDialog().getContainer().dispose();
				confirmPanel.getDialog().setAccepted(true);
			}
		};
	}

}

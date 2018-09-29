package com.kanji.panelsAndControllers.panels;

import com.guimaker.enums.Anchor;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.panelsAndControllers.controllers.ConfirmPanelController;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class ConfirmPanel extends MessagePanel {

	private ConfirmPanelController controller;

	public ConfirmPanel(String message) {
		super(message);
		controller = new ConfirmPanelController(this);
	}

	@Override
	public void createElements() {
		//TODO there's not really anything to extend in message panel, and there's issue
		// with button close - we cannot override the hotkey information
		super.createElements();
		AbstractButton yesButton = createButtonWithHotkey(KeyEvent.VK_ENTER,
				controller.createActionConfirm(), ButtonsNames.CONFIRM,
				HotkeysDescriptions.CONFIRM_ACTION);
		buttonClose.addActionListener(controller.createActionCloseDialog());
		buttonClose.setText(ButtonsNames.REJECT);
		setNavigationButtons(Anchor.CENTER, buttonClose, yesButton);
	}


}

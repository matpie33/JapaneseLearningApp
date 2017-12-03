package com.kanji.panels;

import com.guimaker.enums.Anchor;
import com.kanji.strings.ButtonsNames;
import com.kanji.strings.HotkeysDescriptions;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ConfirmPanel extends MessagePanel {

	public ConfirmPanel(String message) {
		super(message);
	}

	@Override
	public void createElements() {
		//TODO there's not really anything to extend in message panel, and there's issue
		// with button close - we cannot override the hotkey information
		super.createElements();
		AbstractButton yesButton = createButtonConfirm();
		buttonClose.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentDialog.setAccepted(false);
			}
		});
		buttonClose.setText(ButtonsNames.REJECT);
		setNavigationButtons(Anchor.CENTER, buttonClose, yesButton);
	}

	private AbstractButton createButtonConfirm() {
		AbstractAction action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentDialog.getContainer().dispose();
				parentDialog.setAccepted(true);
			}
		};
		return createButtonWithHotkey(KeyEvent.VK_ENTER, action, ButtonsNames.CONFIRM,
				HotkeysDescriptions.CONFIRM_ACTION);
	}


}

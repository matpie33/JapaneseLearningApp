package com.kanji.panels;

import com.guimaker.enums.Anchor;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.HotkeysDescriptions;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ConfirmPanel extends MessagePanel {

	public ConfirmPanel(String message) {
		super(message);
	}

	@Override
	void createElements() {
		super.createElements();
		AbstractButton yesButton = createButtonConfirm();
		AbstractButton noButton = createButtonReject();
		setNavigationButtons(Anchor.CENTER, noButton, yesButton);
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

	private AbstractButton createButtonReject() {
		AbstractAction action = new AbstractAction() {
			private static final long serialVersionUID = 5504620933205592893L;

			@Override
			public void actionPerformed(ActionEvent e) {
				parentDialog.getContainer().dispose();
				parentDialog.setAccepted(false);
			}
		};
		return createButtonWithHotkey(KeyEvent.VK_ESCAPE, action, ButtonsNames.REJECT,
				HotkeysDescriptions.REJECT_ACTION);
	}

}

package com.kanji.panels;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JScrollPane;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.panels.GuiMaker;
import com.guimaker.row.SimpleRow;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.HotkeysDescriptions;

public class ConfirmPanel extends AbstractPanelWithHotkeysInfo {

	private String message;

	public ConfirmPanel(String message) {
		this.message = message;
	}

	@Override
	void createElements() {
		JScrollPane scrollPane = GuiMaker.createTextPaneWrappedInScrollPane(message,
				TextAlignment.CENTERED);
		AbstractButton yesButton = createButtonConfirm();
		AbstractButton noButton = createButtonReject();

		mainPanel.addRow(new SimpleRow(FillType.BOTH, scrollPane));
		addHotkeysPanelHere();
		mainPanel.addRow(new SimpleRow(FillType.NONE, Anchor.CENTER, noButton, yesButton));
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

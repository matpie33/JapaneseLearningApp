package com.kanji.panels;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JScrollPane;

import com.guimaker.row.Anchor;
import com.guimaker.row.RowMaker;
import com.kanji.actions.GuiElementsMaker;
import com.kanji.actions.TextAlignment;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.HotkeysDescriptions;

public class ConfirmPanel extends AbstractPanelWithHotkeysInfo {

	private String message;

	public ConfirmPanel(String message) {
		this.message = message;
	}

	@Override
	void createElements() {
		JScrollPane scrollPane = GuiElementsMaker.createTextPaneWrappedInScrollPane(message,
				TextAlignment.CENTERED);
		JButton yesButton = createButtonConfirm();
		JButton noButton = createButtonReject();

		mainPanel.addRow(RowMaker.createBothSidesFilledRow(scrollPane));
		addHotkeysPanelHere();
		mainPanel.addRow(RowMaker.createUnfilledRow(Anchor.CENTER, noButton, yesButton));
	}

	private JButton createButtonConfirm() {
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

	private JButton createButtonReject() {
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

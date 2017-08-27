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

public class MessagePanel extends AbstractPanelWithHotkeysInfo {

	private String message;

	public MessagePanel(String message) {
		// super(true); //TODO this is misleading - we add escape for closing,
		// but we don't call super with true as parameter
		this.message = message;
	}

	@Override
	void createElements() {
		JButton button = createButtonClose();
		JScrollPane scrollPane = GuiElementsMaker.createTextPaneWrappedInScrollPane(message,
				TextAlignment.CENTERED);

		mainPanel.addRow(RowMaker.createBothSidesFilledRow(scrollPane));
		addHotkeysPanelHere();
		mainPanel.addRow(RowMaker.createUnfilledRow(Anchor.CENTER, button));
	}

	private JButton createButtonClose() {
		AbstractAction dispose = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentDialog.getContainer().dispose();
			}
		};
		return createButtonWithHotkey(KeyEvent.VK_ESCAPE, dispose, ButtonsNames.APPROVE,
				HotkeysDescriptions.CLOSE_WINDOW);
	}

}

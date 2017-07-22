package com.kanji.panels;

import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JTextArea;

import com.guimaker.row.RowMaker;
import com.kanji.actions.CommonActionsMaker;
import com.kanji.constants.ButtonsNames;

public class MessagePanel extends AbstractPanelWithHotkeysInfo {

	private String message;

	public MessagePanel(String message) {
		super(true);
		this.message = message;
	}

	@Override
	void createElements() {
		int level = 0;
		JTextArea prompt = addPromptAtLevel(level, message);
		JButton button = CommonActionsMaker.createButtonDispose(ButtonsNames.buttonApproveText,
				java.awt.event.KeyEvent.VK_ESCAPE, parentDialog);

		mainPanel.addRow(RowMaker.createBothSidesFilledRow(prompt));
		addHotkeysPanelHere();
		mainPanel.addRow(RowMaker.createUnfilledRow(GridBagConstraints.CENTER, button));
	}

	private JTextArea addPromptAtLevel(int level, String message) {
		JTextArea elem = new JTextArea(4, 30);

		elem.setText(message);
		elem.setLineWrap(true);
		elem.setWrapStyleWord(true);
		elem.setOpaque(false);
		elem.setEditable(false);

		return elem;
	}

}

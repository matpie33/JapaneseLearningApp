package com.kanji.panels;

import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import com.guimaker.row.RowMaker;
import com.kanji.actions.CommonActionsMaker;
import com.kanji.actions.GuiElementsMaker;
import com.kanji.constants.ButtonsNames;

public class LoadingPanel extends AbstractPanelWithHotkeysInfo {

	private JButton okButton;
	private String message;

	// TODO hotkeys descriptions are not on the bottom in this panel
	public LoadingPanel(String message) {
		super(true);
		this.message = message;
	}

	@Override
	void createElements() {

		int level = 0;
		JTextArea prompt = addPromptAtLevel(level, message);

		okButton = GuiElementsMaker.createButton(ButtonsNames.buttonApproveText,
				CommonActionsMaker.createDisposeAction(parentDialog));

		mainPanel.addRow(RowMaker.createBothSidesFilledRow(prompt));
		addHotkeysPanelHere();
		mainPanel.addRow(RowMaker.createUnfilledRow(GridBagConstraints.CENTER, okButton));

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

	public void setProgressBar(JProgressBar bar) {
		mainPanel.removeRow(1);
		mainPanel.addRow(RowMaker.createHorizontallyFilledRow(bar));
		mainPanel.addRow(RowMaker.createUnfilledRow(GridBagConstraints.CENTER, okButton));
	}

}

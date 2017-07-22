package com.kanji.panels;

import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import com.guimaker.row.RowMaker;
import com.kanji.actions.CommonActionsMaker;
import com.kanji.actions.GuiElementsMaker;
import com.kanji.constants.ButtonsNames;

public class LoadingPanel extends AbstractPanelWithHotkeysInfo {

	private JButton okButton;
	private String message;

	public LoadingPanel(String message) {
		super(true);
		this.message = message;
	}

	@Override
	void createElements() {

		JScrollPane scrollPane = GuiElementsMaker
				.createCenteredTextPaneWrappedInScrollPane(message);

		okButton = GuiElementsMaker.createButton(ButtonsNames.buttonApproveText,
				CommonActionsMaker.createDisposeAction(parentDialog));

		mainPanel.addRow(RowMaker.createBothSidesFilledRow(scrollPane));
		addHotkeysPanelHere();
		mainPanel.addRow(RowMaker.createUnfilledRow(GridBagConstraints.CENTER, okButton));

	}

	public void setProgressBar(JProgressBar bar) {
		mainPanel.removeRow(2);// TODO this is bad
		mainPanel.addRow(RowMaker.createHorizontallyFilledRow(bar));
		mainPanel.addRow(RowMaker.createUnfilledRow(GridBagConstraints.CENTER, okButton));
	}

}

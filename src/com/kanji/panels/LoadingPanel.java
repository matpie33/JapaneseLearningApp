package com.kanji.panels;

import javax.swing.AbstractButton;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.ComponentType;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.row.SimpleRow;
import com.guimaker.utilities.CommonActionsMaker;
import com.kanji.constants.ButtonsNames;

public class LoadingPanel extends AbstractPanelWithHotkeysInfo {

	private AbstractButton okButton;
	private String message;

	public LoadingPanel(String message) {
		this.message = message;
	}

	@Override
	void createElements() {

		JScrollPane scrollPane = GuiMaker.createTextPaneWrappedInScrollPane(
				new TextPaneOptions().textAlignment(TextAlignment.CENTERED).text(message));

		okButton = createButtonClose();

		mainPanel.addRow(new SimpleRow(FillType.BOTH, scrollPane));
		setNavigationButtons(Anchor.CENTER, okButton);

	}

	public void setProgressBar(JProgressBar bar) {
		mainPanel.removeRow(2);// TODO this is bad
		mainPanel.addRow(new SimpleRow(FillType.HORIZONTAL, bar).nextRow(FillType.NONE,
				Anchor.CENTER, okButton));
	}

}

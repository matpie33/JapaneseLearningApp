package com.kanji.panels;

import javax.swing.*;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.strings.Prompts;

public class LoadingPanel extends AbstractPanelWithHotkeysInfo {

	private AbstractButton buttonClose;
	private String message;
	private JProgressBar progressBar;

	public LoadingPanel(String message) {
		this.message = message;
		progressBar = new JProgressBar();
	}

	@Override
	void createElements() {

		//TODO add method in gui maker to enable connecting one row with another or create a separate row
		JLabel loading = GuiMaker.createLabel(new ComponentOptions().text(Prompts.LOADING_PROMPT));

		buttonClose = createButtonClose();
		MainPanel loadingPanel = new MainPanel(null);
		loadingPanel.addRows(SimpleRowBuilder.createRow(FillType.NONE, Anchor.CENTER, loading).nextRow(FillType.HORIZONTAL, progressBar));
		mainPanel.addRows(SimpleRowBuilder.createRow(FillType.BOTH, loadingPanel.getPanel()));
		setNavigationButtons(Anchor.CENTER, buttonClose);

	}

	public JProgressBar getProgressBar (){
		return progressBar;
	}

}

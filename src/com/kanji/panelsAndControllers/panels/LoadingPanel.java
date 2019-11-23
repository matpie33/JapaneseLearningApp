package com.kanji.panelsAndControllers.panels;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.model.PanelConfiguration;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.AbstractPanelWithHotkeysInfo;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.mainPanel.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.strings.Prompts;

import javax.swing.*;

public class LoadingPanel extends AbstractPanelWithHotkeysInfo {

	private static final String UNIQUE_NAME = "Loading panel";
	private AbstractButton buttonClose;
	private MainPanel progressBarsPanel;

	@Override
	public String getUniqueName() {
		return UNIQUE_NAME;
	}

	@Override
	public void createElements() {

		//TODO add method in gui maker to enable connecting one row with
		// another or create a separate row
		progressBarsPanel = new MainPanel(
				new PanelConfiguration().setColorToUse(BasicColors.BLUE_DARK_3)
										.putRowsAsHighestAsPossible());
		progressBarsPanel.addRow(
				SimpleRowBuilder.createRow(FillType.NONE, Anchor.CENTER,
						GuiElementsCreator.createLabel(
								new ComponentOptions().text(
										Prompts.PROJECT_LOADING))));
		mainPanel.addRow(SimpleRowBuilder.createRow(FillType.BOTH,
				progressBarsPanel.getPanel()));
		buttonClose = createButtonClose();
		setNavigationButtons(Anchor.CENTER, buttonClose);

	}

	public JProgressBar addProgressBar(String textLabel) {
		JLabel label = GuiElementsCreator.createLabel(
				new ComponentOptions().text(textLabel));
		JProgressBar progressBar = new JProgressBar();
		progressBarsPanel.addElementsInColumn(
				SimpleRowBuilder.createRowStartingFromColumn(0, FillType.NONE,
						label, progressBar));
		return progressBar;
	}

}

package com.kanji.panelsAndControllers.panels;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.options.ComponentOptions;
import com.guimaker.options.ScrollPaneOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LoadingPanel extends AbstractPanelWithHotkeysInfo {

	private AbstractButton buttonClose;
	private String message;
	private List<JProgressBar> progressBars;
	private MainPanel progressBarsPanel;

	public LoadingPanel(String message) {
		this.message = message;
		progressBars = new ArrayList<>();
	}

	@Override
	public void createElements() {

		//TODO add method in gui maker to enable connecting one row with another or create a separate row
		progressBarsPanel = new MainPanel(BasicColors.MEDIUM_BLUE, true);
		progressBarsPanel.addRow(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.CENTER, GuiElementsCreator
						.createLabel(new ComponentOptions().text(message))));
		mainPanel.addRow(SimpleRowBuilder.createRow(FillType.BOTH, progressBarsPanel.getPanel()));
		buttonClose = createButtonClose();
		setNavigationButtons(Anchor.CENTER, buttonClose);

	}

	public JProgressBar addProgressBar(String textLabel) {
		JLabel label = GuiElementsCreator
				.createLabel(new ComponentOptions().text(textLabel));
		JProgressBar progressBar = new JProgressBar();
		progressBars.add(progressBar);
		progressBarsPanel.addElementsInColumn(SimpleRowBuilder
				.createRowStartingFromColumn(0, FillType.NONE, label,
						progressBar));
		return progressBar;
	}

}

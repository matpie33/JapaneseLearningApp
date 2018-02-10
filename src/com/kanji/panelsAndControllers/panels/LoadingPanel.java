package com.kanji.panelsAndControllers.panels;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.options.ComponentOptions;
import com.guimaker.options.ScrollPaneOptions;
import com.guimaker.panels.GuiMaker;
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

	@Override public void createElements() {

		//TODO add method in gui maker to enable connecting one row with another or create a separate row
		progressBarsPanel = new MainPanel(BasicColors.OCEAN_BLUE, false);
		JScrollPane scrollPane = GuiMaker.createScrollPane(
				new ScrollPaneOptions()
						.componentToWrap(progressBarsPanel.getPanel())
						.preferredSize(new Dimension(350, 200)).opaque(false));
		progressBarsPanel.addRow(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.CENTER, GuiMaker.createLabel(
						new ComponentOptions().text(message))));
		mainPanel.addRow(SimpleRowBuilder.createRow(FillType.BOTH, scrollPane));
		buttonClose = createButtonClose();
		setNavigationButtons(Anchor.CENTER, buttonClose);

	}

	public JProgressBar addProgressBar(String textLabel) {
		JLabel label = GuiMaker
				.createLabel(new ComponentOptions().text(textLabel));
		JProgressBar progressBar = new JProgressBar();
		progressBars.add(progressBar);
		progressBarsPanel
				.addElementsInColumnStartingFromColumn(0, label, progressBar);
		return progressBar;
	}

}

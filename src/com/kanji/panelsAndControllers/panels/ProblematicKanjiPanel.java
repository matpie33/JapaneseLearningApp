package com.kanji.panelsAndControllers.panels;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.enums.TextAlignment;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.panels.AbstractPanelWithHotkeysInfo;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.webPanel.ContextOwner;
import com.guimaker.webPanel.WebPagePanel;
import com.kanji.constants.enums.SplitPaneOrientation;
import com.kanji.constants.strings.Prompts;
import com.kanji.constants.strings.Titles;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.listRows.RowInKanjiInformations;
import com.guimaker.list.myList.ListConfiguration;
import com.guimaker.list.myList.MyList;
import com.kanji.panelSwitching.FocusableComponentsManager;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.utilities.CommonGuiElementsCreator;
import com.kanji.webPanel.ConnectionFailKanjiOfflinePage;
import com.guimaker.application.ApplicationWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class ProblematicKanjiPanel extends AbstractPanelWithHotkeysInfo {

	private static final String UNIQUE_NAME = "Problematic kanji panel";
	private WebPagePanel englishPolishDictionaryWebPanel;
	private WebPagePanel kanjiKoohiWebPanel;
	private MyList<Kanji> wordsToReviewList;
	private FocusableComponentsManager focusableComponentsManager;
	private ProblematicWordsController<Kanji> controller;

	public ProblematicKanjiPanel(ApplicationController applicationController,
			ProblematicWordsController<Kanji> controller,
			ContextOwner kanjiContextOwner) {

		focusableComponentsManager = new FocusableComponentsManager(getPanel());
		this.controller = controller;
		englishPolishDictionaryWebPanel = new WebPagePanel(kanjiContextOwner,
				null);
		kanjiKoohiWebPanel = new WebPagePanel(kanjiContextOwner,
				new ConnectionFailKanjiOfflinePage(
						ApplicationWindow.getKanjiFont()));

		RowInKanjiInformations rowInKanjiInformations = new RowInKanjiInformations(
				applicationController, PanelDisplayMode.VIEW);
		rowInKanjiInformations.setProblematicWordsController(controller);
		wordsToReviewList = new MyList<>(parentDialog, applicationController,
				rowInKanjiInformations, Titles.PROBLEMATIC_KANJIS,
				new ListConfiguration(Prompts.KANJI)
						.showButtonsLoadNextPreviousWords(false),
				Kanji.getInitializer());

	}

	public WebPagePanel getEnglishPolishDictionaryWebPanel() {
		return englishPolishDictionaryWebPanel;
	}

	public WebPagePanel getKanjiKoohiWebPanel() {
		return kanjiKoohiWebPanel;
	}

	@Override
	public void createElements() {

		MainPanel kanjiOfflineDisplayingPanel = new MainPanel(
				BasicColors.BLUE_DARK_1);
		Font messageFont = new JLabel().getFont().deriveFont(15f);

		JTextComponent kanjiOfflineTextPane = GuiElementsCreator.createTextPane(
				new TextPaneOptions().border(null).editable(false)
						.textAlignment(TextAlignment.CENTERED).text("")
						.border(getDefaultBorder()));
		kanjiOfflineTextPane.setText(Prompts.NO_KANJI_TO_DISPLAY);
		kanjiOfflineTextPane.setFont(messageFont);

		kanjiOfflineDisplayingPanel.addRow(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.CENTER, kanjiOfflineTextPane));

		focusableComponentsManager.makeFocusable(wordsToReviewList.getPanel(),
				englishPolishDictionaryWebPanel.getWebPanel(),
				kanjiKoohiWebPanel.getWebPanel());

		JSplitPane wordsAndDictionaryPane = CommonGuiElementsCreator
				.createSplitPane(SplitPaneOrientation.VERTICAL,
						englishPolishDictionaryWebPanel.getSwitchingPanel(),
						wordsToReviewList.getPanel(), 0.7);

		JSplitPane splitPane = CommonGuiElementsCreator
				.createSplitPane(SplitPaneOrientation.HORIZONTAL,
						wordsAndDictionaryPane,
						kanjiKoohiWebPanel.getSwitchingPanel(), 0.2);

		mainPanel.addRow(SimpleRowBuilder.createRow(FillType.BOTH, splitPane));

		new ProblematicWordsPanelCommonPart(this, controller)
				.addCommonPartToPanel();

	}

	@Override
	public String getUniqueName() {
		return UNIQUE_NAME;
	}

	public FocusableComponentsManager getFocusableComponentsManager() {
		return focusableComponentsManager;
	}

	public MyList<Kanji> getWordsToReviewList() {
		return wordsToReviewList;
	}

}

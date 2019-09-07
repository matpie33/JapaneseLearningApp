package com.kanji.panelsAndControllers.panels;

import com.guimaker.application.ApplicationWindow;
import com.guimaker.enums.*;
import com.guimaker.list.myList.ListConfiguration;
import com.guimaker.list.myList.MyList;
import com.guimaker.model.HotkeyWrapper;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.panelSwitching.FocusableComponentsManager;
import com.guimaker.panels.AbstractPanelWithHotkeysInfo;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.webPanel.ContextOwner;
import com.guimaker.webPanel.WebPagePanel;
import com.kanji.constants.strings.Prompts;
import com.kanji.constants.strings.Titles;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.listRows.RowInKanjiInformations;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.webPageEnhancer.WebPageActions;
import com.kanji.webPanel.ConnectionFailKanjiOfflinePage;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyEvent;

public class ProblematicKanjiPanel extends AbstractPanelWithHotkeysInfo {

	private static final String UNIQUE_NAME = "Problematic kanji panel";
	private WebPagePanel englishPolishDictionaryWebPanel;
	private WebPagePanel kanjiKoohiWebPanel;
	private MyList<Kanji> wordsToReviewList;
	private FocusableComponentsManager focusableComponentsManager;
	private ProblematicWordsController<Kanji> controller;
	private WebPageActions webPageActions;

	public ProblematicKanjiPanel(ApplicationController applicationController,
			ProblematicWordsController<Kanji> controller,
			ContextOwner kanjiContextOwner) {

		focusableComponentsManager = new FocusableComponentsManager(getPanel());
		this.controller = controller;
		englishPolishDictionaryWebPanel = new WebPagePanel(this,
				kanjiContextOwner, null,
				applicationController.getApplicationWindow());
		createKanjiKoohiPanel(applicationController, kanjiContextOwner);

		RowInKanjiInformations rowInKanjiInformations = new RowInKanjiInformations(
				applicationController, PanelDisplayMode.VIEW);
		rowInKanjiInformations.setProblematicWordsController(controller);
		wordsToReviewList = new MyList<>(
				new ListConfiguration<>(Prompts.KANJI, rowInKanjiInformations,
						Kanji.getInitializer(), Titles.PROBLEMATIC_KANJIS,
						applicationController.getApplicationWindow(),
						applicationController).showButtonsLoadNextPreviousWords(
						false));
		addNavigableByKeyboardList(wordsToReviewList);

	}

	private void createKanjiKoohiPanel(
			ApplicationController applicationController,
			ContextOwner kanjiContextOwner) {
		kanjiKoohiWebPanel = new WebPagePanel(this, kanjiContextOwner,
				new ConnectionFailKanjiOfflinePage(
						ApplicationWindow.getKanjiFont()),
				applicationController.getApplicationWindow());
		webPageActions = new WebPageActions(kanjiKoohiWebPanel,
				applicationController);
		kanjiKoohiWebPanel.addHotkey(
				new HotkeyWrapper(KeyModifiers.ALT, KeyEvent.VK_Q),
				webPageActions.createActionFindKanjiPolishKeyword());
	}

	public WebPagePanel getEnglishPolishDictionaryWebPanel() {
		return englishPolishDictionaryWebPanel;
	}

	public WebPagePanel getKanjiKoohiWebPanel() {
		return kanjiKoohiWebPanel;
	}

	@Override
	public void createElements() {

		MainPanel kanjiOfflineDisplayingPanel = new MainPanel();
		Font messageFont = new JLabel().getFont()
									   .deriveFont(15f);

		JTextComponent kanjiOfflineTextPane = GuiElementsCreator.createTextPane(
				new TextPaneOptions().border(null)
									 .editable(false)
									 .textAlignment(TextAlignment.CENTERED)
									 .text("")
									 .border(getDefaultBorder()));
		kanjiOfflineTextPane.setText(Prompts.NO_KANJI_TO_DISPLAY);
		kanjiOfflineTextPane.setFont(messageFont);

		kanjiOfflineDisplayingPanel.addRow(
				SimpleRowBuilder.createRow(FillType.NONE, Anchor.CENTER,
						kanjiOfflineTextPane));

		focusableComponentsManager.makeFocusable(wordsToReviewList.getPanel(),
				englishPolishDictionaryWebPanel.getWebPanel(),
				kanjiKoohiWebPanel.getWebPanel());

		JSplitPane wordsAndDictionaryPane = GuiElementsCreator.createSplitPane(
				SplitPaneOrientation.VERTICAL,
				englishPolishDictionaryWebPanel.getSwitchingPanel(),
				wordsToReviewList.getPanel(), 0.7);

		JSplitPane splitPane = GuiElementsCreator.createSplitPane(
				SplitPaneOrientation.HORIZONTAL, wordsAndDictionaryPane,
				kanjiKoohiWebPanel.getSwitchingPanel(), 0.2);

		mainPanel.addRow(SimpleRowBuilder.createRow(FillType.BOTH, splitPane));

		new ProblematicWordsPanelCommonPart(this,
				controller).addCommonPartToPanel();

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

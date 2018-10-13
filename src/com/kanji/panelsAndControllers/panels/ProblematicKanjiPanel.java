package com.kanji.panelsAndControllers.panels;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.enums.TextAlignment;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.KeyModifiers;
import com.guimaker.webPanel.ContextOwner;
import com.guimaker.webPanel.WebPagePanel;
import com.kanji.constants.enums.SplitPaneOrientation;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.constants.strings.Prompts;
import com.kanji.constants.strings.Titles;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.listRows.RowInKanjiInformations;
import com.kanji.list.myList.ListConfiguration;
import com.kanji.list.myList.MyList;
import com.kanji.panelSwitching.FocusableComponentsManager;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.utilities.CommonGuiElementsCreator;
import com.kanji.webPanel.ConnectionFailKanjiOfflinePage;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyEvent;

public class ProblematicKanjiPanel extends AbstractPanelWithHotkeysInfo {

	private ProblematicWordsController<Kanji> controller;
	private WebPagePanel englishPolishDictionaryWebPanel;
	private WebPagePanel kanjiKoohiWebPanel;
	private MyList<Kanji> wordsToReviewList;
	private FocusableComponentsManager focusableComponentsManager;

	public ProblematicKanjiPanel(ApplicationWindow parentDialog,
			ProblematicWordsController<Kanji> controller,
			ContextOwner kanjiContextContextOwner) {
		this.parentDialog = parentDialog;
		this.controller = controller;

		englishPolishDictionaryWebPanel = new WebPagePanel(
				kanjiContextContextOwner, null);
		kanjiKoohiWebPanel = new WebPagePanel(kanjiContextContextOwner,
				new ConnectionFailKanjiOfflinePage(
						ApplicationWindow.getKanjiFont()));
		focusableComponentsManager = new FocusableComponentsManager(
				mainPanel.getPanel());
		RowInKanjiInformations rowInKanjiInformations = new RowInKanjiInformations(
				parentDialog, PanelDisplayMode.VIEW);
		rowInKanjiInformations.setProblematicWordsController(controller);
		wordsToReviewList = new MyList<>(parentDialog, null,
				rowInKanjiInformations, Titles.PROBLEMATIC_KANJIS,
				new ListConfiguration().showButtonsLoadNextPreviousWords(false),
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

		JSplitPane mainSplitPane = CommonGuiElementsCreator
				.createSplitPane(SplitPaneOrientation.HORIZONTAL,
						wordsAndDictionaryPane,
						kanjiKoohiWebPanel.getSwitchingPanel(), 0.2);

		mainPanel.addRow(SimpleRowBuilder
				.createRow(FillType.BOTH, mainSplitPane));

		AbstractButton buttonReturn = createButtonReturn();
		setNavigationButtons(Anchor.WEST, buttonReturn);
	}

	private AbstractButton createButtonReturn() {
		return createButtonWithHotkey(KeyModifiers.CONTROL, KeyEvent.VK_E,
				controller.goToStartingPanelAndManageState(),
				ButtonsNames.GO_BACK, HotkeysDescriptions.RETURN_FROM_LEARNING);

	}

	public FocusableComponentsManager getFocusableComponentsManager() {
		return focusableComponentsManager;
	}

	public MyList<Kanji> getWordsToReviewList() {
		return wordsToReviewList;
	}
}

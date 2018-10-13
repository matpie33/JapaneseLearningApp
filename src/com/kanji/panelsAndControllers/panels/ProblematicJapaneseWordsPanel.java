package com.kanji.panelsAndControllers.panels;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.model.WebContext;
import com.guimaker.options.ScrollPaneOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.KeyModifiers;
import com.guimaker.webPanel.ContextOwner;
import com.guimaker.webPanel.WebPagePanel;
import com.kanji.constants.enums.SplitPaneOrientation;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.myList.MyList;
import com.kanji.panelSwitching.FocusableComponentsManager;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.utilities.CommonGuiElementsCreator;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class ProblematicJapaneseWordsPanel extends AbstractPanelWithHotkeysInfo
		implements ContextOwner {

	private ProblematicWordsController<JapaneseWord> problematicWordsController;
	private MyList<JapaneseWord> problematicWordsList;
	private MainPanel kanjiInformationPanel;
	private WebPagePanel englishPolishDictionaryPanel;
	private WebPagePanel japaneseEnglishDictionaryPanel;
	private WebPagePanel kanjiKoohiWebPanel;

	private FocusableComponentsManager focusableComponentsManager;

	public ProblematicJapaneseWordsPanel(
			ProblematicWordsController<JapaneseWord> problematicWordsController,
			ApplicationWindow parent) {
		parentDialog = parent;
		this.problematicWordsController = problematicWordsController;
		kanjiInformationPanel = new MainPanel(null, true);
		englishPolishDictionaryPanel = new WebPagePanel(this, null);
		japaneseEnglishDictionaryPanel = new WebPagePanel(this, null);
		kanjiKoohiWebPanel = new WebPagePanel(this, null);
		focusableComponentsManager = new FocusableComponentsManager(
				mainPanel.getPanel());
	}

	public FocusableComponentsManager getFocusableComponentsManager() {
		return focusableComponentsManager;
	}

	public WebPagePanel getKanjiKoohiWebPanel() {
		return kanjiKoohiWebPanel;
	}

	public WebPagePanel getEnglishPolishDictionaryPanel() {
		return englishPolishDictionaryPanel;
	}

	public WebPagePanel getJapaneseEnglishDictionaryPanel() {
		return japaneseEnglishDictionaryPanel;
	}

	public MainPanel getKanjiInformationPanel() {
		return kanjiInformationPanel;
	}

	private void markPanelsAsFocusable(JComponent... panels) {
		for (JComponent panel : panels) {
			focusableComponentsManager.makeFocusable(panel);
		}
	}

	@Override
	public void createElements() {

		markPanelsAsFocusable(problematicWordsList.getPanel(),
				japaneseEnglishDictionaryPanel.getWebPanel(),
				englishPolishDictionaryPanel.getWebPanel(),
				kanjiInformationPanel.getPanel(),
				kanjiKoohiWebPanel.getWebPanel());

		JScrollPane scrollPaneForKanjiInformation = GuiElementsCreator
				.createScrollPane(new ScrollPaneOptions()
						.componentToWrap(kanjiInformationPanel.getPanel()));

		JSplitPane wordsListAndKanjiInformationSplitPane = CommonGuiElementsCreator
				.createSplitPane(SplitPaneOrientation.VERTICAL,
						scrollPaneForKanjiInformation,
						problematicWordsList.getPanel(), 0.2);
		JSplitPane dictionariesSplitPane = CommonGuiElementsCreator
				.createSplitPane(SplitPaneOrientation.VERTICAL,
						japaneseEnglishDictionaryPanel.getSwitchingPanel(),
						englishPolishDictionaryPanel.getSwitchingPanel(), 0.5);

		JSplitPane dictionariesWithProblematicWordsSplitPane = CommonGuiElementsCreator
				.createSplitPane(SplitPaneOrientation.HORIZONTAL,
						dictionariesSplitPane, wordsListAndKanjiInformationSplitPane,
						0.3);

		JSplitPane outerWrappingSplitPane = CommonGuiElementsCreator
				.createSplitPane(SplitPaneOrientation.HORIZONTAL,
						dictionariesWithProblematicWordsSplitPane,
						kanjiKoohiWebPanel.getSwitchingPanel(), 0.7);

		mainPanel.addRow(SimpleRowBuilder
				.createRow(FillType.BOTH, outerWrappingSplitPane));
		setNavigationButtons(Anchor.WEST, createButtonReturn());
	}

	private AbstractButton createButtonReturn() {
		return createButtonWithHotkey(KeyModifiers.CONTROL, KeyEvent.VK_E,
				problematicWordsController.closeDialogAndManageState(),
				ButtonsNames.GO_BACK, HotkeysDescriptions.RETURN_FROM_LEARNING);
	}

	@Override
	public WebContext getContext() {
		return null; //TODO implement it to return kanji tried to retrieve
	}

	public void setList(MyList<JapaneseWord> list) {
		this.problematicWordsList = list;
	}
}

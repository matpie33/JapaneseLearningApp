package com.kanji.panelsAndControllers.panels;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.SplitPaneOrientation;
import com.kanji.constants.strings.Urls;
import com.kanji.context.ContextOwner;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.utilities.CommonGuiElementsMaker;
import com.kanji.utilities.FocusableComponentMaker;
import com.kanji.webPanel.ConnectionFailMessagePage;
import com.kanji.webPanel.WebPagePanel;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;

import javax.swing.*;

public class ProblematicJapaneseWordsPanel extends AbstractPanelWithHotkeysInfo
		implements ContextOwner {

	private ProblematicWordsController problematicWordsController;
	private MyList<JapaneseWordInformation> problematicWords;
	private MainPanel kanjiInformationPanel;
	private WebPagePanel englishDictionaryPanel;
	private WebPagePanel japaneseEnglishDictionaryPanel;
	private static final String TANGORIN_URL = "http://tangorin.com/";

	public ProblematicJapaneseWordsPanel(ProblematicWordsController problematicWordsController,
			ApplicationWindow parent) {
		parentDialog = parent;
		this.problematicWordsController = problematicWordsController;
		kanjiInformationPanel = new MainPanel(null);
		englishDictionaryPanel = new WebPagePanel(this, new ConnectionFailMessagePage());

		japaneseEnglishDictionaryPanel = new WebPagePanel(this, new ConnectionFailMessagePage());
	}

	public void initialize() {
		problematicWords = problematicWordsController.getWordsToReviewList();
		japaneseEnglishDictionaryPanel.showPage(TANGORIN_URL);
		englishDictionaryPanel.showPage(Urls.DICTIONARY_PL_EN_MAIN_PAGE);
	}

	public void searchWord(String word) {
		japaneseEnglishDictionaryPanel.showPage(TANGORIN_URL + "/general/" + word);
	}

	@Override public void setParentDialog(DialogWindow parentDialog) {
		super.setParentDialog(parentDialog);
	}

	@Override public void createElements() {

		FocusableComponentMaker.makeFocusable(problematicWords.getPanel());
		FocusableComponentMaker.makeFocusable(japaneseEnglishDictionaryPanel.getWebPanel());
		FocusableComponentMaker.makeFocusable(englishDictionaryPanel.getWebPanel());
		FocusableComponentMaker.makeFocusable(kanjiInformationPanel.getPanel());

		JSplitPane wordAndKanjiInformationSplitPane = CommonGuiElementsMaker
				.createSplitPane(SplitPaneOrientation.VERTICAL, problematicWords.getPanel(),
						kanjiInformationPanel.getPanel(), 0.5);
		JSplitPane dictionariesSplitPane = CommonGuiElementsMaker
				.createSplitPane(SplitPaneOrientation.VERTICAL,
						japaneseEnglishDictionaryPanel.getSwitchingPanel(),
						englishDictionaryPanel.getSwitchingPanel(), 0.5);

		JSplitPane wordAndDictionariesSplitPane = CommonGuiElementsMaker
				.createSplitPane(SplitPaneOrientation.HORIZONTAL, wordAndKanjiInformationSplitPane,
						dictionariesSplitPane, 0.1);

		mainPanel.addRows(SimpleRowBuilder.createRow(FillType.BOTH, wordAndDictionariesSplitPane));
		setNavigationButtons(Anchor.WEST, createButtonClose());
	}

	@Override public Object getContext() {
		return null; //TODO
	}

}

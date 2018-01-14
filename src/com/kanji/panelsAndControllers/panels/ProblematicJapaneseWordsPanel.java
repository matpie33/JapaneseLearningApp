package com.kanji.panelsAndControllers.panels;

import com.guimaker.enums.FillType;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.SplitPaneOrientation;
import com.kanji.context.ContextOwner;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.utilities.CommonGuiElementsMaker;
import com.kanji.webPanel.ConnectionFailMessagePage;
import com.kanji.webPanel.WebPagePanel;

import javax.swing.*;

public class ProblematicJapaneseWordsPanel extends AbstractPanelWithHotkeysInfo
			implements ContextOwner {

	private ProblematicWordsController problematicWordsController;
	private MyList<JapaneseWordInformation> problematicWords;
	private MainPanel kanjiInformationPanel;
	private WebPagePanel englishDictionaryPanel;
	private WebPagePanel japaneseEnglishDictionaryPanel;

	public ProblematicJapaneseWordsPanel(ProblematicWordsController problematicWordsController) {
		this.problematicWordsController = problematicWordsController;
	}

	public void setProblematicWords (MyList <JapaneseWordInformation> problematicWords){
		this.problematicWords = problematicWords;
		englishDictionaryPanel = new WebPagePanel(this,
				new ConnectionFailMessagePage());
		japaneseEnglishDictionaryPanel = new WebPagePanel(this,
				new ConnectionFailMessagePage());
	}

	@Override public void createElements() {
		JSplitPane wordAndKanjiInformationSplitPane = CommonGuiElementsMaker.createSplitPane(
				SplitPaneOrientation.VERTICAL, problematicWords.getPanel(), kanjiInformationPanel.getPanel(),
				0.7);
		JSplitPane dictionariesSplitPane = CommonGuiElementsMaker.createSplitPane(
				SplitPaneOrientation.VERTICAL, japaneseEnglishDictionaryPanel.getPanel(),
				englishDictionaryPanel.getPanel(),0.5);

		JSplitPane wordAndDictionariesSplitPane = CommonGuiElementsMaker.createSplitPane(
				SplitPaneOrientation.HORIZONTAL, wordAndKanjiInformationSplitPane,
				dictionariesSplitPane,0.5);

		mainPanel.addRows(SimpleRowBuilder.createRow(FillType.BOTH, wordAndDictionariesSplitPane));

	}

	@Override public Object getContext() {
		return null; //TODO
	}
}

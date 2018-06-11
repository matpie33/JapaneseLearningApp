package com.kanji.panelsAndControllers.panels;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.model.WebContext;
import com.guimaker.options.ScrollPaneOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.Colors;
import com.kanji.constants.enums.SplitPaneOrientation;
import com.kanji.constants.strings.Urls;
import com.guimaker.webPanel.ContextOwner;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.problematicWords.ProblematicJapaneseWordsDisplayer;
import com.kanji.utilities.CommonGuiElementsCreator;
import com.kanji.panelSwitching.FocusableComponentsManager;
import com.guimaker.webPanel.WebPagePanel;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;

public class ProblematicJapaneseWordsPanel extends AbstractPanelWithHotkeysInfo
		implements ContextOwner {

	private ProblematicWordsController<JapaneseWord> problematicWordsController;
	private MyList<JapaneseWord> problematicWords;
	private MainPanel kanjiInformationPanel;
	private WebPagePanel englishDictionaryPanel;
	private WebPagePanel japaneseEnglishDictionaryPanel;
	private WebPagePanel kanjiKoohiWebPanel;
	private static final String TANGORIN_URL = "http://www.tangorin.com/";
	private ProblematicJapaneseWordsDisplayer problematicJapaneseWordsDisplayer;

	public ProblematicJapaneseWordsPanel(
			ProblematicWordsController<JapaneseWord> problematicWordsController,
			ApplicationWindow parent,
			ProblematicJapaneseWordsDisplayer problematicWordsDisplayer) {
		parentDialog = parent;
		this.problematicWordsController = problematicWordsController;
		kanjiInformationPanel = new MainPanel(null, true);
		englishDictionaryPanel = new WebPagePanel(this, null);
		japaneseEnglishDictionaryPanel = new WebPagePanel(this, null);
		kanjiKoohiWebPanel = new WebPagePanel(this, null);
		this.problematicJapaneseWordsDisplayer = problematicWordsDisplayer;
	}

	public void initialize() {
		problematicWords = problematicWordsController.getWordsToReviewList();

		japaneseEnglishDictionaryPanel.showPage(TANGORIN_URL);
		englishDictionaryPanel.showPage(Urls.DICTIONARY_PL_EN_MAIN_PAGE);
		String pageToRender = "";
		if (isLoginDataRemembered()) {
			pageToRender = Urls.KANJI_KOOHI_MAIN_PAGE;
		}
		else {
			pageToRender = Urls.KANJI_KOOHI_LOGIN_PAGE;
		}
		kanjiKoohiWebPanel.showPageWithoutGrabbingFocus(pageToRender);
	}

	private boolean isLoginDataRemembered() {
		//TODO duplicated code from problematic kanji displayer
		CookieManager cookieManager = (CookieManager) CookieHandler
				.getDefault();
		for (HttpCookie cookies : cookieManager.getCookieStore().getCookies()) {
			if (cookies.getName().equals("koohii")) {
				return true;
			}
		}
		return false;
	}

	public MainPanel getKanjiInformationPanel() {
		return kanjiInformationPanel;
	}

	public void searchWord(String word) {
		japaneseEnglishDictionaryPanel
				.showPage(TANGORIN_URL + "/general/" + word);
	}

	@Override
	public void setParentDialog(DialogWindow parentDialog) {
		super.setParentDialog(parentDialog);
	}

	@Override
	public void createElements() {

		FocusableComponentsManager focusableComponentsManager = new FocusableComponentsManager(
				mainPanel.getPanel());
		focusableComponentsManager.makeFocusable(problematicWords.getPanel());
		focusableComponentsManager
				.makeFocusable(japaneseEnglishDictionaryPanel.getWebPanel());
		focusableComponentsManager
				.makeFocusable(englishDictionaryPanel.getWebPanel());
		focusableComponentsManager
				.makeFocusable(kanjiInformationPanel.getPanel());
		focusableComponentsManager
				.makeFocusable(kanjiKoohiWebPanel.getWebPanel());
		JScrollPane scrollPaneForKanjiInformations = GuiElementsCreator
				.createScrollPane(new ScrollPaneOptions()
						.componentToWrap(kanjiInformationPanel.getPanel()));
		JSplitPane wordAndKanjiInformationSplitPane = CommonGuiElementsCreator
				.createSplitPane(SplitPaneOrientation.VERTICAL,
						scrollPaneForKanjiInformations,
						problematicWords.getPanel(), 0.2);
		JSplitPane dictionariesSplitPane = CommonGuiElementsCreator
				.createSplitPane(SplitPaneOrientation.VERTICAL,
						japaneseEnglishDictionaryPanel.getSwitchingPanel(),
						englishDictionaryPanel.getSwitchingPanel(), 0.5);

		JSplitPane dictionariesWithProblematicWordsSplitPane = CommonGuiElementsCreator
				.createSplitPane(SplitPaneOrientation.HORIZONTAL,
						dictionariesSplitPane, wordAndKanjiInformationSplitPane,
						0.3);

		JSplitPane fullSplitPane = CommonGuiElementsCreator
				.createSplitPane(SplitPaneOrientation.HORIZONTAL,
						dictionariesWithProblematicWordsSplitPane,
						kanjiKoohiWebPanel.getSwitchingPanel(), 0.7);

		mainPanel.addRow(SimpleRowBuilder
				.createRow(FillType.BOTH, fullSplitPane));
		setNavigationButtons(Anchor.WEST, createButtonClose());
	}

	@Override
	public WebContext getContext() {
		return null; //TODO implement it to return kanji tried to retrieve
	}

	public void showKoohiPage(int kanjiID) {
		showKoohiPage("" + kanjiID);
	}

	public void showKoohiPage(String kanjiData) {
		String uriText = Urls.KANJI_KOOHI_REVIEW_BASE_PAGE;
		uriText += kanjiData;
		kanjiKoohiWebPanel.showPageWithoutGrabbingFocus(uriText);
	}

}

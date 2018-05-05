package com.kanji.panelsAndControllers.panels;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.options.ScrollPaneOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.SplitPaneOrientation;
import com.kanji.constants.strings.Urls;
import com.kanji.context.ContextOwner;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.problematicWords.ProblematicJapaneseWordsDisplayer;
import com.kanji.utilities.CommonGuiElementsCreator;
import com.kanji.utilities.FocusableComponentCreator;
import com.kanji.webPanel.WebPagePanel;
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
	private final String KANJI_KOOHI_LOGIN_PAGE = "https://kanji.koohii.com/account";
	private final String KANJI_KOOHI_MAIN_PAGE = "https://kanji.koohii.com/study";
	private final String KANJI_KOOHI_REVIEW_BASE_PAGE = "https://kanji.koohii.com/study/kanji/";
	private ProblematicJapaneseWordsDisplayer problematicJapaneseWordsDisplayer;

	public ProblematicJapaneseWordsPanel(
			ProblematicWordsController<JapaneseWord> problematicWordsController,
			ApplicationWindow parent,
			ProblematicJapaneseWordsDisplayer problematicWordsDisplayer) {
		parentDialog = parent;
		this.problematicWordsController = problematicWordsController;
		kanjiInformationPanel = new MainPanel(BasicColors.MEDIUM_BLUE);
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
			pageToRender = KANJI_KOOHI_MAIN_PAGE;
		}
		else {
			pageToRender = KANJI_KOOHI_LOGIN_PAGE;
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

		FocusableComponentCreator focusableComponentCreator = new FocusableComponentCreator();
		focusableComponentCreator.makeFocusable(problematicWords.getPanel());
		focusableComponentCreator
				.makeFocusable(japaneseEnglishDictionaryPanel.getWebPanel());
		focusableComponentCreator
				.makeFocusable(englishDictionaryPanel.getWebPanel());
		focusableComponentCreator
				.makeFocusable(kanjiInformationPanel.getPanel());
		focusableComponentCreator
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
	public Object getContext() {
		return null; //TODO this should not be needed
		// TODO - its wrong that web panel requires kanji context owner
	}

	public void showKoohiPage(int kanjiID) {
		showKoohiPage("" + kanjiID);
	}

	public void showKoohiPage(String kanjiData) {
		String uriText = KANJI_KOOHI_REVIEW_BASE_PAGE;
		uriText += kanjiData;
		kanjiKoohiWebPanel.showPageWithoutGrabbingFocus(uriText);
	}

}

package com.kanji.problematicWords;

import com.guimaker.model.WebContext;
import com.guimaker.webPanel.ContextOwner;
import com.kanji.constants.strings.Prompts;
import com.kanji.constants.strings.Urls;
import com.kanji.context.KanjiContext;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.myList.MyList;
import com.kanji.model.WordRow;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.panelsAndControllers.panels.AbstractPanelWithHotkeysInfo;
import com.kanji.panelsAndControllers.panels.ProblematicKanjiPanel;
import com.kanji.utilities.KanjiCharactersReader;
import com.kanji.webPanel.KanjiKoohiWebPageHandler;
import com.kanji.windows.ApplicationWindow;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProblematicKanjiDisplayer
		implements ProblematicWordsDisplayer<Kanji>, ContextOwner {

	private ProblematicKanjiPanel problematicKanjiPanel;
	private KanjiContext kanjiContext;
	private KanjiCharactersReader kanjiCharactersReader;
	private MyList<Kanji> wordsToReviewList;
	private KanjiKoohiWebPageHandler kanjiKoohiWebPageHandler;

	public ProblematicKanjiDisplayer(ApplicationWindow applicationWindow,
			ProblematicWordsController<Kanji> controller) {

		problematicKanjiPanel = new ProblematicKanjiPanel(applicationWindow,
				controller, this);

		kanjiContext = KanjiContext.emptyContext();
		kanjiCharactersReader = KanjiCharactersReader.getInstance();
		kanjiCharactersReader.loadKanjisIfNeeded();
		wordsToReviewList = problematicKanjiPanel.getWordsToReviewList();
		controller.setProblematicWordsDisplayer(this);
		kanjiKoohiWebPageHandler = KanjiKoohiWebPageHandler.getInstance();
	}

	@Override
	public MyList<Kanji> getWordsToReviewList() {
		return wordsToReviewList;
	}

	@Override
	public void browseWord(WordRow<Kanji> wordRow) {
		String uriText = Urls.KANJI_KOOHI_REVIEW_BASE_PAGE;
		uriText += wordRow.getListElement().getId();
		problematicKanjiPanel.getKanjiKoohiWebPanel()
				.showPageWithoutGrabbingFocus(uriText);
		kanjiContext = new KanjiContext(kanjiCharactersReader
				.getKanjiById(wordRow.getListElement().getId()),
				wordRow.getListElement().getId());
	}

	@Override
	public WebContext getContext() {
		return new WebContext(kanjiContext.getKanjiCharacter(),
				Prompts.NO_KANJI_TO_DISPLAY);
	}

	@Override
	public void initializeWebPages() {
		problematicKanjiPanel.getEnglishPolishDictionaryWebPanel()
				.showPageWithoutGrabbingFocus(Urls.DICTIONARY_PL_EN_MAIN_PAGE);
		problematicKanjiPanel.getKanjiKoohiWebPanel()
				.showPageWithoutGrabbingFocus(
						kanjiKoohiWebPageHandler.getInitialPage());
	}

	@Override
	public AbstractPanelWithHotkeysInfo getPanel() {
		return problematicKanjiPanel;
	}

	@Override
	public boolean isListPanelFocused() {
		return problematicKanjiPanel.getFocusableComponentsManager()
				.getFocusedComponent().equals(wordsToReviewList.getPanel());
	}



	@Override
	public void focusPreviouslyFocusedElement() {
		problematicKanjiPanel.getFocusableComponentsManager()
				.focusPreviouslyFocusedElement();
	}

	public String getKanjiKoohiLoginCookieHeader() {
		return kanjiKoohiWebPageHandler.getKanjiKoohiLoginCookieHeader();
	}

	public void setLoginDataCookie(String loginDataCookie) throws IOException {
		kanjiKoohiWebPageHandler.setLoginDataCookie(loginDataCookie);
	}
}

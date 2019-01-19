package com.kanji.problematicWords;

import com.guimaker.list.myList.MyList;
import com.guimaker.model.WebContext;
import com.guimaker.panels.AbstractPanelWithHotkeysInfo;
import com.kanji.constants.strings.Prompts;
import com.kanji.constants.strings.Urls;
import com.kanji.context.KanjiContext;
import com.kanji.list.listElements.Kanji;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.panelsAndControllers.panels.ProblematicKanjiPanel;
import com.kanji.utilities.KanjiCharactersReader;
import com.kanji.webPageEnhancer.WebPageCallExecutor;
import com.kanji.webPanel.KanjiKoohiWebPageHandler;

import java.io.IOException;

public class ProblematicKanjiDisplayer
		implements ProblematicWordsDisplayer<Kanji> {

	private ProblematicKanjiPanel problematicKanjiPanel;
	private KanjiContext kanjiContext;
	private KanjiCharactersReader kanjiCharactersReader;
	private MyList<Kanji> wordsToReviewList;
	private KanjiKoohiWebPageHandler kanjiKoohiWebPageHandler;
	private ProblematicWordsController<Kanji> problematicWordsController;
	private WebPageCallExecutor webPageCallExecutor;

	public ProblematicKanjiDisplayer(
			ApplicationController applicationController) {

		problematicWordsController = new ProblematicWordsController<>(
				applicationController, this);
		problematicKanjiPanel = new ProblematicKanjiPanel(applicationController,
				problematicWordsController, this);
		problematicKanjiPanel.setParentDialog(
				applicationController.getApplicationWindow());
		kanjiContext = KanjiContext.emptyContext();
		kanjiCharactersReader = KanjiCharactersReader.getInstance();
		kanjiCharactersReader.loadKanjisIfNeeded();
		wordsToReviewList = problematicKanjiPanel.getWordsToReviewList();
		kanjiKoohiWebPageHandler = KanjiKoohiWebPageHandler.getInstance();
		webPageCallExecutor = new WebPageCallExecutor(problematicKanjiPanel
				.getKanjiKoohiWebPanel());
	}

	public ProblematicWordsController<Kanji> getProblematicWordsController() {
		return problematicWordsController;
	}

	@Override
	public MyList<Kanji> getWordsToReviewList() {
		return wordsToReviewList;
	}

	@Override
	public void browseWord(Kanji kanji) {
		String uriText = Urls.KANJI_KOOHI_REVIEW_BASE_PAGE;
		uriText += kanji.getId();
		webPageCallExecutor.openUrl(uriText);
		kanjiContext = new KanjiContext(
				kanjiCharactersReader.getKanjiById(kanji.getId()),
				kanji.getId());
	}

	@Override
	public WebContext getContext() {
		return new WebContext(kanjiContext.getKanjiCharacter(),
				Prompts.NO_KANJI_TO_DISPLAY);
	}

	@Override
	public void initializeWebPages() {
		problematicKanjiPanel.getEnglishPolishDictionaryWebPanel()
							 .showPageWithoutGrabbingFocus(
									 Urls.DICTIONARY_PL_EN_MAIN_PAGE);
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
									.getFocusedComponent()
									.equals(wordsToReviewList.getPanel());
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

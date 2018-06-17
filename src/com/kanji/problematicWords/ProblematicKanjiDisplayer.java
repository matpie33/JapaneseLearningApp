package com.kanji.problematicWords;

import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.model.WebContext;
import com.guimaker.webPanel.ContextOwner;
import com.kanji.constants.strings.Prompts;
import com.kanji.constants.strings.Titles;
import com.kanji.constants.strings.Urls;
import com.kanji.context.KanjiContext;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.listRows.RowInKanjiInformations;
import com.kanji.list.myList.ListConfiguration;
import com.kanji.list.myList.MyList;
import com.kanji.model.WordRow;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.panelsAndControllers.panels.AbstractPanelWithHotkeysInfo;
import com.kanji.panelsAndControllers.panels.ProblematicKanjiPanel;
import com.kanji.utilities.KanjiCharactersReader;
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
	private CookieManager cookieManager;
	private KanjiContext kanjiContext;
	private KanjiCharactersReader kanjiCharactersReader;
	private MyList<Kanji> wordsToReviewList;
	private final String KANJI_KOOHI_LOGIN_COOKIE = "RevTK";

	public ProblematicKanjiDisplayer(ApplicationWindow applicationWindow,
			ProblematicWordsController controller) {

		problematicKanjiPanel = new ProblematicKanjiPanel(
				ApplicationWindow.getKanjiFont(), applicationWindow, controller,
				this);
		cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
		kanjiContext = KanjiContext.emptyContext();
		kanjiCharactersReader = KanjiCharactersReader.getInstance();
		kanjiCharactersReader.loadKanjisIfNeeded();
		RowInKanjiInformations rowInKanjiInformations = new RowInKanjiInformations(
				applicationWindow, PanelDisplayMode.VIEW);
		rowInKanjiInformations.setProblematicWordsController(controller);
		wordsToReviewList = new MyList<>(applicationWindow, null,
				rowInKanjiInformations, Titles.PROBLEMATIC_KANJIS,
				new ListConfiguration().showButtonsLoadNextPreviousWords(false),
				Kanji.getInitializer());
		controller.setProblematicWordsDisplayer(this);
	}

	@Override
	public MyList<Kanji> getWordsToReviewList() {
		return wordsToReviewList;
	}

	@Override
	public void browseWord(WordRow<Kanji> wordRow) {
		String uriText = Urls.KANJI_KOOHI_REVIEW_BASE_PAGE;
		uriText += wordRow.getListElement().getId();
		problematicKanjiPanel.showPageInKoohi(uriText);
		kanjiContext = new KanjiContext(kanjiCharactersReader
				.getKanjiById(wordRow.getListElement().getId()),
				wordRow.getListElement().getId());
	}

	@Override
	public WordRow createWordRow(Kanji listElement, int rowNumber) {
		return new WordRow(listElement, rowNumber);
	}

	@Override
	public WebContext getContext() {
		return new WebContext(kanjiContext.getKanjiCharacter(),
				Prompts.NO_KANJI_TO_DISPLAY);
	}

	@Override
	public void initialize() {
		String pageToRender;
		if (isLoginDataRemembered()) {
			pageToRender = Urls.KANJI_KOOHI_MAIN_PAGE;
		}
		else {
			pageToRender = Urls.KANJI_KOOHI_LOGIN_PAGE;
		}
		problematicKanjiPanel.showPageInKoohi(pageToRender);
		problematicKanjiPanel.initialize();
	}

	@Override
	public AbstractPanelWithHotkeysInfo getPanel() {
		return problematicKanjiPanel;
	}

	@Override
	public boolean isListPanelFocused() {
		return problematicKanjiPanel.isListPanelFocused();
	}

	private boolean isLoginDataRemembered() {
		for (HttpCookie cookie : cookieManager.getCookieStore().getCookies()) {
			if (isCookieForLoginDataFromKoohiiPage(cookie)) {
				return true;
			}
		}
		return false;
	}

	public String getKanjiKoohiLoginCookieHeader() {
		List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
		for (HttpCookie cookie : cookies) {
			if (isCookieForLoginDataFromKoohiiPage(cookie)) {
				return cookie.toString();
			}
		}
		return "";
	}

	private boolean isCookieForLoginDataFromKoohiiPage(HttpCookie cookie) {

		return cookie.getName().equals(KANJI_KOOHI_LOGIN_COOKIE) && cookie
				.getDomain().equals("kanji.koohii.com");
	}

	public void setLoginDataCookie(String loginDataCookie) throws IOException {
		Map<String, List<String>> headers = new LinkedHashMap<>();
		headers.put("Set-Cookie", Arrays.asList(loginDataCookie));
		List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
		for (HttpCookie cookie : cookies) {
			if (cookie.getName().equals(KANJI_KOOHI_LOGIN_COOKIE)) {
				return;
			}
		}
		cookieManager.put(URI.create(Urls.KANJI_KOOHI_LOGIN_PAGE), headers);

	}

}

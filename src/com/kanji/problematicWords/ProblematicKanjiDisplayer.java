package com.kanji.problematicWords;

import com.kanji.constants.strings.Titles;
import com.kanji.context.ContextOwner;
import com.kanji.context.KanjiContext;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.listRows.RowInKanjiRepeatingList;
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
		implements ProblematicWordsDisplayer<Kanji>,
		ContextOwner<KanjiContext> {

	private ProblematicKanjiPanel problematicKanjiPanel;
	private final String KANJI_KOOHI_LOGIN_PAGE = "https://kanji.koohii.com/account";
	private final String KANJI_KOOHI_MAIN_PAGE = "https://kanji.koohii.com/study";
	private final String KANJI_KOOHI_REVIEW_BASE_PAGE = "http://kanji.koohii.com/study/kanji/";
	//TODO duplicated urls in problematic japanese words panel
	private CookieManager cookieManager;
	private KanjiContext kanjiContext;
	private KanjiCharactersReader kanjiCharactersReader;
	private MyList<Kanji> wordsToReviewList;

	public ProblematicKanjiDisplayer(ApplicationWindow applicationWindow,
			ProblematicWordsController controller) {

		problematicKanjiPanel = new ProblematicKanjiPanel(
				applicationWindow.getKanjiFont(), applicationWindow, controller,
				this);
		cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
		kanjiContext = KanjiContext.emptyContext();
		kanjiCharactersReader = KanjiCharactersReader.getInstance();
		kanjiCharactersReader.loadKanjisIfNeeded();
		wordsToReviewList = new MyList<>(applicationWindow, null,
				new RowInKanjiRepeatingList(controller),
				Titles.PROBLEMATIC_KANJIS, Kanji.getInitializer());
		controller.setProblematicWordsDisplayer(this);
	}

	@Override
	public MyList<Kanji> getWordsToReviewList() {
		return wordsToReviewList;
	}

	@Override
	public void browseWord(WordRow<Kanji> wordRow) {
		String uriText = KANJI_KOOHI_REVIEW_BASE_PAGE;
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
	public KanjiContext getContext() {
		return kanjiContext;
	}

	@Override
	public void initialize() {
		String pageToRender = "";
		if (isLoginDataRemembered()) {
			pageToRender = KANJI_KOOHI_MAIN_PAGE;
		}
		else {
			pageToRender = KANJI_KOOHI_LOGIN_PAGE;
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
		for (HttpCookie cookies : cookieManager.getCookieStore().getCookies()) {
			if (cookies.getName().equals("koohii")) {
				return true;
			}
		}
		return false;
	}

	public String getKanjiKoohiCookieHeader() {
		List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
		for (HttpCookie cookie : cookies) {
			if (cookie.getName().equalsIgnoreCase("koohii")) {
				return cookie.toString();
			}
		}
		return "";
	}

	public void setCookies(String cookieHeader) throws IOException {
		Map<String, List<String>> headers = new LinkedHashMap<>();
		headers.put("Set-Cookie", Arrays.asList(cookieHeader));
		cookieManager.put(URI.create(KANJI_KOOHI_LOGIN_PAGE), headers);
	}

}

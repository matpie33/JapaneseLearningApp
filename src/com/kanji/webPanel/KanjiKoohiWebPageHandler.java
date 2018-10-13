package com.kanji.webPanel;

import com.kanji.constants.strings.Urls;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KanjiKoohiWebPageHandler {

	private final String KANJI_KOOHI_LOGIN_COOKIE = "RevTK";
	private CookieManager cookieManager;
	private static KanjiKoohiWebPageHandler kanjiKoohiWebPageHandler;

	private KanjiKoohiWebPageHandler() {
		cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
	}

	public static KanjiKoohiWebPageHandler getInstance (){
		if (kanjiKoohiWebPageHandler == null){
			kanjiKoohiWebPageHandler = new KanjiKoohiWebPageHandler();
		}
		return kanjiKoohiWebPageHandler;
	}

	public String getInitialPage() {
		return isLoginDataRemembered() ?
				Urls.KANJI_KOOHI_MAIN_PAGE :
				Urls.KANJI_KOOHI_LOGIN_PAGE;
	}

	private boolean isLoginDataRemembered() {
		CookieManager cookieManager = (CookieManager) CookieHandler
				.getDefault();
		for (HttpCookie cookie : cookieManager.getCookieStore().getCookies()) {
			if (isCookieForLoginDataFromKoohiiPage(cookie)) {
				return true;
			}
		}
		return false;
	}

	private boolean isCookieForLoginDataFromKoohiiPage(HttpCookie cookie) {

		return cookie.getName().equals(KANJI_KOOHI_LOGIN_COOKIE) && cookie
				.getDomain().equals("kanji.koohii.com");
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

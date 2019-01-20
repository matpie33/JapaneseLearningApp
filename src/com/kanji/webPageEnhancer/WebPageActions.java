package com.kanji.webPageEnhancer;

import com.guimaker.webPanel.WebPagePanel;
import com.kanji.panelsAndControllers.controllers.ApplicationController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.util.Scanner;

public class WebPageActions {

	private WebPagePanel webPagePanel;
	private final static String FIND_KANJI_KEYWORD_JS = "findKanjiPolishKeyword.js";
	private final static String CREATE_TOOLTIP_JS = "createTooltip.js";
	private final static String GET_KANJI_KEYWORDS = "kanjiKeywords.js";
	private final static String CALL_ENGLISH_DICTIONARY = "callEnglishDictionary.js";

	private final static String JAVASCRIPT_ROOT_DIRECTORY = "js/";

	private ApplicationController applicationController;
	private EnglishDictionaryCaller englishDictionaryCaller;

	public WebPageActions(WebPagePanel webPagePanel,
			ApplicationController applicationController) {
		this.webPagePanel = webPagePanel;
		this.applicationController = applicationController;
		englishDictionaryCaller = new EnglishDictionaryCaller();
	}

	public AbstractAction createActionFindKanjiPolishKeyword() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String getKanjiKeywords = getJavascriptFromFile(
						GET_KANJI_KEYWORDS);
				String findKanjiJavascript = getJavascriptFromFile(
						FIND_KANJI_KEYWORD_JS);
				String createTooltip = getJavascriptFromFile(CREATE_TOOLTIP_JS);
				webPagePanel.addJavaObjectReferenceForJavascript(
						applicationController);
				webPagePanel.executeJavascript(
						getKanjiKeywords + findKanjiJavascript + createTooltip);
			}

		};
	}

	public AbstractAction createActionCallEnglishDictionary() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String createTooltip = getJavascriptFromFile(CREATE_TOOLTIP_JS);
				String callEnglishDictionary = getJavascriptFromFile(
						CALL_ENGLISH_DICTIONARY);
				webPagePanel.addJavaObjectReferenceForJavascript(
						englishDictionaryCaller);
				webPagePanel.executeJavascript(
						callEnglishDictionary + createTooltip);

			}
		};
	}

	private String getJavascriptFromFile(String filename) {
		InputStream resourceAsStream = getClass().getClassLoader()
												 .getResourceAsStream(
														 JAVASCRIPT_ROOT_DIRECTORY
																 + filename);
		return convertStreamToString(resourceAsStream);
	}

	private String convertStreamToString(InputStream inputStream) {
		Scanner s = new Scanner(inputStream).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

}

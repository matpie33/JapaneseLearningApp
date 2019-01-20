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
	private final static String JAVASCRIPT_ROOT_DIRECTORY = "js/";
	private ApplicationController applicationController;

	public WebPageActions(WebPagePanel webPagePanel,
			ApplicationController applicationController) {
		this.webPagePanel = webPagePanel;
		this.applicationController = applicationController;
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

			private String getJavascriptFromFile(String filename) {
				InputStream resourceAsStream = getClass().getClassLoader()
														 .getResourceAsStream(
																 JAVASCRIPT_ROOT_DIRECTORY
																		 + filename);
				return convertStreamToString(resourceAsStream);
			}
		};
	}

	private String convertStreamToString(InputStream inputStream) {
		Scanner s = new Scanner(inputStream).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

}

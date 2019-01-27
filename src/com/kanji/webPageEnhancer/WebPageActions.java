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
	private final static String GET_KANJI_KEYWORDS = "kanjiKeywords.js";

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
				webPagePanel.addJavaObjectReferenceForJavascript(
						applicationController);
				webPagePanel.executeJavascriptFiles(
						GET_KANJI_KEYWORDS,FIND_KANJI_KEYWORD_JS,
								webPagePanel.getJavascriptForShowingTooltip());
			}

		};
	}



}

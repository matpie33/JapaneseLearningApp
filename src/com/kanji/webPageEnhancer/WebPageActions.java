package com.kanji.webPageEnhancer;

import com.guimaker.webPanel.WebPagePanel;
import com.kanji.panelsAndControllers.controllers.ApplicationController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.util.Scanner;

public class WebPageActions {

	private WebPagePanel webPagePanel;
	private final static String JAVASCRIPT_FILENAME =
			"findKanjiPolishKeyword.js";
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
				InputStream resourceAsStream = getClass().getClassLoader()
														 .getResourceAsStream(
																 JAVASCRIPT_FILENAME);
				webPagePanel.addJavaObjectReferenceForJavascript(
						applicationController);
				webPagePanel.executeJavascript(
						convertStreamToString(resourceAsStream));
			}
		};
	}

	private String convertStreamToString(InputStream inputStream) {
		Scanner s = new Scanner(inputStream).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

}

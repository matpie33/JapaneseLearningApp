package com.kanji.controllers;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.guimaker.panels.MainPanel;
import com.kanji.Row.KanjiWords;
import com.kanji.constants.Prompts;
import com.kanji.fileReading.KanjiCharactersReader;
import com.kanji.panels.ProblematicKanjiPanel;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;

public class ProblematicKanjisController {

	private boolean useInternet;
	private int repeatedProblematics;
	private ProblematicKanjiPanel problematicKanjiPanel;
	private KanjiCharactersReader kanjiCharactersReader;
	private List<KanjiRow> kanjisToBrowse;
	private Set<Integer> problematicKanjisIds;
	private KanjiWords kanjiInfos;

	private class KanjiRow {
		private MainPanel panel;
		private int kanjiId;

		private KanjiRow(MainPanel p, int kanjiId) {
			panel = p;
			this.kanjiId = kanjiId;
		}

		private MainPanel getPanel() {
			return panel;
		}

		private int getId() {
			return kanjiId;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof KanjiRow == false) {
				return false;
			}
			KanjiRow row = (KanjiRow) o;
			return row.getPanel().equals(panel) && row.getId() == kanjiId;
		}

	}

	public ProblematicKanjisController(ProblematicKanjiPanel problematicKanjiPanel,
			Set<Integer> problematicKanji, KanjiWords kanjis) {
		this.problematicKanjiPanel = problematicKanjiPanel;
		kanjisToBrowse = new ArrayList<>();
		useInternet = true;
		this.kanjiCharactersReader = new KanjiCharactersReader();
		kanjiCharactersReader.load();
		problematicKanjisIds = problematicKanji;
		kanjiInfos = kanjis;
		// TODO better use existing kanji reader instead of
		// creating new here
	}

	public KanjiCharactersReader getKanjisReader() {
		return kanjiCharactersReader;
	}

	public void goToNextResource() {
		KanjiRow row = kanjisToBrowse.get(0);
		goToSpecifiedResource(row.getPanel(), row.getId());
	}

	public void goToSpecifiedResource(MainPanel panel, int kanjiId) {
		KanjiRow k = new KanjiRow(panel, kanjiId);
		repeatedProblematics++;
		kanjisToBrowse.remove(k);
		problematicKanjiPanel.highlightRow(k.getPanel().getPanel());
		// TODO naming is bad here
		if (useInternet) {
			browseKanji(k);
		}
		else {
			problematicKanjiPanel.showKanjiOffline(kanjiCharactersReader.getKanjiById(k.getId()));
		}

	}

	private void browseKanji(KanjiRow kanjiRow) {
		String uriText = "http://kanji.koohii.com/study/kanji/";
		uriText += kanjiRow.getId();
		URI uriObject = constructUriFromText(uriText);
		if (uriObject != null) {
			openUrlInBrowser(uriObject);
		}
	}

	private void openUrlInBrowser(URI uriObject) {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(uriObject);
			}
			catch (IOException ex) {
				ex.printStackTrace();
				problematicKanjiPanel.showMsg("Problems with browsing");
			}
		}
		else {
			problematicKanjiPanel.showMsg("Desktop unsupported");
		}
	}

	private URI constructUriFromText(String text) {
		URI uriObject = null;
		try {
			uriObject = new URI(text);
		}
		catch (URISyntaxException e1) {
			e1.printStackTrace();
			problematicKanjiPanel.showMsg("error");
			return null;
		}
		return uriObject;
	}

	public void addKanjiRow(MainPanel panel, int kanjiId) {
		KanjiRow k = new KanjiRow(panel, kanjiId);
		kanjisToBrowse.add(k);
	}

	public void setUseInternet(boolean useInternet) {
		this.useInternet = useInternet;
	}

	public void buildRowsForProblematicKanjis() {
		for (Integer kanjiId : problematicKanjisIds) {
			problematicKanjiPanel.buildRow(kanjiInfos.getWordForId(kanjiId), kanjiId);
		}
	}

	public void showNextKanji() {
		if (hasMoreKanji()) {
			goToNextResource();
		}
		else {
			problematicKanjiPanel.showMsg(Prompts.noMoreKanjis);
		}
	}

	public boolean allProblematicKanjisRepeated() {
		return repeatedProblematics == problematicKanjisIds.size();
	}

	public boolean hasMoreKanji() {
		return !kanjisToBrowse.isEmpty();
	}

	public void hideProblematicsPanel(DialogWindow parentDialog) {
		assert (parentDialog.getParent() instanceof ApplicationWindow);
		ApplicationWindow parent = (ApplicationWindow) parentDialog.getParent();
		parent.addButtonIcon();
		parentDialog.getContainer().dispose();
		if (allProblematicKanjisRepeated()) {
			parent.removeButtonProblematicsKanji();
		}
	}

	public void checkForTooManyRowsToDisplaAll(int maximumNumberOfRowsVisible) {
		if (problematicKanjisIds.size() > maximumNumberOfRowsVisible) {
			problematicKanjiPanel.limitSize();
		}
	}

}

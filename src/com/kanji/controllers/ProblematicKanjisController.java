package com.kanji.controllers;

import java.awt.Desktop;
import java.awt.Font;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.kanji.Row.KanjiInformation;
import com.kanji.constants.Prompts;
import com.kanji.fileReading.KanjiCharactersReader;
import com.kanji.listSearching.KanjiIdChecker;
import com.kanji.listSearching.SearchingDirection;
import com.kanji.model.KanjiRow;
import com.kanji.myList.MyList;
import com.kanji.panels.KanjiPanel;
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
	private MyList<KanjiInformation> kanjiList;
	private KanjiPanel kanjiPanel;
	private Font kanjiFont;

	public ProblematicKanjisController(Font kanjiFont, ProblematicKanjiPanel problematicKanjiPanel,
			Set<Integer> problematicKanjisSet, MyList<KanjiInformation> kanjiList) {
		this.problematicKanjiPanel = problematicKanjiPanel;
		kanjisToBrowse = new ArrayList<>();
		useInternet = true;
		kanjiCharactersReader = KanjiCharactersReader.getInstance();
		kanjiCharactersReader.loadKanjisIfNeeded();
		problematicKanjisIds = problematicKanjisSet;
		this.kanjiList = kanjiList;
		this.kanjiFont = kanjiFont;
	}

	private void goToNextResource() {
		KanjiRow row = kanjisToBrowse.get(0);
		goToSpecifiedResource(row);
	}

	public void goToSpecifiedResource(KanjiRow row) {
		repeatedProblematics++;
		kanjisToBrowse.remove(row);
		if (useInternet) {
			browseKanji(row);
		}
		else {
			String kanji = kanjiCharactersReader.getKanjiById(row.getId());
			if (kanjiPanel == null || !kanjiPanel.isDisplayAble()) {
				kanjiPanel = new KanjiPanel(kanjiFont, kanji, this);
				problematicKanjiPanel.showKanjiDialog(kanjiPanel);
			}
			else {
				kanjiPanel.changeKanji(kanji);
			}

		}
		problematicKanjiPanel.highlightRow(row.getRowNumber());

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
				problematicKanjiPanel.showMessage("Problems with browsing");
			}
		}
		else {
			problematicKanjiPanel.showMessage("Desktop unsupported");
		}
	}

	private URI constructUriFromText(String text) {
		URI uriObject = null;
		try {
			uriObject = new URI(text);
		}
		catch (URISyntaxException e1) {
			e1.printStackTrace();
			problematicKanjiPanel.showMessage("error");
			return null;
		}
		return uriObject;
	}

	public void addKanjiRow(int kanjiId) {
		KanjiRow k = new KanjiRow(kanjiId, kanjisToBrowse.size());
		kanjisToBrowse.add(k);
	}

	public void setUseInternet(boolean useInternet) {
		this.useInternet = useInternet;
	}

	public List<KanjiInformation> getKanjis() {
		List<KanjiInformation> kanjis = new ArrayList<>();
		for (Integer kanjiId : problematicKanjisIds) {
			kanjis.add(new KanjiInformation(kanjiList
					.findRowBasedOnPropertyStartingFromHighlightedWord(new KanjiIdChecker(),
							kanjiId, SearchingDirection.FORWARD, kanjiList.getParent())
					.getKanjiKeyword(), kanjiId));
		}
		return kanjis;
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
		if (!problematicKanjiPanel.allProblematicKanjisRepeated()) {
			parent.addButtonIcon();
		}
		parentDialog.getContainer().dispose();
		if (allProblematicKanjisRepeated()) {
			parent.removeButtonProblematicsKanji();
		}
	}

	public void limitSizeIfTooManyRows(int maximumNumberOfRowsVisible) {
		if (problematicKanjisIds.size() > maximumNumberOfRowsVisible) {
			problematicKanjiPanel.limitSize();
		}
	}

	public void showNextKanjiOrCloseChildDialog() {
		if (hasMoreKanji())
			goToNextResource();
		else {
			problematicKanjiPanel.getDialog().closeChild();
			problematicKanjiPanel.getDialog().showMessageDialog(Prompts.NO_MORE_KANJIS);
		}
	}

	public Set<Integer> getProblematicKanjis() {
		return problematicKanjisIds;
	}

	public int getNumberOfRows() {
		return kanjisToBrowse.size();
	}

}
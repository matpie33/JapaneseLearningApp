package com.kanji.controllers;

import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.kanji.enums.ApplicationSaveableState;
import com.kanji.listElements.KanjiInformation;
import com.kanji.strings.Prompts;
import com.kanji.strings.Titles;
import com.kanji.utilities.KanjiCharactersReader;
import com.kanji.listSearching.KanjiIdChecker;
import com.kanji.listSearching.SearchingDirection;
import com.kanji.model.KanjiRow;
import com.kanji.saving.ProblematicKanjisState;
import com.kanji.myList.MyList;
import com.kanji.listRows.RowInKanjiRepeatingList;
import com.kanji.panels.KanjiPanel;
import com.kanji.panels.ProblematicKanjiPanel;
import com.kanji.saving.ApplicationStateManager;
import com.kanji.saving.SavingInformation;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;

import javax.swing.*;

public class ProblematicKanjisController implements ApplicationStateManager{

	private boolean useInternet;
	private ProblematicKanjiPanel problematicKanjiPanel;
	private KanjiCharactersReader kanjiCharactersReader;
	private List<KanjiRow> kanjisToBrowse;
	private MyList<KanjiInformation> kanjiList;
	private KanjiPanel kanjiPanel;
	private Font kanjiFont;
	private MyList<KanjiInformation> kanjiRepeatingList;
	private ApplicationController applicationController;
	private ApplicationWindow applicationWindow;

	public ProblematicKanjisController(ApplicationWindow applicationWindow,
			Font kanjiFont,	MyList<KanjiInformation> kanjiList) {
		applicationController = applicationWindow.getApplicationController();
		this.applicationWindow = applicationWindow;
		this.problematicKanjiPanel = new ProblematicKanjiPanel(kanjiFont, kanjiList,
				applicationWindow, this);
		kanjisToBrowse = new ArrayList<>();
		useInternet = true;
		kanjiCharactersReader = KanjiCharactersReader.getInstance();
		kanjiCharactersReader.loadKanjisIfNeeded();
		kanjiRepeatingList = new MyList<>(applicationWindow, applicationWindow.getStartingPanel(), null,
				new RowInKanjiRepeatingList(this), Titles.PROBLEMATIC_KANJIS);
		this.kanjiList = kanjiList;
		this.kanjiFont = kanjiFont;
	}

	public ProblematicKanjiPanel getProblematicKanjiPanel() {
		return problematicKanjiPanel;
	}

	public void createProblematicKanjisList (List <KanjiInformation> reviewedKanjis,
			List <KanjiInformation> notReviewedKanjis){
		for (int i=0; i< reviewedKanjis.size(); i++){
			KanjiInformation kanjiInformation = reviewedKanjis.get(i);
			kanjiRepeatingList.addWord(kanjiInformation);

		}
		int firstUnreviewedKanjiRowNumber = reviewedKanjis.size();
		for (int i=0; i< notReviewedKanjis.size(); i++){
			KanjiInformation kanjiInformation = notReviewedKanjis.get(i);
			kanjiRepeatingList.addWord(kanjiInformation);
			kanjisToBrowse.add(new KanjiRow(kanjiInformation.getKanjiID(),
					firstUnreviewedKanjiRowNumber+i));
		}
	}

	public void highlightReviewedWords(int numberOfReviewedWords){
		for (int i=0; i<numberOfReviewedWords; i++){
			kanjiRepeatingList.highlightRow(i);
		}
	}

	public void addProblematicKanjis(Set<Integer> problematicKanjisIds){
		if (kanjisToBrowse.isEmpty()){
			kanjiRepeatingList.cleanWords();
		}
		for (Integer kanjiId: problematicKanjisIds){
			KanjiRow k = new KanjiRow(kanjiId, kanjiRepeatingList.getNumberOfWords());
			boolean addedToList = kanjiRepeatingList.addWord(new KanjiInformation(kanjiList
					.findRowBasedOnPropertyStartingFromBeginningOfList(new KanjiIdChecker(),
							kanjiId, SearchingDirection.FORWARD)
					.getKanjiKeyword(), kanjiId));
			if (addedToList){
				kanjisToBrowse.add(k);
			}

		}

	}

	private void goToNextResource() {
		KanjiRow row = kanjisToBrowse.get(0);
		goToSpecifiedResource(row);
	}

	public void goToSpecifiedResource(KanjiRow row) {
		kanjisToBrowse.remove(row);
		if (useInternet) {
			browseKanji(row);
		}
		else {
			String kanji = kanjiCharactersReader.getKanjiById(row.getId());
			if (kanjiPanel == null || !kanjiPanel.isDisplayable()) {
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

	public void setUseInternet(boolean useInternet) {
		this.useInternet = useInternet;
	}

	public boolean allProblematicKanjisRepeated() {
		return kanjiRepeatingList.areAllWordsHighlighted();
	}

	public boolean hasMoreKanji() {
		return !kanjisToBrowse.isEmpty();
	}

	public void closeDialogAndManageState(DialogWindow parentDialog) {
		assert (parentDialog.getParent() instanceof ApplicationWindow);
		ApplicationWindow parent = (ApplicationWindow) parentDialog.getParent();
		if (!allProblematicKanjisRepeated()) {
			parent.addButtonIcon();
		}
		else{
			applicationController.finishedRepeating();
			applicationController.saveProject();
		}
		parentDialog.getContainer().dispose();


	}

	public void limitSizeIfTooManyRows(int maximumNumberOfRowsVisible) {
		if (kanjiRepeatingList.getNumberOfWords() > maximumNumberOfRowsVisible) {
			problematicKanjiPanel.limitSize();
		}
	}

	public AbstractAction createActionShowNextKanjiOrCloseDialog() {
		return new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e){
				if (hasMoreKanji())
					goToNextResource();
				else {
					problematicKanjiPanel.getDialog().closeChild();
					problematicKanjiPanel.getDialog().showMessageDialog(Prompts.NO_MORE_KANJIS);
				}
			}
		};

	}

	public AbstractAction createActionForShowingKanjiUsingInternet (boolean useInternet){
		return new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) {
				setUseInternet(useInternet);
				JRadioButton source = (JRadioButton) e.getSource();
				source.setSelected(true);
			}
		};
	}

	public int getNumberOfRows() {
		return kanjiRepeatingList.getNumberOfWords();
	}

	public MyList <KanjiInformation> getKanjiRepeatingList (){
		return kanjiRepeatingList;
	}

	@Override public SavingInformation getApplicationState() {
		ProblematicKanjisState information = new ProblematicKanjisState(
				kanjiRepeatingList.getHighlightedWords(), kanjiRepeatingList.getNotHighlightedWords());
		SavingInformation savingInformation = applicationController.getApplicationState();
		savingInformation.setProblematicKanjisState(information);
		return savingInformation;
	}

	@Override
	public void restoreState(SavingInformation savingInformation){
		applicationWindow.displayMessageAboutUnfinishedRepeating();
		applicationWindow.showProblematicKanjiDialog(savingInformation.getProblematicKanjisState());
	}

}

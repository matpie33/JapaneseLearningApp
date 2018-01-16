package com.kanji.controllers;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

import com.kanji.kanjiContext.KanjiContext;
import com.kanji.kanjiContext.KanjiContextOwner;
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
import com.kanji.panels.ProblematicKanjiPanel;
import com.kanji.saving.ApplicationStateManager;
import com.kanji.saving.SavingInformation;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;

import javax.swing.*;

public class ProblematicKanjisController implements ApplicationStateManager, KanjiContextOwner{

	private ProblematicKanjiPanel problematicKanjiPanel;
	private KanjiCharactersReader kanjiCharactersReader;
	private List<KanjiRow> kanjisToBrowse;
	private MyList<KanjiInformation> kanjiList;
	private MyList<KanjiInformation> kanjiRepeatingList;
	private ApplicationController applicationController;
	private ApplicationWindow applicationWindow;
	private final String KANJI_KOOHI_LOGIN_PAGE = "https://kanji.koohii.com/account";
	private final String KANJI_KOOHI_MAIN_PAGE = "https://kanji.koohii.com/study";
	private CookieManager cookieManager;
	private KanjiContext kanjiContext;

	public ProblematicKanjisController(ApplicationWindow applicationWindow,
			Font kanjiFont,	MyList<KanjiInformation> kanjiList) {
		applicationController = applicationWindow.getApplicationController();
		this.applicationWindow = applicationWindow;
		this.problematicKanjiPanel = new ProblematicKanjiPanel(kanjiFont, kanjiList,
				applicationWindow, this);
		kanjisToBrowse = new ArrayList<>();
		kanjiCharactersReader = KanjiCharactersReader.getInstance();
		kanjiCharactersReader.loadKanjisIfNeeded();
		kanjiRepeatingList = new MyList<>(applicationWindow, applicationWindow.getStartingPanel(), null,
				new RowInKanjiRepeatingList(this), Titles.PROBLEMATIC_KANJIS);
		this.kanjiList = kanjiList;
		cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
		kanjiContext = KanjiContext.emptyContext();
	}

	public String getKanjiKoohiLoginPageUrl (){

		String pageToRender = "";
		if (isLoginDataRemembered()){
			pageToRender = KANJI_KOOHI_MAIN_PAGE;
		}
		else{
			pageToRender = KANJI_KOOHI_LOGIN_PAGE;
		}
		return pageToRender;
	}

	private boolean isLoginDataRemembered (){
		for (HttpCookie cookies: cookieManager.getCookieStore().getCookies()){
			if (cookies.getName().equals("koohii")){
				return true;
			}
		}
		return false;
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
		kanjiContext = new KanjiContext(kanjiCharactersReader.getKanjiById(row.getId()), row.getId());
		browseKanji(row);
		problematicKanjiPanel.highlightRow(row.getRowNumber());

	}

	private void browseKanji(KanjiRow kanjiRow) {
		String uriText = "http://kanji.koohii.com/study/kanji/";
		uriText += kanjiRow.getId();
		problematicKanjiPanel.showPageInKoohi(uriText);
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
		parent.addButtonIcon();
		if (allProblematicKanjisRepeated()) {
			applicationController.finishedRepeating();
			applicationController.saveProject();
		}
		parentDialog.getContainer().dispose();
	}


	public AbstractAction createActionShowNextKanjiOrCloseDialog() {
		return new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e){
				if (!problematicKanjiPanel.isListPanelFocused()){
					return;
				}
				if (hasMoreKanji())
					goToNextResource();
				else {
					problematicKanjiPanel.getDialog().showMessageDialog(Prompts.NO_MORE_KANJIS);
				}
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

	public List <String> getCookieHeaders (){
		return cookieManager.getCookieStore().getCookies().stream().map(cookie ->
			cookie.toString()).collect(Collectors.toList());
	}

	public void setCookies(List <String> cookiesHeaders) throws IOException {
		Map<String, List <String>> headers = new LinkedHashMap<>();
		headers.put("Set-Cookie", cookiesHeaders);
		cookieManager.put(URI.create(KANJI_KOOHI_LOGIN_PAGE), headers);
	}

	@Override public KanjiContext getKanjiContext() {
		return kanjiContext;
	}
}

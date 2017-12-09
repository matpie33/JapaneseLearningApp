package com.kanji.controllers;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import javax.swing.JFileChooser;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import com.kanji.enums.ApplicationSaveableState;
import com.kanji.enums.ListWordType;
import com.kanji.listElements.JapaneseWordInformation;
import com.kanji.listElements.KanjiInformation;
import com.kanji.listElements.RepeatingInformation;
import com.kanji.listRows.RowInJapaneseWordInformations;
import com.kanji.panels.ProblematicKanjiPanel;
import com.kanji.panels.RepeatingWordsPanel;
import com.kanji.strings.*;
import com.kanji.enums.ApplicationPanels;
import com.kanji.enums.SavingStatus;
import com.kanji.exception.DuplicatedWordException;
import com.kanji.model.KanjisAndRepeatingInfo;
import com.kanji.myList.MyList;
import com.kanji.listRows.RowInKanjiInformations;
import com.kanji.listRows.RowInRepeatingList;
import com.kanji.panels.LoadingPanel;
import com.kanji.range.SetOfRanges;
import com.kanji.saving.ApplicationStateManager;
import com.kanji.utilities.CustomFileReader;
import com.kanji.saving.LoadingAndSaving;
import com.kanji.saving.SavingInformation;
import com.kanji.windows.ApplicationWindow;

public class ApplicationController implements ApplicationStateManager {

	private RepeatingWordsController repeatingWordsPanelController;
	private ApplicationWindow parent;
	private MyList<KanjiInformation> kanjiList;
	private MyList<RepeatingInformation> kanjiRepeatingDates;
	private MyList<RepeatingInformation> japaneseWordsRepeatingDates;
	private MyList<JapaneseWordInformation> japaneseWords;
	private LoadingAndSaving loadingAndSaving;
	private Set<Integer> problematicKanjis;
	private boolean isClosingSafe;
	private ApplicationStateManager applicationStateManager;
	private Map <ApplicationSaveableState, ApplicationStateManager> applicationStateToManagerMap;
	private ProblematicKanjisController problematicKanjisController;

	public ApplicationController(ApplicationWindow parent) {
		problematicKanjis = new HashSet<>();
		this.parent = parent;
		isClosingSafe = true;
		applicationStateManager = this;
		applicationStateToManagerMap = new HashMap<>();

		loadingAndSaving = new LoadingAndSaving();

	}

	public ProblematicKanjiPanel getProblematicKanjiPanel (){
		return problematicKanjisController.getProblematicKanjiPanel();
	}

	public RepeatingWordsPanel getRepeatingWordsPanel (){
		return repeatingWordsPanelController.getRepeatingWordsPanel();
	}

	public void initializeApplicationStateManagers(){
		problematicKanjisController = new ProblematicKanjisController(parent,
				parent.getKanjiFont(), kanjiList);
		this.repeatingWordsPanelController = new RepeatingWordsController(parent);
		applicationStateToManagerMap.put(ApplicationSaveableState.REPEATING_WORDS,
				repeatingWordsPanelController);
		applicationStateToManagerMap.put(ApplicationSaveableState.REVIEWING_PROBLEMATIC_KANJIS,
				problematicKanjisController);
	}

	//TODO dependencies between classes are weird and should be reconsidered

	public void initializeListsElements() {
		initializeKanjiList();
		initializeKanjiRepeatingDates();
		initializeJapaneseWordsList();
		initializeJapaneseRepeatingDates();
	}

	private void initializeJapaneseWordsList(){
		japaneseWords = new MyList<>(parent, this, new RowInJapaneseWordInformations(parent),
				Titles.KANJI_LIST, true,
				JapaneseWordInformation.getElementsTypesAndLabels(), ListWordType.JAPANESE_WORD);
		japaneseWords.addWord(new JapaneseWordInformation("ねこ", "kot"));
		japaneseWords.addWord(new JapaneseWordInformation("犬",
				"いぬ", "pies"));
	}

	private void initializeJapaneseRepeatingDates(){
		japaneseWordsRepeatingDates = new MyList<>(parent,this,
				new RowInRepeatingList(), Titles.REPEATING_LIST, false,
				RepeatingInformation.getElementsTypesAndLabels(), ListWordType.REPEATING_DATA);
		japaneseWordsRepeatingDates.addWord(
				new RepeatingInformation("abc", LocalDateTime.of(1993, 11, 13, 13, 25), true, "3 minuty"));
		japaneseWordsRepeatingDates.addWord(
				new RepeatingInformation("abc", LocalDateTime.of(2005, 1, 1, 11, 11), true, "4 minuty"));
		japaneseWordsRepeatingDates.addWord(
				new RepeatingInformation("abc", LocalDateTime.of(2000, 12, 31, 10, 0), true, "5 minut"));
	}

	private JFileChooser createFileChooser(){
		// TODO think about some more clever way of determining whether we are
		// in test or deployment directory
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(getWorkingDirectory(fileChooser));
		return fileChooser;
	}

	public void loadKanjiList (){
		JFileChooser fileChooser = createFileChooser();
		int option = fileChooser.showOpenDialog(parent.getContainer());
		if (option == JFileChooser.CANCEL_OPTION){
			return;
		}
		File file = fileChooser.getSelectedFile();
		CustomFileReader fileReader = new CustomFileReader();
		try {
			KanjisAndRepeatingInfo words = fileReader.readFile(file);
			List <KanjiInformation> kanjiInformations = words.getKanjiInformations();
			List <RepeatingInformation> repeatingInformations = words.getRepeatingInformations();
			Set <Integer> problematicKanjis = words.getProblematicKanjis();
			setProblematicKanjis(problematicKanjis);
			kanjiList.cleanWords();
			kanjiRepeatingDates.cleanWords();
			for (KanjiInformation kanjiInformation: kanjiInformations){
				kanjiList.addWord(kanjiInformation);
			}
			for (RepeatingInformation repeatingInformation: repeatingInformations){
				kanjiRepeatingDates.addWord(repeatingInformation);
			}
		}
		catch (DuplicatedWordException|IOException e) {
			parent.showMessageDialog(e.getMessage());
			e.printStackTrace();
		}
	}

	public void openKanjiProject() {
		File fileToSave = openFile();
		if (!fileToSave.exists())
			return;

		SavingInformation savingInformation = null;
		try {
			savingInformation = loadingAndSaving.load(fileToSave);
		}
		catch (Exception e1) {
			parent.showMessageDialog("Exception loading from file.");
			e1.printStackTrace();
			return;
		}
		loadingAndSaving.setFileToSave(fileToSave);
		try {
			if (savingInformation.getKanjiKoohiCookiesHeaders() != null){
				//TODO this should go to application controller's "restore
				// state method along with filling the mylists"
				problematicKanjisController.setCookies(savingInformation.getKanjiKoohiCookiesHeaders());
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			parent.showMessageDialog("Error setting cookies");
		}

		kanjiList.cleanWords();
		setProblematicKanjis(savingInformation.getProblematicKanjis());
		parent.updateTitle(fileToSave.toString());
		parent.changeSaveStatus(SavingStatus.NO_CHANGES);

		showLoadedKanjisInPanel(savingInformation);
		showLoadedRepeatingInformations(savingInformation.getRepeatingList());

	}

	private void showLoadedKanjisInPanel(SavingInformation savingInformation) {
		final LoadingPanel loadingPanel = parent.showProgressDialog();
		SwingWorker s = new SwingWorker<Void, Integer>() {

			private JProgressBar progressBar = loadingPanel.getProgressBar();

			@Override
			public Void doInBackground() throws Exception {
				kanjiList.cleanWords();
				List <KanjiInformation> kanjiWords = savingInformation.getKanjiWords();
				progressBar.setMaximum(kanjiWords.size());
				for (int i = 0; i < kanjiWords.size(); i++) {
					kanjiList.addWord(kanjiWords.get(i));
					//TODO create methods add list of words: it would not have update view in it
					// and method addWord should use updateView
					progressBar.setValue(i);
				}

				return null;
			}


			@Override
			public void done() {
				kanjiList.scrollToBottom();
				parent.closeDialog();
				if (savingInformation.hasStateToRestore()){
					getStateManagerForHandlingState(savingInformation.
							getApplicationSaveableState()).restoreState(savingInformation);
				}
			}
		};
		s.execute();
	}

	private ApplicationStateManager getStateManagerForHandlingState (ApplicationSaveableState state){
		for (Map.Entry <ApplicationSaveableState, ApplicationStateManager> entry:
				applicationStateToManagerMap.entrySet()){
			if (entry.getKey().equals(state)){
				return entry.getValue();
			}
		}
		throw new IllegalArgumentException("No state manager found for given state: "+state);
	}

	private void showLoadedRepeatingInformations(List<RepeatingInformation> mapOfRepeats) {
		SwingWorker s = new SwingWorker<Void, Integer>() {
			@Override
			public Void doInBackground() throws Exception {
				kanjiRepeatingDates.cleanWords();
				kanjiRepeatingDates.addWordsList(mapOfRepeats);
				kanjiRepeatingDates.scrollToBottom();
				return null;
			}
		};
		s.execute();
	}

	private void initializeKanjiList() {
		kanjiList = new MyList<>(parent, this, new RowInKanjiInformations(parent),
				Titles.KANJI_LIST, true,
				KanjiInformation.getElementsTypesAndLabels(), ListWordType.KANJI);
		for (int i = 1; i <= 15; i++) {
			kanjiList.addWord(new KanjiInformation("Word no. " + i, i));
		}
		kanjiList.addWord(
				new KanjiInformation("Firstly a trivial correction: the integer ALIGN_JUSTIF"
						+ " should read ALIGN_JUSTIFIED Secondly, I have tried several variations of getting "
						+ "justified text in JTextPane including the solution given above and using a menuitem "
						+ "with alignment action such as: menu.add(new , i, i);", 11));
	}

	private void initializeKanjiRepeatingDates() {
		kanjiRepeatingDates = new MyList<>(parent,this, new RowInRepeatingList(),
				Titles.REPEATING_LIST, false,
				RepeatingInformation.getElementsTypesAndLabels(), ListWordType.REPEATING_DATA);
		kanjiRepeatingDates.addWord(
				new RepeatingInformation("abc", LocalDateTime.of(1993, 11, 13, 13, 25), true, "3 minuty"));
		kanjiRepeatingDates.addWord(
				new RepeatingInformation("abc", LocalDateTime.of(2005, 1, 1, 11, 11), true, "4 minuty"));
		kanjiRepeatingDates.addWord(
				new RepeatingInformation("abc", LocalDateTime.of(2000, 12, 31, 10, 0), true, "5 minut"));
	}

	private File openFile() {
		JFileChooser fileChooser = createFileChooser();
		String directory;

		int chosenOption = fileChooser.showOpenDialog(parent.getContainer());
		if (chosenOption == JFileChooser.CANCEL_OPTION)
			return new File("");
		File file = fileChooser.getSelectedFile();
		return file;
	}

	private File getWorkingDirectory (JFileChooser fileChooser){
		String directory;
		if (System.getProperty("user.dir").contains("dist")) {
			directory = "Powtórki kanji";
		}
		else {
			directory = "Testy do kanji";
		}
		return new File(fileChooser.getCurrentDirectory() + File.separator + directory);
	}

	public boolean showConfirmDialog(String message) {
		return parent.showConfirmDialog(message);
	}

	public void showInsertWordDialog(MyList list) {
		parent.showInsertDialog(list);
	}

	public void showSearchWordDialog(MyList list) {
		parent.showSearchWordDialog(list);
	}

	public void showLearningStartDialog() {
		parent.showLearningStartDialog(kanjiRepeatingDates, kanjiList.getNumberOfWords());
	}

	public MyList<JapaneseWordInformation> getJapaneseWords() {
		return japaneseWords;
	}

	public MyList<RepeatingInformation> getJapaneseWordsRepeatingDates() {
		return japaneseWordsRepeatingDates;
	}

	public MyList<KanjiInformation> getKanjiList() {
		return kanjiList;
	}

	public MyList<RepeatingInformation> getKanjiRepeatingDates() {
		return kanjiRepeatingDates;
	}

	public void saveProject() {
		if (!loadingAndSaving.hasFileToSave()) {
			return;
		}
		parent.changeSaveStatus(SavingStatus.SAVING);
		SavingInformation savingInformation = applicationStateManager.getApplicationState();

		try {
			loadingAndSaving.save(savingInformation);
		}
		catch (IOException e1) {
			parent.showMessageDialog("Exception while saving.");
			e1.printStackTrace();
		}
		parent.changeSaveStatus(SavingStatus.SAVED);
	}

	public void saveList() {
		JFileChooser fileChooser = createFileChooser();

		int option = fileChooser.showSaveDialog(parent.getContainer());
		if (option != JFileChooser.APPROVE_OPTION){
			return;
		}

		File f = fileChooser.getSelectedFile();
		f = new File(f.toString() +".txt");
		CustomFileReader reader = new CustomFileReader();
		try {
			reader.writeToFile(f, kanjiList, kanjiRepeatingDates, getProblematicKanjis());
		}
		catch (IOException e) {
			parent.showMessageDialog(e.getMessage());
			e.printStackTrace();
		}
	}

	public void showSaveDialog() {
		if (!loadingAndSaving.hasFileToSave()) {
			JFileChooser fileChooser = createFileChooser();
			int option = fileChooser.showSaveDialog(this.parent.getContainer());
			if (option == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				loadingAndSaving.setFileToSave(file);
				saveProject();
			}
		}

	}

	public void setProblematicKanjis(Set<Integer> problematicKanjiList) {
		problematicKanjis = problematicKanjiList;
		parent.updateProblematicKanjisAmount();
	}

	public Set<Integer> getProblematicKanjis() {
		return problematicKanjis;
	}

	public void addWordToRepeatingList(RepeatingInformation word) {
		kanjiRepeatingDates.addWord(word);
	}

	public void setRepeatingInformation(RepeatingInformation info) {
		repeatingWordsPanelController.setRepeatingInformation(info);
	}

	public void initiateWordsLists(SetOfRanges ranges, boolean withProblematic) {
		repeatingWordsPanelController.initiateWordsLists(ranges, withProblematic);
	}

	public void startRepeating() {
		parent.showPanel(ApplicationPanels.REPEATING_PANEL);
		isClosingSafe = false;
		repeatingWordsPanelController.startRepeating();
		applicationStateManager = repeatingWordsPanelController;
	}

	public void finishedRepeating(){
		isClosingSafe = true;
		applicationStateManager = this;
	}

	public boolean isClosingSafe (){
		return isClosingSafe;
	}

	@Override public SavingInformation getApplicationState() {
		SavingInformation savingInformation = new SavingInformation(kanjiList.getWords(),
				kanjiRepeatingDates.getWords(), getProblematicKanjis());
		List <String> kanjiKoohiCookiesHeaders = problematicKanjisController.getCookieHeaders();
		if (!kanjiKoohiCookiesHeaders.isEmpty()){
			savingInformation.setKanjiKoohiCookiesHeaders(kanjiKoohiCookiesHeaders);
		}
		return savingInformation;
	}

	@Override
	public void restoreState(SavingInformation savingInformation){
		applicationStateManager.restoreState(savingInformation);
	}

	public void switchStateManager (ApplicationStateManager stateManager){
		this.applicationStateManager = stateManager;
		isClosingSafe = false;
	}

	public void clearStateManager (){
		applicationStateManager = this;
		isClosingSafe = true;
	}

}

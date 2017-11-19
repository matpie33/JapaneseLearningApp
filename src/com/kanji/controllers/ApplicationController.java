package com.kanji.controllers;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import javax.swing.JFileChooser;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import com.kanji.enums.ApplicationSaveableState;
import com.kanji.listElements.KanjiInformation;
import com.kanji.listElements.RepeatingInformation;
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
	private MyList<KanjiInformation> listOfWords;
	private MyList<RepeatingInformation> listOfRepeatingDates;
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
				parent.getKanjiFont(), listOfWords);
		this.repeatingWordsPanelController = new RepeatingWordsController(parent);
		applicationStateToManagerMap.put(ApplicationSaveableState.REPEATING_WORDS,
				repeatingWordsPanelController);
		applicationStateToManagerMap.put(ApplicationSaveableState.REVIEWING_PROBLEMATIC_KANJIS,
				problematicKanjisController);
	}

	//TODO dependencies between classes are weird and should be reconsidered

	public void initializeListsElements() {
		initWordsList();
		initRepeatsList();
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
			listOfWords.cleanWords();
			listOfRepeatingDates.cleanWords();
			for (KanjiInformation kanjiInformation: kanjiInformations){
				listOfWords.addWord(kanjiInformation);
			}
			for (RepeatingInformation repeatingInformation: repeatingInformations){
				listOfRepeatingDates.addWord(repeatingInformation);
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

		listOfWords.cleanWords();
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
				listOfWords.cleanWords();
				List <KanjiInformation> kanjiWords = savingInformation.getKanjiWords();
				progressBar.setMaximum(kanjiWords.size());
				for (int i = 0; i < kanjiWords.size(); i++) {
					listOfWords.addWord(kanjiWords.get(i));
					//TODO create methods add list of words: it would not have update view in it
					// and method addWord should use updateView
					progressBar.setValue(i);
				}

				return null;
			}


			@Override
			public void done() {
				listOfWords.scrollToBottom();
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
				listOfRepeatingDates.cleanWords();
				listOfRepeatingDates.addWordsList(mapOfRepeats);
				listOfRepeatingDates.scrollToBottom();
				return null;
			}
		};
		s.execute();
	}

	private void initWordsList() {
		listOfWords = new MyList<>(parent, parent.getStartingPanel(), this, new RowInKanjiInformations(parent),
				Titles.KANJI_LIST);
		for (int i = 1; i <= 15; i++) {
			listOfWords.addWord(new KanjiInformation("Word no. " + i, i));
		}
		listOfWords.addWord(
				new KanjiInformation("Firstly a trivial correction: the integer ALIGN_JUSTIF"
						+ " should read ALIGN_JUSTIFIED Secondly, I have tried several variations of getting "
						+ "justified text in JTextPane including the solution given above and using a menuitem "
						+ "with alignment action such as: menu.add(new , i, i);", 11));
	}

	private void initRepeatsList() {
		listOfRepeatingDates = new MyList<>(parent, parent.getStartingPanel(),this, new RowInRepeatingList(),
				Titles.REPEATING_LIST);
		listOfRepeatingDates.addWord(
				new RepeatingInformation("abc", LocalDateTime.of(1993, 11, 13, 13, 25), true, "3 minuty"));
		listOfRepeatingDates.addWord(
				new RepeatingInformation("abc", LocalDateTime.of(2005, 1, 1, 11, 11), true, "4 minuty"));
		listOfRepeatingDates.addWord(
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

	public void showInsertWordDialog() {
		parent.showInsertDialog(listOfWords);
	}

	public void showSearchWordDialog() {
		parent.showSearchWordDialog(listOfWords);
	}

	public void showLearningStartDialog() {
		parent.showLearningStartDialog(listOfRepeatingDates, listOfWords.getNumberOfWords());
	}

	public MyList<KanjiInformation> getWordsList() {
		return listOfWords;
	}

	public MyList<RepeatingInformation> getRepeatsList() {
		return listOfRepeatingDates;
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
			reader.writeToFile(f, listOfWords, listOfRepeatingDates, getProblematicKanjis());
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
		listOfRepeatingDates.addWord(word);
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
		return new SavingInformation(listOfWords.getWords(),
				listOfRepeatingDates.getWords(), getProblematicKanjis());
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

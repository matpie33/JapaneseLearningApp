package com.kanji.panelsAndControllers.controllers;

import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.util.*;

import com.kanji.list.listElements.ListElement;
import com.kanji.list.listElements.RepeatingInformation;
import com.kanji.constants.enums.ApplicationPanels;
import com.kanji.constants.strings.Prompts;
import com.kanji.repeating.RepeatingKanjiDisplayer;
import com.kanji.repeating.RepeatingWordDisplayer;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.panels.RepeatingWordsPanel;
import com.kanji.range.Range;
import com.kanji.range.SetOfRanges;
import com.kanji.saving.ApplicationStateManager;
import com.kanji.saving.RepeatingState;
import com.kanji.timer.TimeSpentHandler;
import com.kanji.timer.TimeSpentMonitor;
import com.kanji.constants.enums.RepeatingWordsPanelState;
import com.kanji.saving.SavingInformation;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;

public class RepeatingWordsController implements TimeSpentMonitor, ApplicationStateManager {

	private ApplicationWindow parent;
	private ListElement currentWord;
	private ListElement previousWord;
	private boolean paused;
	private MyList wordsList;
	private RepeatingWordsPanelState repeatingWordsPanelState;
	private TimeSpentHandler timeSpentHandler;
	private RepeatingInformation repeatInfo;
	private RepeatingWordsPanel panel;
	private List <ListElement> wordsLeftToRepeat;
	private RepeatingWordDisplayer wordDisplayer;
	//TODO rename variables having "kanji" in their name

	public RepeatingWordsController(ApplicationWindow parent) {
		this.parent = parent;
		timeSpentHandler = new TimeSpentHandler(this);
		parent.setTimeSpentHandler(timeSpentHandler);
		this.panel = new RepeatingWordsPanel(this);
		repeatingWordsPanelState = RepeatingWordsPanelState.RECOGNIZING_WORD;
		wordsLeftToRepeat = new ArrayList<>();
	}

	public void setWordDisplayer (RepeatingWordDisplayer wordDisplayer){
		this.wordDisplayer = wordDisplayer;
	}

	public RepeatingWordsPanel getRepeatingWordsPanel (){
		return panel;
	}


	public String createRemainingKanjisPrompt() {
		return Prompts.REMAINING_KANJI + " " + this.wordsLeftToRepeat.size() + " "
				+ Prompts.KANJI;
	}

	private void addSelectedWordsToList(SetOfRanges rangesOfRowNumbers) {
		for (Range range : rangesOfRowNumbers.getRangesAsList()) {
			if (!range.isEmpty()) {
				for (int i = range.getRangeStart(); i <= range.getRangeEnd(); i++) {
					wordsLeftToRepeat.add(getKanjiInformationByRowNumber(i));
				}
			}
		}
	}

	private ListElement getKanjiInformationByRowNumber(int rowNumber1Based){
		return wordsList.getWordInRow(rowNumber1Based);
	}

	private <Word extends ListElement> void addProblematicKanjisToList(
			Set <Word> words) {
		for (ListElement word: words) {
			if (!this.wordsLeftToRepeat.contains(word)) {
				this.wordsLeftToRepeat.add(word);
			}
		}
	}

	void startRepeating() {
		previousWord = wordsList.createWord();
		currentWord = wordsList.createWord();
		panel.addWordInformationPanelCards(wordDisplayer.getRecognizingWordPanel(),
				wordDisplayer.getFullInformationPanel());
		timeSpentHandler.startTimer();
		removePreviousWordAndPickNextOrFinishRepeating();
		showNextWord();
	}

	void resumeUnfinishedRepeating (RepeatingState <ListElement> kanjiRepeatingState){
		for (ListElement keyword: kanjiRepeatingState.getCurrentlyRepeatedWords()){
			wordsLeftToRepeat.add(keyword);
		}
		wordDisplayer.addProblematicWords(kanjiRepeatingState
				.getCurrentProblematicWords());
		repeatInfo = kanjiRepeatingState.getRepeatingInformation();
		repeatInfo.setRepeatingDate(LocalDateTime.now());
		timeSpentHandler.resumeTime(kanjiRepeatingState.getTimeSpent());
	}

	private void removePreviousWordAndPickNextOrFinishRepeating() {
		wordsLeftToRepeat.remove(currentWord);
		panel.updateRemainingKanjisText(createRemainingKanjisPrompt());
		previousWord = currentWord;

		if (!this.wordsLeftToRepeat.isEmpty()) {
			pickRandomWord();
		}
		else {
			displayFinishMessageAndStopTimer();
		}
	}

	private void pickRandomWord() {
		Random randomizer = new Random();
		int index = randomizer.nextInt(wordsLeftToRepeat.size());
		currentWord = wordsLeftToRepeat.get(index);
		showWord(currentWord);
	}

	private void showWord(ListElement word) {
		panel.showWord(wordDisplayer.getWordHint(word));
	}

	private void displayFinishMessageAndStopTimer() {

		timeSpentHandler.stopTimer();
		repeatInfo.setWasRepeated(true);
		repeatInfo.setTimeSpentOnRepeating(timeSpentHandler.getTimePassed());

		parent.getApplicationController().addWordToRepeatingList(repeatInfo);
		setProblematicWordsToController();
		parent.showPanel(ApplicationPanels.STARTING_PANEL);

		parent.scrollToBottom();

		parent.showMessageDialog(createFinishMessage());
		if (wordDisplayer.hasProblematicWords()){
			parent.getApplicationController().saveProject();
			parent.showProblematicKanjiDialog(wordDisplayer.getProblematicWords());
		}
		else{
			parent.getApplicationController().finishedRepeating();
		}

		parent.getApplicationController().saveProject();
	}


	private void setProblematicWordsToController (){
		if (wordDisplayer.getClass().equals(RepeatingKanjiDisplayer.class)){
			parent.getApplicationController().setProblematicKanjis(
					wordDisplayer.getProblematicWords());
		}
	}

	private String createFinishMessage() {
		String message = Prompts.REPEATING_DONE;
		message += Prompts.REPEATING_TIME;
		message += timeSpentHandler.getTimePassed();
		return message;
	}

	public void reset() {
		timeSpentHandler.reset();
		wordDisplayer.clearRepeatingData();
		this.wordsLeftToRepeat = new ArrayList<>();
		wordsList = parent.getApplicationController().getActiveWordsList();
		wordDisplayer.addProblematicWords(
				parent.getApplicationController().getProblematicKanjis());
	}

	void setRepeatingInformation(RepeatingInformation info) {
		repeatInfo = info;
	}

	public RepeatingWordsPanel getPanel() {
		return panel;
	}

	public void updateTime(String timePassed) {
		panel.updateTime(timePassed);
	}

	private void goToPreviousWord() {
		showWord(previousWord);
		currentWord = previousWord;
		removeWordFromCurrentProblematics();
		panel.showCurrentKanjiAndSetButtons();
	}

	private void removeWordFromCurrentProblematics() {
		wordDisplayer.removeWordFromProblematic(currentWord);
	}

	private void pauseAndResume() {
		stopLearning();
		parent.showMessageDialog(Prompts.PAUSE_ENABLED);
		resumeLearning();
	}

	private void stopLearning(){
		paused = true;
		timeSpentHandler.stopTimer();
	}

	private void resumeLearning (){
		paused = false;
		timeSpentHandler.startTimer();
	}

	private void markWordAsRecognizedAndGoToNext() {
		if (paused) {
			return;
		}
		removeCurrentWordIfItsProblematic();
		removePreviousWordAndPickNextOrFinishRepeating();
		showNextWord();
	}

	private void showNextWord(){
		panel.setElementsToRecognizingState();
		repeatingWordsPanelState = RepeatingWordsPanelState.RECOGNIZING_WORD;
	}

	private void removeCurrentWordIfItsProblematic() {
		wordDisplayer.removeWordFromProblematic(currentWord);
	}

	private void markWordAsNotRecognizedAndGoToNext() {
		if (paused) {
			return;
		}
		addCurrentWordToProblematicList();
		removePreviousWordAndPickNextOrFinishRepeating();
		showNextWord();
	}

	private void addCurrentWordToProblematicList() {
		wordDisplayer.markWordAsProblematic(currentWord);
	}

	private void pressedButtonReturn() {
		boolean accepted = parent.showConfirmDialog(Prompts.EXIT_LEARNING);
		if (!accepted){
			return;
		}
		parent.showPanel(ApplicationPanels.STARTING_PANEL);
		parent.getApplicationController().finishedRepeating();
		timeSpentHandler.stopTimer();
	}

	public boolean previousWordExists() {
		return previousWord != null;
	}

	<Word extends ListElement> void initiateWordsLists(SetOfRanges ranges,
			Set <Word> words, boolean withProblematic) {
		reset();
		if (!ranges.isEmpty()) {
			addSelectedWordsToList(ranges);
		}
		if (withProblematic) {
			addProblematicKanjisToList(words);
		}
	}

	public AbstractAction createActionGoToPreviousWord (){
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				goToPreviousWord();
				panel.toggleGoToPreviousWordButton();
				repeatingWordsPanelState = RepeatingWordsPanelState.WORD_INFORMATION_SHOWING;
			}
		};
	}

	public AbstractAction createActionPause(){
		return new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				pauseAndResume();
			}
		};
	}

	public AbstractAction createShowFullInformationOrMarkWordAsRecognizedAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (repeatingWordsPanelState == RepeatingWordsPanelState.WORD_INFORMATION_SHOWING){
					markWordAsRecognizedAndGoToNext();
					showRecognizingPanel();
				}
				else{
					wordDisplayer.showWordFullInformation(currentWord);
					panel.showCurrentKanjiAndSetButtons();
					panel.showCardWithFullInformationAboutWord();
					repeatingWordsPanelState = RepeatingWordsPanelState.WORD_INFORMATION_SHOWING;
				}
			}
		};
	}

	public AbstractAction createNotRecognizedWordAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
			markWordAsNotRecognizedAndGoToNext();
			showRecognizingPanel();
			}
		};
	}

	private void showRecognizingPanel (){
		panel.showCardForRecognizingWord();
		wordDisplayer.showRecognizingWordPanel();
	}

	public AbstractAction createActionExit() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				pressedButtonReturn();
			}
		};
	}

	@Override public SavingInformation getApplicationState() {
		SavingInformation savingInformation = parent.getApplicationController()
				.getApplicationState();
		RepeatingState repeatingState = wordDisplayer.getRepeatingState(
				timeSpentHandler.getTimeForSerialization(), repeatInfo,
				convertWordsListToSet());
		savingInformation.setRepeatingState(repeatingState);
		return savingInformation;
	}

	private Set <ListElement> convertWordsListToSet (){
		Set <ListElement> wordsSet = new HashSet<>();
		for (ListElement word:wordsLeftToRepeat){
			wordsSet.add(word);
		}
		return wordsSet;
	}



	@Override
	public void restoreState(SavingInformation savingInformation){
		reset();
		resumeUnfinishedRepeating(
				savingInformation.getRepeatingState());
		parent.displayMessageAboutUnfinishedRepeating();
		parent.getApplicationController().startRepeating();
	}

}

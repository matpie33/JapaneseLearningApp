package com.kanji.panelsAndControllers.controllers;

import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.util.*;

import com.kanji.list.listElements.KanjiInformation;
import com.kanji.list.listElements.RepeatingInformation;
import com.kanji.constants.enums.ApplicationPanels;
import com.kanji.constants.strings.Prompts;
import com.kanji.utilities.KanjiCharactersReader;
import com.kanji.list.listElementPropertyManagers.KanjiIdChecker;
import com.kanji.list.listElementPropertyManagers.KanjiKeywordChecker;
import com.kanji.constants.enums.SearchingDirection;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.panels.RepeatingWordsPanel;
import com.kanji.range.Range;
import com.kanji.range.SetOfRanges;
import com.kanji.saving.ApplicationStateManager;
import com.kanji.timer.TimeSpentHandler;
import com.kanji.timer.TimeSpentMonitor;
import com.kanji.saving.KanjiRepeatingState;
import com.kanji.constants.enums.RepeatingWordsPanelState;
import com.kanji.saving.SavingInformation;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;

public class RepeatingWordsController implements TimeSpentMonitor, ApplicationStateManager {

	//TODO merge issue fix with master branch
	private KanjiCharactersReader kanjiCharactersReader;
	private ApplicationWindow parent;
	private Set<Integer> problematicKanjisIds;
	private Set<Integer> currentProblematicKanjisIds;
	private KanjiInformation currentWord = new KanjiInformation("", 0);
	private KanjiInformation previousWord = new KanjiInformation("", 0);
	private boolean paused;
	private MyList<KanjiInformation> kanjiList;
	private RepeatingWordsPanelState repeatingWordsPanelState;
	private TimeSpentHandler timeSpentHandler;
	private RepeatingInformation repeatInfo;
	private RepeatingWordsPanel panel;
	private List <KanjiInformation> kanjisLeftToRepeat;

	public RepeatingWordsController(ApplicationWindow parent) {
		kanjiCharactersReader = KanjiCharactersReader.getInstance();
		kanjiCharactersReader.loadKanjisIfNeeded();
		currentProblematicKanjisIds = new HashSet<>();
		this.parent = parent;
		timeSpentHandler = new TimeSpentHandler(this);
		parent.setTimeSpentHandler(timeSpentHandler);
		this.panel = new RepeatingWordsPanel(parent, this);
		repeatingWordsPanelState = RepeatingWordsPanelState.WORD_NOT_SHOWING;
		kanjisLeftToRepeat = new ArrayList<>();
	}

	public RepeatingWordsPanel getRepeatingWordsPanel (){
		return panel;
	}

	private String getCurrentKanji() {
		return kanjiCharactersReader.getKanjiById(currentWord.getKanjiID());
	}

	public String createRemainingKanjisPrompt() {
		return Prompts.REMAINING_KANJI + " " + this.kanjisLeftToRepeat.size() + " "
				+ Prompts.KANJI;
	}

	private void addSelectedWordsToList(SetOfRanges rangesOfRowNumbers) {
		for (Range range : rangesOfRowNumbers.getRangesAsList()) {
			if (!range.isEmpty()) {
				for (int i = range.getRangeStart(); i <= range.getRangeEnd(); i++) {
					kanjisLeftToRepeat.add(getKanjiInformationByRowNumber(i));
				}
			}
		}
	}

	private KanjiInformation getKanjiInformationByRowNumber(int rowNumber1Based){
		return kanjiList.getWordInRow(rowNumber1Based);
	}

	private KanjiInformation getKanjiInformationById(int id){
		return kanjiList.findRowBasedOnPropertyStartingFromBeginningOfList(new KanjiIdChecker(),
				id, SearchingDirection.FORWARD);
	}

	private void addProblematicKanjisToList() {
		for (int i : problematicKanjisIds) {
			if (!this.kanjisLeftToRepeat.contains(new KanjiInformation("", i))) {
				this.kanjisLeftToRepeat.add(getKanjiInformationById(i));
			}
		}
	}

	void startRepeating() {
		previousWord = new KanjiInformation("", 0);
		timeSpentHandler.startTimer();
		removePreviousWordAndPickNext();
		goToNextWord();
	}

	private KanjiInformation getKanjiInformationByKeyword (String keyword){
		return kanjiList.findRowBasedOnPropertyStartingFromHighlightedWord
				(new KanjiKeywordChecker(), keyword, SearchingDirection.FORWARD);
	}

	void resumeUnfinishedRepeating (KanjiRepeatingState kanjiRepeatingState){
		for (String keyword: kanjiRepeatingState.getCurrentlyRepeatedWords()){
			kanjisLeftToRepeat.add(getKanjiInformationByKeyword(keyword));
		}
		currentProblematicKanjisIds = kanjiRepeatingState.getCurrentProblematicKanjis();
		repeatInfo = kanjiRepeatingState.getRepeatingInformation();
		repeatInfo.setRepeatingDate(LocalDateTime.now());
		timeSpentHandler.resumeTime(kanjiRepeatingState.getTimeRepresentation());
	}

	private void removePreviousWordAndPickNext() {
		kanjisLeftToRepeat.remove(currentWord);
		previousWord = currentWord;

		if (!this.kanjisLeftToRepeat.isEmpty()) {
			pickRandomWord();
			panel.clearKanji();
		}
		else {
			displayFinishMessageAndStopTimer();
		}
	}

	private void pickRandomWord() {
		Random randomizer = new Random();
		int index = randomizer.nextInt(kanjisLeftToRepeat.size());
		currentWord = kanjisLeftToRepeat.get(index);
		showWord(currentWord.getKanjiKeyword());
	}

	private void showWord(String word) {
		panel.showWord(word);
	}

	private void displayFinishMessageAndStopTimer() {

		timeSpentHandler.stopTimer();
		repeatInfo.setWasRepeated(true);
		repeatInfo.setTimeSpentOnRepeating(timeSpentHandler.getTimePassed());
		problematicKanjisIds.addAll(currentProblematicKanjisIds);

		parent.getApplicationController().addWordToRepeatingList(repeatInfo);
		parent.getApplicationController().setProblematicKanjis(problematicKanjisIds);
		parent.showPanel(ApplicationPanels.STARTING_PANEL);

		parent.scrollToBottom();

		parent.showMessageDialog(createFinishMessage());
		if (currentProblematicKanjisIds.size() > 0){
			parent.getApplicationController().saveProject();
			parent.showProblematicKanjiDialog(currentProblematicKanjisIds);
		}
		else{
			parent.getApplicationController().finishedRepeating();
		}

		parent.getApplicationController().saveProject();
	}

	private String createFinishMessage() {
		String message = Prompts.REPEATING_DONE;
		message += Prompts.REPEATING_TIME;
		message += timeSpentHandler.getTimePassed();
		return message;
	}

	public void reset() {
		timeSpentHandler.reset();
		problematicKanjisIds = new HashSet<>(); //TODO this line can be removed
		currentProblematicKanjisIds.clear();
		this.kanjisLeftToRepeat = new ArrayList<>();
		currentWord = new KanjiInformation("", 0);
		kanjiList = parent.getApplicationController().getKanjiList();
		this.problematicKanjisIds = parent.getApplicationController().getProblematicKanjis();
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
		showWord(previousWord.getKanjiKeyword());
		currentWord = previousWord;
		removeWordFromCurrentProblematics();
		panel.showCurrentKanjiAndSetButtons(getCurrentKanji());
	}

	private void removeWordFromCurrentProblematics() {
		int id = currentWord.getKanjiID();
		currentProblematicKanjisIds.remove(id);
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

	private void markWordAsRecognized() {
		if (paused) {
			return;
		}
		removeWordIfItsProblematic();
		removePreviousWordAndPickNext();
		goToNextWord();
	}

	private void goToNextWord (){
		panel.setElementsToLearningState();
		panel.updateRemainingKanjis(createRemainingKanjisPrompt());
		repeatingWordsPanelState = RepeatingWordsPanelState.WORD_NOT_SHOWING;
	}

	private void removeWordIfItsProblematic() {
		problematicKanjisIds.remove(getRowNumberInListForCurrentWord());
	}

	private void markWordAsNotRecognized() {
		if (paused) {
			return;
		}
		addWordToProblematicList();
		removePreviousWordAndPickNext();
		goToNextWord();
	}

	private void addWordToProblematicList() {
		this.currentProblematicKanjisIds.add(getRowNumberInListForCurrentWord());
	}

	private int getRowNumberInListForCurrentWord (){
		int num = currentWord.getKanjiID();
		return num;
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

	 void initiateWordsLists(SetOfRanges ranges, boolean withProblematic) {
		reset();
		if (!ranges.isEmpty()) {
			addSelectedWordsToList(ranges);
		}
		if (withProblematic) {
			addProblematicKanjisToList();
		}
	}

	public AbstractAction createActionGoToPreviousWord (){
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				goToPreviousWord();
				panel.toggleGoToPreviousWordButton();
				repeatingWordsPanelState = RepeatingWordsPanelState.WORD_IS_SHOWING;
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

	public AbstractAction createRecognizedWordAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (repeatingWordsPanelState == RepeatingWordsPanelState.WORD_IS_SHOWING){
					markWordAsRecognized();
				}
				else{
					panel.showCurrentKanjiAndSetButtons(getCurrentKanji());
					repeatingWordsPanelState = RepeatingWordsPanelState.WORD_IS_SHOWING;
				}
			}
		};
	}

	public AbstractAction createNotRecognizedWordAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				markWordAsNotRecognized();
			}
		};
	}

	public AbstractAction createActionExit() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				pressedButtonReturn();
			}
		};
	}

	@Override public SavingInformation getApplicationState() {
		SavingInformation savingInformation = parent.getApplicationController().getApplicationState();
		KanjiRepeatingState kanjiRepeatingState =
				new KanjiRepeatingState(currentProblematicKanjisIds, collectKanjiKeywordsLeftToRepeat(),
						repeatInfo, timeSpentHandler.getTimeForSerialization());
		savingInformation.setKanjiRepeatingState(kanjiRepeatingState);
		return savingInformation;
	}

	private List <String> collectKanjiKeywordsLeftToRepeat (){
		List <String> kanjiKeywords = new ArrayList<>();
		for (KanjiInformation kanjiInformation: kanjisLeftToRepeat){
			kanjiKeywords.add(kanjiInformation.getKanjiKeyword());
		}
		return kanjiKeywords;
	}

	@Override
	public void restoreState(SavingInformation savingInformation){
		reset();
		resumeUnfinishedRepeating(
				savingInformation.getKanjiRepeatingState());
		parent.displayMessageAboutUnfinishedRepeating();
		parent.getApplicationController().startRepeating();
	}

}

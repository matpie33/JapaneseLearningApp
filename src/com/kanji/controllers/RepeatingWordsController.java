package com.kanji.controllers;

import java.awt.event.ActionEvent;
import java.util.*;

import com.kanji.Row.KanjiInformation;
import com.kanji.Row.RepeatingInformation;
import com.kanji.constants.ApplicationPanels;
import com.kanji.constants.Prompts;
import com.kanji.fileReading.KanjiCharactersReader;
import com.kanji.listSearching.KanjiIdChecker;
import com.kanji.listSearching.KanjiKeywordChecker;
import com.kanji.listSearching.SearchOptions;
import com.kanji.listSearching.SearchingDirection;
import com.kanji.myList.MyList;
import com.kanji.panels.RepeatingWordsPanel;
import com.kanji.range.Range;
import com.kanji.range.SetOfRanges;
import com.kanji.timer.TimeSpentHandler;
import com.kanji.timer.TimeSpentMonitor;
import com.kanji.utilities.RepeatingState;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;

public class RepeatingWordsController implements TimeSpentMonitor {
	private List<String> currentlyRepeatedWords;
	private KanjiCharactersReader kanjiCharactersReader;
	private ApplicationWindow parent;
	private Set<Integer> problematicKanjis;
	private Set<Integer> currentProblematicKanjis;
	private String currentWord;
	private String previousWord = "";
	private boolean paused;
	private MyList<KanjiInformation> kanjiList;
	private RepeatingState repeatingState;
	private int maxCharactersInRow = 15;
	private TimeSpentHandler timeSpentHandler;
	private RepeatingInformation repeatInfo;
	private RepeatingWordsPanel panel;

	public RepeatingWordsController(ApplicationWindow parent, RepeatingWordsPanel panel) {

		kanjiCharactersReader = KanjiCharactersReader.getInstance();
		kanjiCharactersReader.loadKanjisIfNeeded();
		currentProblematicKanjis = new HashSet<>();
		this.currentlyRepeatedWords = new ArrayList<>();
		this.parent = parent;
		timeSpentHandler = new TimeSpentHandler(this);
		this.panel = panel;
		repeatingState = RepeatingState.WORD_NOT_SHOWING;
	}

	private String getCurrentKanji() {
		return this.kanjiCharactersReader.getKanjiById(getCurrentWordId());
	}

	private int getCurrentWordId() {
		return kanjiList.findRowBasedOnPropertyStartingFromBeginningOfList(
				new KanjiKeywordChecker(SearchOptions.BY_FULL_EXPRESSION), currentWord,
				SearchingDirection.FORWARD).getKanjiID();
	}

	public String createRemainingKanjisPrompt() {
		return Prompts.REMAINING_KANJI + " " + this.currentlyRepeatedWords.size() + " "
				+ Prompts.KANJI;
	}

	private void addSelectedWordsToList(SetOfRanges ranges) {
		for (Range range : ranges.getRangesAsList()) {
			if (!range.isEmpty()) {
				for (int i = range.getRangeStart(); i <= range.getRangeEnd(); i++) {
					currentlyRepeatedWords.add(kanjiList.getWordInRow(i - 1).getKanjiKeyword());
				}
			}
		}
	}

	private void addProblematicKanjisToList() {
		for (int i : problematicKanjis) {
			String word = kanjiList.findRowBasedOnPropertyStartingFromHighlightedWord(
					new KanjiIdChecker(), i, SearchingDirection.FORWARD).getKanjiKeyword();
			if (!this.currentlyRepeatedWords.contains(word)) {
				this.currentlyRepeatedWords.add(word);
			}
		}
	}

	void startRepeating() {
		previousWord = "";
		timeSpentHandler.startTimer();
		removePreviousWordAndPickNext();
		goToNextWord();
	}

	private void removePreviousWordAndPickNext() {
		this.currentlyRepeatedWords.remove(currentWord);
		previousWord = currentWord;

		if (!this.currentlyRepeatedWords.isEmpty()) {
			pickRandomWord();
			panel.clearKanji();
		}
		else {
			displayFinishMessageAndStopTimer();
		}
	}

	private void pickRandomWord() {
		Random randomizer = new Random();
		int index = randomizer.nextInt(currentlyRepeatedWords.size());
		this.currentWord = currentlyRepeatedWords.get(index);
		showWord(currentWord);
	}

	private void showWord(String word) {
		panel.showWord(word);
	}

	private void displayFinishMessageAndStopTimer() {

		timeSpentHandler.stopTimer();
		repeatInfo.setWasRepeated(true);
		repeatInfo.setTimeSpentOnRepeating(timeSpentHandler.getTimePassed());
		problematicKanjis.addAll(currentProblematicKanjis);

		parent.getApplicationController().addWordToRepeatingList(repeatInfo);
		parent.getApplicationController().addProblematicKanjis(problematicKanjis);
		parent.showPanel(ApplicationPanels.STARTING_PANEL);

		parent.getApplicationController().saveProject();
		parent.updateProblematicKanjisAmount();
		parent.scrollToBottom();

		parent.showMessageDialog(createFinishMessage());
		if (currentProblematicKanjis.size() > 0)
			parent.showProblematicKanjiDialog(currentProblematicKanjis);
	}

	private String createFinishMessage() {
		String message = Prompts.REPEATING_DONE;
		message += Prompts.REPEATING_TIME;
		message += timeSpentHandler.getTimePassed();
		return message;
	}

	private void reset() {
		timeSpentHandler.reset();
		problematicKanjis = new HashSet<>();
		currentProblematicKanjis.clear();
		this.currentlyRepeatedWords = new ArrayList<>();
		currentWord = "";
		kanjiList = parent.getApplicationController().getWordsList();
		this.problematicKanjis = parent.getApplicationController().getProblematicKanjis();
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
		panel.showCurrentKanjiAndSetButtons(getCurrentKanji());
	}

	private void removeWordFromCurrentProblematics() {
		int id = getCurrentWordId();
		currentProblematicKanjis.remove(id);
	}

	private void pauseLearning() {
		paused = true;
		timeSpentHandler.stopTimer();
		parent.showMessageDialog(Prompts.PAUSE_ENABLED);
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
		repeatingState = RepeatingState.WORD_NOT_SHOWING;
	}

	private void removeWordIfItsProblematic() {
		int id = getCurrentWordId();
		problematicKanjis.remove(id);
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
		int num = getCurrentWordId();
		this.currentProblematicKanjis.add(num);
	}

	private void pressedButtonReturn() {
		boolean accepted = parent.showConfirmDialog(Prompts.EXIT_LEARNING);
		if (!accepted){
			return;
		}
		parent.showPanel(ApplicationPanels.STARTING_PANEL);
		timeSpentHandler.stopTimer();
	}

	public boolean previousWordExists() {
		return !previousWord.isEmpty();
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
				repeatingState = RepeatingState.WORD_IS_SHOWING;
			}
		};
	}

	public AbstractAction createButtonPause (){
		return new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				pauseLearning();
			}
		};
	}

	public AbstractAction createRecognizedWordAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (repeatingState == RepeatingState.WORD_IS_SHOWING){
					markWordAsRecognized();
				}
				else{
					panel.showCurrentKanjiAndSetButtons(getCurrentKanji());
					repeatingState = RepeatingState.WORD_IS_SHOWING;
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

}

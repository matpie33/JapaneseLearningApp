package com.kanji.controllers;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.guimaker.enums.TextAlignment;
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
import com.kanji.windows.ApplicationWindow;

public class RepeatingWordsController implements TimeSpentMonitor {
	private Set<String> currentlyRepeatedWords;
	private KanjiCharactersReader kanjiCharactersReader;

	// TODO currently repeated words are not set - they can be duplicated, IDs
	// are set
	private ApplicationWindow parent;
	private Set<Integer> problematicKanjis;
	private Set<Integer> currentProblematicKanjis;
	private String currentWord;
	private String previousWord = "";
	private boolean paused;
	private MyList<KanjiInformation> kanjiList;

	private int maxCharactersInRow = 15;

	private TimeSpentHandler timeSpentHandler;

	private RepeatingInformation repeatInfo;
	private RepeatingWordsPanel panel;

	public RepeatingWordsController(ApplicationWindow parent, RepeatingWordsPanel panel) {

		kanjiCharactersReader = KanjiCharactersReader.getInstance();
		kanjiCharactersReader.loadKanjisIfNeeded();
		currentProblematicKanjis = new HashSet<>();
		this.currentlyRepeatedWords = new HashSet<>();
		this.parent = parent;
		timeSpentHandler = new TimeSpentHandler(this);
		this.panel = panel;
	}

	public String getCurrentKanji() {
		return this.kanjiCharactersReader.getKanjiById(getCurrentWordId());
	}

	private int getCurrentWordId() {
		return kanjiList.findRowBasedOnPropertyStartingFromBeginningOfList(
				new KanjiKeywordChecker(SearchOptions.BY_FULL_EXPRESSION), currentWord,
				SearchingDirection.FORWARD, parent).getKanjiID();
	}

	public String createRemainingKanjisPrompt() {
		return Prompts.REMAINING_KANJI + " " + this.currentlyRepeatedWords.size() + " "
				+ Prompts.KANJI;
	}

	public void addChosenForRepeatingWordsToList(SetOfRanges ranges) {
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
					new KanjiIdChecker(), i, SearchingDirection.FORWARD, parent).getKanjiKeyword();
			if (!this.currentlyRepeatedWords.contains(word)) {
				this.currentlyRepeatedWords.add(word);
			}
		}
	}

	public void startRepeating() {
		previousWord = "";
		timeSpentHandler.startTimer();
		removePreviousWordAndRandomlyPickNext();
		panel.goToNextWord();
	}

	private void removePreviousWordAndRandomlyPickNext() {
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
		int index = randomizer.nextInt(this.currentlyRepeatedWords.size());
		this.currentWord = getRandomElementFromSetByIndex(index);
		showWord(currentWord);
	}

	private String getRandomElementFromSetByIndex(int index) {
		int i = 0;
		for (String s : currentlyRepeatedWords) {
			if (i == index) {
				return s;
			}
			i++;
		}
		return "";
	}

	private void showWord(String word) {
		if (currentWord.length() > maxCharactersInRow) {
			panel.showWord(word, TextAlignment.JUSTIFIED);
		}
		else {
			panel.showWord(word, TextAlignment.CENTERED);
		}
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
		parent.scrollToBottom();

		parent.showMessageDialog(createFinishMessage());
		if (currentProblematicKanjis.size() > 0)
			parent.showProblematicKanjiDialog(kanjiList, currentProblematicKanjis);
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
		this.currentlyRepeatedWords = new HashSet<>();
		currentWord = "";
		kanjiList = parent.getApplicationController().getWordsList();
		this.problematicKanjis = parent.getApplicationController().getProblematicKanjis();
	}

	public void setRepeatingInformation(RepeatingInformation info) {
		repeatInfo = info;
	}

	public RepeatingWordsPanel getPanel() {
		return panel;
	}

	public void updateTime(String timePassed) {
		panel.updateTime(timePassed);
	}

	public void goToPreviousWord() {
		showWord(previousWord);
		currentWord = previousWord;
		removeWordFromCurrentProblematics();
		panel.removeLastElementFromRow2();
		panel.showCurrentKanjiAndShowAppropriateButtons();
	}

	private void removeWordFromCurrentProblematics() {
		int id = getCurrentWordId();
		currentProblematicKanjis.remove(Integer.valueOf(id));
	}

	public void pressedButtonPause() {
		paused = true;
		timeSpentHandler.stopTimer();
		parent.showMessageDialog(Prompts.PAUSE_ENABLED);
		paused = false;
		timeSpentHandler.startTimer();
	}

	public void presedButtonShowWord() {
		panel.showCurrentKanjiAndShowAppropriateButtons();
	}

	public void pressedRecognizedWordButton() {
		if (paused) {
			return;
		}
		removeWordIfItsProblematic();
		removePreviousWordAndRandomlyPickNext();
		panel.goToNextWord();
	}

	private void removeWordIfItsProblematic() {
		int id = getCurrentWordId();
		problematicKanjis.remove(Integer.valueOf(id));
	}

	public void pressedNotRecognizedWordButton() {
		if (paused) {
			return;
		}
		addToProblematic();
		removePreviousWordAndRandomlyPickNext();
		panel.goToNextWord();
		panel.setButtonsToLearningAndAddThem();
	}

	private void addToProblematic() {
		int num = getCurrentWordId();
		this.currentProblematicKanjis.add(Integer.valueOf(num));
	}

	public void pressedButtonReturn() {
		parent.showPanel(ApplicationPanels.STARTING_PANEL);
		timeSpentHandler.stopTimer();
	}

	public boolean previousWordExists() {
		return !previousWord.isEmpty();
	}

	public void initiateWordsLists(SetOfRanges ranges, boolean withProblematic) {
		reset();
		if (!ranges.isEmpty()) {
			addChosenForRepeatingWordsToList(ranges);
		}
		if (withProblematic) {
			addProblematicKanjisToList();
		}
	}

}

package com.kanji.controllers;

import java.awt.Font;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.kanji.Row.KanjiInformation;
import com.kanji.Row.RepeatingInformation;
import com.kanji.actions.TextAlignment;
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

	// TODO kanji list should not allow adding words with same keyword // or
	// maybe
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
		return kanjiList.findRowNumberBasedOnPropertyStartingFromBeginningOfList(
				new KanjiKeywordChecker(SearchOptions.BY_FULL_EXPRESSION), currentWord,
				SearchingDirection.FORWARD, parent);
	}

	public String createRemainingKanjisPrompt() {
		return Prompts.remainingKanjiPrompt + " " + this.currentlyRepeatedWords.size() + " "
				+ Prompts.kanjiPrompt;
	}

	public void setRepeatingWords(MyList<KanjiInformation> wordsList) {
		this.currentlyRepeatedWords = new HashSet<>();
		kanjiList = wordsList;
	}

	public void setRangesToRepeat(SetOfRanges ranges) {
		for (Range range : ranges.getRangesAsList()) {
			if (!range.isEmpty()) {
				for (int i = range.getRangeStart(); i <= range.getRangeEnd(); i++) {
					currentlyRepeatedWords.add(kanjiList
							.findRowBasedOnPropertyStartingFromHighlightedWord(new KanjiIdChecker(),
									i, SearchingDirection.FORWARD, kanjiList.getParent())
							.getKanjiKeyword());

				}
			}
		}
	}

	public void setProblematicKanjis(Set<Integer> problematicKanjis) {
		this.problematicKanjis = problematicKanjis;

	}

	public void addProblematicKanjisToList() {
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

		parent.save();
		parent.scrollToBottom();

		parent.showMessageDialog(createFinishMessage());
		if (currentProblematicKanjis.size() > 0)
			parent.showProblematicKanjiDialog(kanjiList, currentProblematicKanjis);
	}

	private String createFinishMessage() {
		String message = Prompts.repeatingIsDonePrompt;
		message += Prompts.repeatingTimePrompt;
		message += timeSpentHandler.getTimePassed();
		return message;
	}

	public void reset() {
		timeSpentHandler.reset();
		problematicKanjis = new HashSet<>();
		currentProblematicKanjis.clear();
		currentWord = "";
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
		parent.showMessageDialog(Prompts.pauseIsEnabled);
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
		int num = getCurrentWordId() + 1; // TODO remove this + 1
		this.currentProblematicKanjis.add(Integer.valueOf(num));
	}

	public void pressedButtonReturn() {
		parent.showPanel(ApplicationPanels.STARTING_PANEL);
		timeSpentHandler.stopTimer();
	}

	public Font getKanjiFont() {
		return this.kanjiCharactersReader.getFont();
	}

	public boolean previousWordExists() {
		return !previousWord.isEmpty();
	}

	public void setWordsRangeToRepeat(SetOfRanges ranges, boolean withProblematic) {
		setRepeatingWords(parent.getApplicationController().getWordsList());
		// TODO if set of ranges is empty, we should not call set ranges to
		// repeat all, so probably
		// split this method
		setRangesToRepeat(ranges);
		reset();
		setProblematicKanjis(parent.getApplicationController().getProblematicKanjis());
		if (withProblematic) {
			addProblematicKanjisToList();
		}

		startRepeating();
	}

}

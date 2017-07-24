package com.kanji.panels;

import java.awt.Font;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.kanji.Row.KanjiWords;
import com.kanji.Row.RepeatingInformation;
import com.kanji.actions.TextAlignment;
import com.kanji.constants.Prompts;
import com.kanji.fileReading.ExcelReader;
import com.kanji.myList.MyList;
import com.kanji.range.Range;
import com.kanji.range.SetOfRanges;
import com.kanji.timer.TimeSpentHandler;
import com.kanji.timer.TimeSpentMonitor;
import com.kanji.windows.ApplicationWindow;

public class RepeatingWordsController implements TimeSpentMonitor {
	private static final long serialVersionUID = 5557984078176822840L;
	private MyList words;
	private List<String> wordsToRepeat;
	private ExcelReader excel;
	private ApplicationWindow parent;
	private Set<Integer> problematicKanjis;
	private Set<Integer> currentProblematicKanjis;
	private String currentWord;
	private String previousWord = "";
	private boolean paused;

	private int maxCharactersInRow = 15;

	private TimeSpentHandler timeSpentHandler;

	private RepeatingInformation repeatInfo;
	private RepeatingWordsPanel panel;

	public RepeatingWordsController(ApplicationWindow parent) {

		excel = new ExcelReader();
		excel.load();
		currentProblematicKanjis = new HashSet<>();
		this.wordsToRepeat = new LinkedList();
		this.parent = parent;
		timeSpentHandler = new TimeSpentHandler(this);
		panel = new RepeatingWordsPanel(this);
	}
	// TODO in main panel, replace int GridBagConstraints.NORTH etc. with my own
	// enum, would be easier to use

	// TODO think about whether the create elements method is needed, here we
	// didnt use it though it works as intended

	private void pickRandomWord() {
		Random randomizer = new Random();
		int index = randomizer.nextInt(this.wordsToRepeat.size());
		this.currentWord = this.wordsToRepeat.get(index);
		showWord(currentWord);
	}

	private void showWord(String word) {
		if (currentWord.length() > maxCharactersInRow) {
			panel.showWord(word, TextAlignment.JUSTIFIED);
		}
		else {
			panel.showWord(word, TextAlignment.CENTERED);
		}
	}

	public String getCurrentKanji() {
		return this.excel
				.getKanjiById(((KanjiWords) words.getWords()).getIdOfTheWord(this.currentWord));
	}

	public void pressedButtonPause() {
		paused = true;
		timeSpentHandler.stopTimer();
		parent.showMsgDialog(Prompts.pauseIsEnabled);
		paused = false;
		timeSpentHandler.startTimer();
	}

	public void presedButtonShowWord() {
		panel.showCurrentKanji();
	}

	private void removeWordFromCurrentProblematics() {
		int id = getCurrentWordId();
		currentProblematicKanjis.remove(Integer.valueOf(id));
	}

	private void removeWordIfItsProblematic() {
		int id = getCurrentWordId();
		problematicKanjis.remove(Integer.valueOf(id));
	}

	private int getCurrentWordId() {
		return ((KanjiWords) words.getWords()).getIdOfTheWord(this.currentWord);
	}

	private void addToProblematic() {
		int num = getCurrentWordId();
		this.currentProblematicKanjis.add(Integer.valueOf(num));
	}

	private void removePreviousWordAndRandomlyPickNext() {
		this.wordsToRepeat.remove(currentWord);
		previousWord = currentWord;

		if (!this.wordsToRepeat.isEmpty()) {
			pickRandomWord();
			panel.clearKanji();
		}
		else {
			displayFinishMessageAndStopTimer();
		}
	}

	private void displayFinishMessageAndStopTimer() {

		timeSpentHandler.stopTimer();
		problematicKanjis.addAll(currentProblematicKanjis);

		this.parent.getStartingController().addProblematicKanjis(this.problematicKanjis);
		this.parent.showCardPanel(ApplicationWindow.LIST_PANEL);
		repeatInfo.setWasRepeated(true);
		repeatInfo.setTimeSpentOnRepeating(timeSpentHandler.getTimePassed());
		parent.getStartingController().addToRepeatsList(repeatInfo);
		this.parent.save();
		parent.scrollToBottom();

		String message = Prompts.repeatingIsDonePrompt;
		message += Prompts.repeatingTimePrompt;
		message += timeSpentHandler.getTimePassed();
		this.parent.showMsgDialog(message);
		System.out.println("done");
		if (currentProblematicKanjis.size() > 0)
			parent.showProblematicKanjiDialog((KanjiWords) words.getWords(),
					currentProblematicKanjis);
	}

	public String createRemainingKanjisPrompt() {
		return Prompts.remainingKanjiPrompt + " " + this.wordsToRepeat.size() + " "
				+ Prompts.kanjiPrompt;
	}

	public void setRepeatingWords(MyList wordsList) {
		this.wordsToRepeat = new LinkedList<>();
		this.words = wordsList;
	}

	public void setRangesToRepeat(SetOfRanges ranges) {
		for (Range range : ranges.getRangesAsList()) {
			if (!range.isEmpty()) {
				for (int i = range.getRangeStart(); i <= range.getRangeEnd(); i++) {
					wordsToRepeat.add(words.findWordInRow(i - 1));
				}
			}
		}
	}

	public void setProblematicKanjis(Set<Integer> problematicKanjis) {
		this.problematicKanjis = problematicKanjis;
		System.out.println("start");
		for (int i : problematicKanjis) {
			String word = ((KanjiWords) words.getWords()).getWordForId(i);

			if (!this.wordsToRepeat.contains(word)) { // TODO maybe set instead
														// of list?
				this.wordsToRepeat.add(word);

			}
		}
		System.out.println("done");
	}

	public void startRepeating() {

		previousWord = "";
		timeSpentHandler.startTimer();
		removePreviousWordAndRandomlyPickNext();
		panel.goToNextWord();
		panel.requestFocusForShowWord();
	}

	public void reset() {
		timeSpentHandler.reset();
		this.problematicKanjis = new HashSet();
		currentProblematicKanjis.clear();
		currentWord = "";
	}

	public void setExcelReader(ExcelReader excel) {
		this.excel = excel;
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
	}

	public void pressedRecognizedWordButton() {
		if (paused) {
			return;
		}
		removeWordIfItsProblematic();
		removePreviousWordAndRandomlyPickNext();
		panel.goToNextWord();
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

	public void pressedButtonReturn() {
		RepeatingWordsController.this.parent.showCardPanel(ApplicationWindow.LIST_PANEL);
		timeSpentHandler.stopTimer();
	}

	public Font getKanjiFont() {
		return new Font(this.excel.getFontName(), 1, 80);
	}

	public boolean previousWordExists() {
		return !previousWord.isEmpty();
	}

}

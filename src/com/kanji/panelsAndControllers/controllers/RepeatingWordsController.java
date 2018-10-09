package com.kanji.panelsAndControllers.controllers;

import com.kanji.constants.enums.ApplicationPanels;
import com.kanji.constants.enums.RepeatingWordsPanelState;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.listElements.RepeatingData;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.panelUpdaters.RepeatingWordsPanelUpdater;
import com.kanji.panelsAndControllers.panels.RepeatingWordsPanel;
import com.kanji.range.Range;
import com.kanji.range.SetOfRanges;
import com.kanji.repeating.RepeatingWordDisplayer;
import com.kanji.saving.ApplicationStateManager;
import com.kanji.saving.RepeatingState;
import com.kanji.saving.SavingInformation;
import com.kanji.timer.TimeSpentHandler;
import com.kanji.timer.TimeSpentMonitor;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.util.*;

public class RepeatingWordsController
		implements TimeSpentMonitor, ApplicationStateManager {

	private ApplicationWindow parent;
	private ListElement currentWord;
	private ListElement previousWord;
	private boolean paused;
	private MyList wordsList;
	private RepeatingWordsPanelState repeatingWordsPanelState;
	private TimeSpentHandler timeSpentHandler;
	private RepeatingData repeatInfo;
	private RepeatingWordsPanel panel;
	private List<ListElement> wordsLeftToRepeat;
	private RepeatingWordDisplayer wordDisplayer;
	private ApplicationWindow applicationWindow;
	private RepeatingWordsPanelUpdater panelUpdater;

	public RepeatingWordsController(ApplicationWindow parent) {
		applicationWindow = parent;
		this.parent = parent;
		timeSpentHandler = new TimeSpentHandler(this);
		parent.setTimeSpentHandler(timeSpentHandler);
		this.panel = new RepeatingWordsPanel(this);
		repeatingWordsPanelState = RepeatingWordsPanelState.RECOGNIZING_WORD;
		wordsLeftToRepeat = new ArrayList<>();
		panelUpdater = new RepeatingWordsPanelUpdater(panel);
	}

	public void setWordDisplayer(RepeatingWordDisplayer wordDisplayer) {
		this.wordDisplayer = wordDisplayer;
	}

	public RepeatingWordsPanel getRepeatingWordsPanel() {
		return panel;
	}

	private void addSelectedWordsToList(SetOfRanges rangesOfRowNumbers) {
		for (Range range : rangesOfRowNumbers.getRangesAsList()) {
			if (!range.isEmpty()) {
				for (int i = range.getRangeStart();
					 i <= range.getRangeEnd(); i++) {
					wordsLeftToRepeat.add(getWordElementByRowNumber(i));
				}
			}
		}
	}

	private ListElement getWordElementByRowNumber(int rowNumber1Based) {
		return wordsList.getWordInRow(rowNumber1Based);
	}

	private <Word extends ListElement> void addProblematicWordToList(
			Set<Word> words) {
		for (ListElement word : words) {
			if (!this.wordsLeftToRepeat.contains(word)) {
				this.wordsLeftToRepeat.add(word);
			}
		}
	}

	void startRepeating() {
		wordDisplayer.setAllProblematicWords(
				applicationWindow.getApplicationController()
						.getProblematicWordsBasedOnCurrentTab());
		previousWord = wordsList.createWord();
		currentWord = wordsList.createWord();
		panel.addWordDataPanelCards(wordDisplayer.getRecognizingWordPanel(),
				wordDisplayer.getFullInformationPanel());
		timeSpentHandler.startTimer();
		removePreviousWordAndPickNextOrFinishRepeating();
		showNextWord();
	}

	void resumeUnfinishedRepeating(RepeatingState<ListElement> repeatingState) {
		for (ListElement keyword : repeatingState.getCurrentlyRepeatedWords()) {
			wordsLeftToRepeat.add(keyword);
		}
		repeatInfo = repeatingState.getRepeatingData();
		repeatInfo.setRepeatingDate(LocalDateTime.now());
		timeSpentHandler.resumeTime(repeatingState.getTimeSpent());
		applicationWindow.getStartingPanel()
				.switchToList(repeatingState.getTypeOfWordForRepeating());
	}

	private void removePreviousWordAndPickNextOrFinishRepeating() {
		wordsLeftToRepeat.remove(currentWord);
		updateRemainingWordsText();
		previousWord = currentWord;

		if (!this.wordsLeftToRepeat.isEmpty()) {
			pickRandomWord();
		}
		else {
			displayFinishMessageAndStopTimer();
		}
	}

	public void updateRemainingWordsText(){
		panelUpdater.updateRemainingWordsText(this.wordsLeftToRepeat.size());
	}

	private void pickRandomWord() {
		Random randomizer = new Random();
		int index = randomizer.nextInt(wordsLeftToRepeat.size());
		currentWord = wordsLeftToRepeat.get(index);
		showWord(currentWord);
	}

	private void showWord(ListElement word) {
		panelUpdater.setWordHint(wordDisplayer.getWordHint(word));
	}

	private void displayFinishMessageAndStopTimer() {

		timeSpentHandler.stopTimer();
		repeatInfo.setWasRepeated(true);
		repeatInfo.setTimeSpentOnRepeating(timeSpentHandler.getTimePassed());

		parent.getApplicationController().addWordToRepeatingList(repeatInfo);
		applicationWindow.updateProblematicWordsAmount();

		parent.scrollToBottom();

		parent.showMessageDialog(createFinishMessage());
		if (wordDisplayer.hasProblematicWords()) {
			parent.getApplicationController().saveProject();
			parent.showProblematicWordsDialog(
					wordDisplayer.getProblematicWords());
		}
		else {
			parent.showPanel(ApplicationPanels.STARTING_PANEL);
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
		wordDisplayer.clearRepeatingData();
		this.wordsLeftToRepeat = new ArrayList<>();
		wordsList = parent.getApplicationController().getActiveWordsList();
	}

	void setRepeatingInformation(RepeatingData info) {
		repeatInfo = info;
	}

	public RepeatingWordsPanel getPanel() {
		return panel;
	}

	@Override
	public void updateTime(String timePassed) {
		panelUpdater.updateTime(timePassed);
	}

	private void goToPreviousWord() {
		showWord(previousWord);
		wordDisplayer.showWordFullInformation(previousWord);
		panel.showWordAssessmentPanel();
		currentWord = previousWord;
		removeWordFromCurrentProblematics();
		panelUpdater.setButtonsToWordAssessmentState(previousWordExists());
	}

	private void removeWordFromCurrentProblematics() {
		wordDisplayer.removeWordFromProblematic(currentWord);
	}

	private void pauseAndResume() {
		stopLearning();
		parent.showMessageDialog(Prompts.PAUSE_ENABLED);
		resumeLearning();
	}

	private void stopLearning() {
		paused = true;
		timeSpentHandler.stopTimer();
	}

	private void resumeLearning() {
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

	private void showNextWord() {
		panelUpdater.setButtonsToWordGuessState(previousWordExists());
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
		if (!accepted) {
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
			Set<Word> words, boolean withProblematic) {
		reset();
		if (!ranges.isEmpty()) {
			addSelectedWordsToList(ranges);
		}
		if (withProblematic) {
			addProblematicWordToList(words);
		}
	}

	public AbstractAction createActionGoToPreviousWord() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				goToPreviousWord();
				panelUpdater.toggleGoToPreviousWordButton();
				repeatingWordsPanelState = RepeatingWordsPanelState.WORD_INFORMATION_SHOWING;
			}
		};
	}

	public AbstractAction createActionPause() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				pauseAndResume();
			}
		};
	}

	public AbstractAction createShowFullInformationOrMarkWordAsRecognizedAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (repeatingWordsPanelState
						== RepeatingWordsPanelState.WORD_INFORMATION_SHOWING) {
					markWordAsRecognizedAndGoToNext();
					showRecognizingPanel();
				}
				else {
					wordDisplayer.showWordFullInformation(currentWord);
					panelUpdater
							.setButtonsToWordAssessmentState(previousWordExists());
					panel.showWordAssessmentPanel();
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

	private void showRecognizingPanel() {
		panel.showWordGuessingPanel();
		wordDisplayer.showRecognizingWordPanel();
	}

	public AbstractAction createActionExit() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				pressedButtonReturn();
			}
		};
	}

	@Override
	public SavingInformation getApplicationState() {
		SavingInformation savingInformation = parent.getApplicationController()
				.getApplicationState();
		RepeatingState repeatingState = wordDisplayer
				.getRepeatingState(timeSpentHandler.getTimeForSerialization(),
						repeatInfo, convertWordsListToSet());
		savingInformation.setRepeatingState(repeatingState);
		return savingInformation;
	}

	private Set<ListElement> convertWordsListToSet() {
		Set<ListElement> wordsSet = new HashSet<>();
		for (ListElement word : wordsLeftToRepeat) {
			wordsSet.add(word);
		}
		return wordsSet;
	}

	@Override
	public void restoreState(SavingInformation savingInformation) {
		setWordDisplayer(applicationWindow.getApplicationController()
				.getWordDisplayerForWordType(
						savingInformation.getRepeatingState()
								.getTypeOfWordForRepeating()));

		reset();
		resumeUnfinishedRepeating(savingInformation.getRepeatingState());
		parent.displayMessageAboutUnfinishedRepeating();
		parent.getApplicationController().startRepeating();
	}

	public void setButtonsToWordGuessingState() {
		panelUpdater.setButtonsToWordGuessState(previousWordExists());
	}
}

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

	private ApplicationWindow applicationWindow;
	private ListElement currentWord;
	private ListElement previousWord;
	private MyList wordsList;
	private RepeatingWordsPanelState repeatingWordsPanelState;
	private TimeSpentHandler timeSpentHandler;
	private RepeatingData repeatingData;
	private RepeatingWordsPanel panel;
	private List<ListElement> wordsLeftToRepeat;
	private RepeatingWordDisplayer wordDisplayer;
	private RepeatingWordsPanelUpdater panelUpdater;

	public RepeatingWordsController(ApplicationWindow applicationWindow) {
		this.applicationWindow = applicationWindow;
		timeSpentHandler = new TimeSpentHandler(this);
		applicationWindow.setTimeSpentHandler(timeSpentHandler);
		this.panel = new RepeatingWordsPanel(this);
		repeatingWordsPanelState = RepeatingWordsPanelState.WORD_GUESSING;
		wordsLeftToRepeat = new ArrayList<>();
		panelUpdater = new RepeatingWordsPanelUpdater(panel);
	}

	public void setWordDisplayer(RepeatingWordDisplayer wordDisplayer) {
		this.wordDisplayer = wordDisplayer;
	}

	public RepeatingWordsPanel getRepeatingWordsPanel() {
		return panel;
	}

	private void collectSelectedWordsToList(SetOfRanges rangesOfRowNumbers) {
		for (Range range : rangesOfRowNumbers.getRangesAsList()) {
			if (!range.isEmpty()) {
				for (int i = range.getRangeStart();
					 i <= range.getRangeEnd(); i++) {
					wordsLeftToRepeat.add(wordsList.getWordInRow(i));
				}
			}
		}
	}

	private <Word extends ListElement> void addProblematicWordsToList(
			Set<Word> problematicWords) {
		for (ListElement word : problematicWords) {
			if (!wordsLeftToRepeat.contains(word)) {
				wordsLeftToRepeat.add(word);
			}
		}
	}

	public void startRepeating() {
		wordDisplayer.setListOfAllProblematicWords(
				applicationWindow.getApplicationController()
						.getProblematicWordsBasedOnCurrentTab());
		panel.addWordDataPanelCards(wordDisplayer.getWordGuessingPanel(),
				wordDisplayer.getWordAssessmentPanel());
		timeSpentHandler.startTimer();
		pickNextWordOrFinishRepeating();
		setStateToWordGuessing();
	}

	private void resumeUnfinishedRepeating(
			RepeatingState<ListElement> repeatingState) {
		wordsLeftToRepeat.addAll(repeatingState.getCurrentlyRepeatedWords());
		repeatingData = repeatingState.getRepeatingData();
		repeatingData.setRepeatingDate(LocalDateTime.now());
		timeSpentHandler.resumeTime(repeatingState.getTimeSpent());

	}

	private void pickNextWordOrFinishRepeating() {
		wordsLeftToRepeat.remove(currentWord);
		updateRemainingWordsText();
		if (!this.wordsLeftToRepeat.isEmpty()) {
			pickRandomWord();
		}
		else {
			finishRepeating();
		}
	}

	public void updateRemainingWordsText() {
		panelUpdater.updateRemainingWordsText(this.wordsLeftToRepeat.size());
	}

	private void pickRandomWord() {
		previousWord = currentWord;
		Random randomizer = new Random();
		int indexOfNextWord = randomizer.nextInt(wordsLeftToRepeat.size());
		currentWord = wordsLeftToRepeat.get(indexOfNextWord);
		showWordHint(currentWord);
	}

	private void showWordHint(ListElement word) {
		panelUpdater.setWordHint(wordDisplayer.getWordHint(word));
	}

	private void finishRepeating() {
		timeSpentHandler.stopTimer();
		updateApplicationData();
		applicationWindow.showMessageDialog(createFinishMessage());
		closeRepeatingPanelAndOpenProperOne();
		applicationWindow.getApplicationController().saveProject();
	}

	private void updateApplicationData() {
		repeatingData.setWasRepeated(true);
		repeatingData.setTimeSpentOnRepeating(timeSpentHandler.getTimePassed());
		applicationWindow.getApplicationController().addWordToRepeatingList(repeatingData);
		applicationWindow.updateProblematicWordsAmount();
		applicationWindow.scrollRepeatingListToBottom();
	}

	private void closeRepeatingPanelAndOpenProperOne() {
		if (wordDisplayer.hasProblematicWords()) {
			applicationWindow.showProblematicWordsDialog(
					wordDisplayer.getProblematicWords());
		}
		else {
			applicationWindow.showPanel(ApplicationPanels.STARTING_PANEL);
			applicationWindow.getApplicationController().finishedRepeating();
		}
	}

	private String createFinishMessage() {
		return String.format(Prompts.REPEATING_DONE + Prompts.REPEATING_TIME,
				timeSpentHandler.getTimePassed());
	}

	private void reset() {
		timeSpentHandler.reset();
		wordDisplayer.clearRepeatingData();
		this.wordsLeftToRepeat = new ArrayList<>();
		wordsList = applicationWindow.getApplicationController().getActiveWordsList();
	}

	public void setRepeatingData(RepeatingData repeatingData) {
		this.repeatingData = repeatingData;
	}

	public RepeatingWordsPanel getPanel() {
		return panel;
	}

	@Override
	public void updateTime(String timePassed) {
		panelUpdater.updateTime(timePassed);
	}

	private void goToPreviousWord() {
		showWordHint(previousWord);
		showWordAssessmentPanel(previousWord);
		currentWord = previousWord;
		wordDisplayer.removeWordFromProblematic(currentWord);
		panelUpdater.toggleGoToPreviousWordButton();
	}

	private void showWordAssessmentPanel(ListElement word) {
		wordDisplayer.showWordAssessmentPanel(word);
		panel.showWordAssessmentPanel();
		panelUpdater.setButtonsToWordAssessmentState(previousWordExists());
		repeatingWordsPanelState = RepeatingWordsPanelState.WORD_ASSESSMENT;
	}

	private void pauseAndResumeWhenDialogIsClosed() {
		timeSpentHandler.stopTimer();
		applicationWindow.showMessageDialog(Prompts.PAUSE_ENABLED);
		timeSpentHandler.startTimer();
	}

	private void markWordAsRecognizedAndGoToNext() {
		wordDisplayer.removeWordFromProblematic(currentWord);
		pickNextWordOrFinishRepeating();
		setStateToWordGuessing();
	}

	private void setStateToWordGuessing() {
		panelUpdater.setButtonsToWordGuessState(previousWordExists());
		repeatingWordsPanelState = RepeatingWordsPanelState.WORD_GUESSING;
	}

	private void markWordAsNotRecognizedAndGoToNext() {
		wordDisplayer.markWordAsProblematic(currentWord);
		pickNextWordOrFinishRepeating();
		setStateToWordGuessing();
	}

	private void pressedExitButton() {
		boolean accepted = applicationWindow.showConfirmDialog(Prompts.EXIT_LEARNING);
		if (!accepted) {
			return;
		}
		applicationWindow.showPanel(ApplicationPanels.STARTING_PANEL);
		applicationWindow.getApplicationController().finishedRepeating();
		timeSpentHandler.stopTimer();
	}

	private boolean previousWordExists() {
		return previousWord != null;
	}

	public <Word extends ListElement> void resetAndInitializeWordsLists(
			SetOfRanges ranges, Set<Word> problematicWords,
			boolean withProblematic) {
		reset();
		if (!ranges.isEmpty()) {
			collectSelectedWordsToList(ranges);
		}
		if (withProblematic) {
			addProblematicWordsToList(problematicWords);
		}
	}

	public AbstractAction createActionGoToPreviousWord() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				goToPreviousWord();
			}
		};
	}

	public AbstractAction createActionPause() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				pauseAndResumeWhenDialogIsClosed();
			}
		};
	}

	public AbstractAction createShowFullInformationOrMarkWordAsRecognizedAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (repeatingWordsPanelState
						== RepeatingWordsPanelState.WORD_ASSESSMENT) {
					markWordAsRecognizedAndGoToNext();
					showWordGuessingPanel();
				}
				else {
					showWordAssessmentPanel(currentWord);
				}
			}
		};
	}

	public AbstractAction createNotRecognizedWordAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				markWordAsNotRecognizedAndGoToNext();
				showWordGuessingPanel();
			}
		};
	}

	private void showWordGuessingPanel() {
		panel.showWordGuessingPanel();
		wordDisplayer.showWordGuessingPanel();
	}

	public AbstractAction createActionExit() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				pressedExitButton();
			}
		};
	}

	@Override
	public SavingInformation getApplicationState() {
		SavingInformation savingInformation = applicationWindow.getApplicationController()
				.getApplicationState();
		RepeatingState repeatingState = wordDisplayer
				.getRepeatingState(timeSpentHandler.getTimeForSerialization(),
						repeatingData, convertWordsListToSet());
		savingInformation.setRepeatingState(repeatingState);
		return savingInformation;
	}

	private Set<ListElement> convertWordsListToSet() {
		//TODO why don't we serialize list?
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
		applicationWindow.getStartingPanel().switchToList(
				savingInformation.getRepeatingState()
						.getTypeOfWordForRepeating());
		applicationWindow.displayMessageAboutUnfinishedRepeating();
		applicationWindow.getApplicationController().startRepeating();
	}

	public void setButtonsToWordGuessingState() {
		panelUpdater.setButtonsToWordGuessState(previousWordExists());
	}
}

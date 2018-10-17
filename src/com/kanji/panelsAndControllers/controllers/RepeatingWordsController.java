package com.kanji.panelsAndControllers.controllers;

import com.kanji.constants.enums.RepeatingWordsPanelState;
import com.kanji.constants.enums.TypeOfWordForRepeating;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.listElements.RepeatingData;
import com.kanji.panelsAndControllers.panelUpdaters.RepeatingWordsPanelUpdater;
import com.kanji.panelsAndControllers.panels.RepeatingWordsPanel;
import com.kanji.range.SetOfRanges;
import com.kanji.saving.ApplicationStateManager;
import com.kanji.saving.RepeatingState;
import com.kanji.saving.SavingInformation;
import com.kanji.timer.TimeSpentHandler;
import com.kanji.timer.TimeSpentMonitor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.util.Set;

public class RepeatingWordsController<Word extends ListElement>
		implements TimeSpentMonitor, ApplicationStateManager {

	private ApplicationController applicationController;
	private RepeatingWordsPanelState repeatingWordsPanelState;
	private TimeSpentHandler timeSpentHandler;
	private RepeatingData repeatingData;
	private RepeatingWordsPanel panel;
	private RepeatingWordsPanelUpdater panelUpdater;
	private WordSpecificRepeatingController<Word> wordSpecificRepeatingController;
	private TypeOfWordForRepeating currentTypeOfWordForRepeating;

	public RepeatingWordsController(ApplicationController applicationController,
			WordSpecificRepeatingController<Word> wordSpecificRepeatingController) {
		this.wordSpecificRepeatingController = wordSpecificRepeatingController;
		this.applicationController = applicationController;
		timeSpentHandler = new TimeSpentHandler(this);
		applicationController.getApplicationWindow()
				.setTimeSpentHandler(timeSpentHandler);
		this.panel = new RepeatingWordsPanel(this);
		repeatingWordsPanelState = RepeatingWordsPanelState.WORD_GUESSING;
		panelUpdater = new RepeatingWordsPanelUpdater(panel);
	}

	public WordSpecificRepeatingController<Word> getWordSpecificRepeatingController() {
		return wordSpecificRepeatingController;
	}

	public RepeatingWordsPanel getRepeatingWordsPanel() {
		return panel;
	}

	public void startRepeating() {
		getWordsSpecificController().setListOfAllProblematicWords(
				applicationController.getProblematicWordsBasedOnCurrentTab());
		panelUpdater.clearWordDataPanel();
		timeSpentHandler.startTimer();
		pickNextWordOrFinishRepeating();
		setStateToWordGuessing();
	}

	private WordSpecificRepeatingController getWordsSpecificController() {
		return wordSpecificRepeatingController;
	}

	private void resumeUnfinishedRepeating(RepeatingState repeatingState) {
		getWordsSpecificController()
				.addWordsToRepeat(repeatingState.getCurrentlyRepeatedWords());
		repeatingData = repeatingState.getRepeatingData();
		repeatingData.setRepeatingDate(LocalDateTime.now());
		timeSpentHandler.resumeTime(repeatingState.getTimeSpent());

	}

	private void showWordHint(ListElement word) {
		panelUpdater
				.setWordHint(getWordsSpecificController().getWordHint(word));
	}

	private void updateRemainingWordsText() {
		panelUpdater.updateRemainingWordsText(
				getWordsSpecificController().getNumberOfWordsLeft(),
				currentTypeOfWordForRepeating);
	}

	private void finishRepeating() {
		timeSpentHandler.stopTimer();
		updateApplicationData();
		applicationController.getApplicationWindow()
				.showMessageDialog(createFinishMessage());
		closeRepeatingPanelAndOpenProperOne();
		applicationController.saveProject();
	}

	private void updateApplicationData() {
		repeatingData.setWasRepeated(true);
		repeatingData.setTimeSpentOnRepeating(timeSpentHandler.getTimePassed());
		applicationController.addWordToRepeatingList(repeatingData);
		applicationController.updateProblematicWordsAmount();
		applicationController.getActiveRepeatingList().scrollToBottom();
	}

	private void closeRepeatingPanelAndOpenProperOne() {
		if (getWordsSpecificController().hasProblematicWords()) {
			applicationController.showProblematicWordsDialog(
					getWordsSpecificController().getProblematicWords());
		}
		else {
			applicationController.getApplicationWindow().showPanel(
					applicationController.getApplicationWindow()
							.getStartingPanel().getUniqueName());
			applicationController.finishedRepeating();
		}
	}

	private String createFinishMessage() {
		return String.format(Prompts.REPEATING_DONE + Prompts.REPEATING_TIME,
				timeSpentHandler.getTimePassed());
	}

	private void reset() {
		timeSpentHandler.reset();
		getWordsSpecificController().reset();

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
		ListElement previousWord = getWordsSpecificController()
				.switchToPreviousWord();
		showWordHint(previousWord);
		showFullWordDetailsPanel(previousWord);
		panelUpdater.toggleGoToPreviousWordButton();
	}

	private void showFullWordDetailsPanel(ListElement word) {
		getWordsSpecificController()
				.showFullWordDetailsPanel(word, panel.getWordDataPanel());
		panelUpdater.setButtonsToWordAssessmentState(
				getWordsSpecificController().previousWordExists());
		repeatingWordsPanelState = RepeatingWordsPanelState.WORD_ASSESSMENT;
	}

	private void pauseAndResumeWhenDialogIsClosed() {
		timeSpentHandler.stopTimer();
		applicationController.getApplicationWindow()
				.showMessageDialog(Prompts.PAUSE_ENABLED);
		timeSpentHandler.startTimer();
	}

	private void markWordAsRecognizedAndGoToNext() {
		getWordsSpecificController().markCurrentWordAsRecognized();
		pickNextWordOrFinishRepeating();
		setStateToWordGuessing();
	}

	private void pickNextWordOrFinishRepeating() {
		getWordsSpecificController().removeCurrentWordFromListToRepeat();
		updateRemainingWordsText();
		if (getWordsSpecificController().getNumberOfWordsLeft() > 0) {
			ListElement currentWord = getWordsSpecificController()
					.pickRandomWord();
			showWordHint(currentWord);
		}
		else {
			finishRepeating();
		}
	}

	private void setStateToWordGuessing() {
		panelUpdater.setButtonsToWordGuessState(
				getWordsSpecificController().previousWordExists());
		repeatingWordsPanelState = RepeatingWordsPanelState.WORD_GUESSING;
	}

	private void markWordAsNotRecognizedAndGoToNext() {
		getWordsSpecificController().markWordAsProblematic();
		pickNextWordOrFinishRepeating();
		setStateToWordGuessing();
	}

	private void pressedExitButton() {
		boolean accepted = applicationController.getApplicationWindow()
				.showConfirmDialog(Prompts.EXIT_LEARNING);
		if (!accepted) {
			return;
		}
		applicationController.getApplicationWindow().showPanel(
				applicationController.getApplicationWindow().getStartingPanel()
						.getUniqueName());
		applicationController.finishedRepeating();
		timeSpentHandler.stopTimer();
	}

	public <Word extends ListElement> void resetAndInitializeWordsLists(
			SetOfRanges ranges, Set<Word> problematicWords,
			boolean withProblematic) {
		reset();
		if (!ranges.isEmpty()) {
			getWordsSpecificController().collectSelectedWordsToList(ranges);
		}
		if (withProblematic) {
			getWordsSpecificController()
					.addProblematicWordsToList(problematicWords);
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
					showFullWordDetailsPanel(
							getWordsSpecificController().getCurrentWord());
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
		panelUpdater.clearWordDataPanel();
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
		SavingInformation savingInformation = applicationController
				.getApplicationState();
		RepeatingState repeatingState = getWordsSpecificController()
				.getRepeatingState(timeSpentHandler.getTimeForSerialization(),
						repeatingData, currentTypeOfWordForRepeating);
		savingInformation.setRepeatingState(repeatingState);
		return savingInformation;
	}

	@Override
	public void restoreState(SavingInformation savingInformation) {
		currentTypeOfWordForRepeating = savingInformation.getRepeatingState()
				.getTypeOfWordForRepeating();

		reset();
		resumeUnfinishedRepeating(savingInformation.getRepeatingState());
		applicationController.getApplicationWindow().getStartingPanel()
				.switchToList(savingInformation.getRepeatingState()
						.getTypeOfWordForRepeating());
		applicationController.displayMessageAboutUnfinishedRepeating();
		applicationController.startRepeating();
	}

	public void setTypeOfWordForRepeating(
			TypeOfWordForRepeating typeOfRepeating) {
		this.currentTypeOfWordForRepeating = typeOfRepeating;
	}
}

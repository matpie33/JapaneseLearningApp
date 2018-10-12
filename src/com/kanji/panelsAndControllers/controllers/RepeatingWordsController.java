package com.kanji.panelsAndControllers.controllers;

import com.guimaker.enums.PanelDisplayMode;
import com.kanji.constants.enums.ApplicationPanels;
import com.kanji.constants.enums.RepeatingWordsPanelState;
import com.kanji.constants.enums.TypeOfWordForRepeating;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.listElements.RepeatingData;
import com.kanji.list.listRows.japanesePanelCreatingComponents.JapaneseWordPanelCreator;
import com.kanji.panelsAndControllers.panelUpdaters.RepeatingWordsPanelUpdater;
import com.kanji.panelsAndControllers.panels.RepeatingWordsPanel;
import com.kanji.range.SetOfRanges;
import com.kanji.repeating.RepeatingJapaneseWordsDisplayer;
import com.kanji.repeating.RepeatingKanjiDisplayer;
import com.kanji.saving.ApplicationStateManager;
import com.kanji.saving.RepeatingState;
import com.kanji.saving.SavingInformation;
import com.kanji.timer.TimeSpentHandler;
import com.kanji.timer.TimeSpentMonitor;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RepeatingWordsController
		implements TimeSpentMonitor, ApplicationStateManager {

	private ApplicationWindow applicationWindow;
	private RepeatingWordsPanelState repeatingWordsPanelState;
	private TimeSpentHandler timeSpentHandler;
	private RepeatingData repeatingData;
	private RepeatingWordsPanel panel;
	private RepeatingWordsPanelUpdater panelUpdater;
	private Map<TypeOfWordForRepeating, WordSpecificRepeatingController<? extends ListElement>> typeOfWordToControllerMap = new HashMap<>();
	private TypeOfWordForRepeating currentTypeOfWordForRepeating;

	public RepeatingWordsController(ApplicationWindow applicationWindow) {
		this.applicationWindow = applicationWindow;
		timeSpentHandler = new TimeSpentHandler(this);
		applicationWindow.setTimeSpentHandler(timeSpentHandler);
		this.panel = new RepeatingWordsPanel(this);
		repeatingWordsPanelState = RepeatingWordsPanelState.WORD_GUESSING;
		panelUpdater = new RepeatingWordsPanelUpdater(panel);
		initializeTypeOfWordToControllerMap();
	}

	private void initializeTypeOfWordToControllerMap() {
		typeOfWordToControllerMap.put(TypeOfWordForRepeating.KANJIS,
				new WordSpecificRepeatingController<>(
						applicationWindow.getApplicationController()
								.getKanjiList(), new RepeatingKanjiDisplayer(
						ApplicationWindow.getKanjiFont()), this));
		typeOfWordToControllerMap.put(TypeOfWordForRepeating.JAPANESE_WORDS,
				new WordSpecificRepeatingController<>(
						applicationWindow.getApplicationController()
								.getJapaneseWords(),
						new RepeatingJapaneseWordsDisplayer(
								new JapaneseWordPanelCreator(applicationWindow
										.getApplicationController(),
										applicationWindow,
										PanelDisplayMode.VIEW)), this));
	}

	public RepeatingWordsPanel getRepeatingWordsPanel() {
		return panel;
	}

	public void startRepeating() {
		getWordsSpecificController().setListOfAllProblematicWords(
				applicationWindow.getApplicationController()
						.getProblematicWordsBasedOnCurrentTab());
		panelUpdater.clearWordDataPanel();
		timeSpentHandler.startTimer();
		pickNextWordOrFinishRepeating();
		setStateToWordGuessing();
	}

	private WordSpecificRepeatingController getWordsSpecificController() {
		return typeOfWordToControllerMap.get(currentTypeOfWordForRepeating);
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

	public void updateRemainingWordsText() {
		panelUpdater.updateRemainingWordsText(
				getWordsSpecificController().getNumberOfWordsLeft());
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
		applicationWindow.getApplicationController()
				.addWordToRepeatingList(repeatingData);
		applicationWindow.updateProblematicWordsAmount();
		applicationWindow.scrollRepeatingListToBottom();
	}

	private void closeRepeatingPanelAndOpenProperOne() {
		if (getWordsSpecificController().hasProblematicWords()) {
			applicationWindow.showProblematicWordsDialog(
					getWordsSpecificController().getProblematicWords());
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
		getWordsSpecificController().showFullWordDetailsPanel(word);
		panelUpdater.setButtonsToWordAssessmentState(
				getWordsSpecificController().previousWordExists());
		repeatingWordsPanelState = RepeatingWordsPanelState.WORD_ASSESSMENT;
	}

	private void pauseAndResumeWhenDialogIsClosed() {
		timeSpentHandler.stopTimer();
		applicationWindow.showMessageDialog(Prompts.PAUSE_ENABLED);
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
		boolean accepted = applicationWindow
				.showConfirmDialog(Prompts.EXIT_LEARNING);
		if (!accepted) {
			return;
		}
		applicationWindow.showPanel(ApplicationPanels.STARTING_PANEL);
		applicationWindow.getApplicationController().finishedRepeating();
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
		SavingInformation savingInformation = applicationWindow
				.getApplicationController().getApplicationState();
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
		applicationWindow.getStartingPanel().switchToList(
				savingInformation.getRepeatingState()
						.getTypeOfWordForRepeating());
		applicationWindow.displayMessageAboutUnfinishedRepeating();
		applicationWindow.getApplicationController().startRepeating();
	}

	public void setButtonsToWordGuessingState() {
		panelUpdater.setButtonsToWordGuessState(
				getWordsSpecificController().previousWordExists());
	}

	public void setTypeOfWordForRepeating(
			TypeOfWordForRepeating typeOfRepeating) {
		this.currentTypeOfWordForRepeating = typeOfRepeating;
	}
}

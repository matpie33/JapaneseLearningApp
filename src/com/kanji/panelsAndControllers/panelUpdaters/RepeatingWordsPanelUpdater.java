package com.kanji.panelsAndControllers.panelUpdaters;

import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.Labels;
import com.kanji.constants.strings.Prompts;
import com.kanji.panelsAndControllers.panels.RepeatingWordsPanel;

import javax.swing.*;

public class RepeatingWordsPanelUpdater {

	private RepeatingWordsPanel panel;

	public RepeatingWordsPanelUpdater(RepeatingWordsPanel panel) {
		this.panel = panel;
	}

	public void setButtonsToWordGuessState(boolean previousWordExists) {
		panel.getNotRecognizedWordButton().setEnabled(false);
		panel.getShowPreviousWordButton().setEnabled(previousWordExists);
		panel.getShowWordOrMarkAsRecognizedButton()
				.setText(ButtonsNames.SHOW_WORD);

	}

	public void setButtonsToWordAssessmentState(boolean previousWordExists) {
		panel.getShowWordOrMarkAsRecognizedButton()
				.setText(ButtonsNames.RECOGNIZED_WORD);
		panel.getNotRecognizedWordButton().setEnabled(true);
		panel.getShowPreviousWordButton().setEnabled(previousWordExists);
	}

	public void updateRemainingWordsText(int numberOfWordsRemaining) {
		panel.getRemainingLabel()
				.setText(createRemainingWordsText(numberOfWordsRemaining));
	}

	public String createRemainingWordsText(int numberOfWordsRemaining) {
		return Prompts.REMAINING_WORDS + " " + numberOfWordsRemaining + " "
				+ Prompts.KANJI;
	}

	public void updateTime(String timePassed) {
		panel.getTimeElapsedLabel().setText(Labels.TIME_LABEL + timePassed);
	}

	public void setWordHint(String wordHint) {
		panel.getWordHintTextPane().setText(wordHint);
	}

	public void toggleGoToPreviousWordButton() {
		AbstractButton showPreviousWordButton = panel
				.getShowPreviousWordButton();
		showPreviousWordButton.setEnabled(!showPreviousWordButton.isEnabled());
	}
}

package com.kanji.panelsAndControllers.panelUpdaters;

import com.kanji.constants.enums.TypeOfWordForRepeating;
import com.kanji.constants.strings.JapaneseApplicationButtonsNames;
import com.kanji.constants.strings.Labels;
import com.kanji.constants.strings.Prompts;
import com.kanji.panelsAndControllers.panels.RepeatingWordsPanel;
import com.kanji.utilities.JapaneseWritingUtilities;

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
				.setText(JapaneseApplicationButtonsNames.SHOW_WORD);

	}

	public void setButtonsToWordAssessmentState(boolean previousWordExists) {
		panel.getShowWordOrMarkAsRecognizedButton()
				.setText(JapaneseApplicationButtonsNames.RECOGNIZED_WORD);
		panel.getNotRecognizedWordButton().setEnabled(true);
		panel.getShowPreviousWordButton().setEnabled(previousWordExists);
	}

	public void updateRemainingWordsText(int numberOfWordsRemaining,
			TypeOfWordForRepeating typeOfWordForRepeating) {
		panel.getRemainingWordsAmountLabel().setText(
				createRemainingWordsText(numberOfWordsRemaining,
						typeOfWordForRepeating));
	}

	private String createRemainingWordsText(int numberOfWordsRemaining,
			TypeOfWordForRepeating typeOfWordForRepeating) {
		return String.format(Prompts.WORDS_LEFT_TO_REPEAT, JapaneseWritingUtilities
						.getTextForTypeOfWordForRepeating(typeOfWordForRepeating),
				numberOfWordsRemaining);
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

	public void clearWordDataPanel() {
		panel.getWordDataPanel().clear();
	}
}

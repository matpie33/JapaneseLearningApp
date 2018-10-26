package com.kanji.panelsAndControllers.panelUpdaters;

import com.guimaker.panels.MainPanel;
import com.guimaker.row.AbstractSimpleRow;
import com.kanji.constants.enums.TypeOfWordForRepeating;
import com.kanji.constants.strings.Prompts;
import com.kanji.panelsAndControllers.panels.LearningStartPanel;
import com.kanji.utilities.JapaneseWritingUtilities;

import javax.swing.*;
import java.awt.*;

public class LearningStartPanelUpdater {

	private LearningStartPanel panel;

	public LearningStartPanelUpdater(LearningStartPanel panel) {
		this.panel = panel;
	}

	public void addProblematicWordsNotification() {
		Component focusOwner = panel.getDialog()
									.getContainer()
									.getFocusOwner();
		panel.addLabelWithProblematicWords();
		panel.getRangesPanel()
			 .updateView();
		focusOwner.requestFocusInWindow();
	}

	public void removeProblematicWordsNotification() {
		panel.getRangesPanel()
			 .removeRowWithElements(panel.getProblematicWordsLabel());
	}

	public void updateSumOfWords(int sumOfWords,
			TypeOfWordForRepeating typeOfWordForRepeating) {
		panel.getSumOfWordsLabel()
			 .setText(createSumOfWordsText(sumOfWords, typeOfWordForRepeating));
	}

	private String createSumOfWordsText(int sumOfWords,
			TypeOfWordForRepeating typeOfWordForRepeating) {
		return String.format(Prompts.SUM_OF_WORDS_TO_BE_REPEATED,
				JapaneseWritingUtilities.getTextForTypeOfWordForRepeating(
						typeOfWordForRepeating), sumOfWords);
	}

	public void removeRow(int rowNumber) {
		panel.getRangesPanel()
			 .removeRow(rowNumber);
		panel.getDialog()
			 .getContainer()
			 .getMostRecentFocusOwner()
			 .requestFocusInWindow();
	}

	public void showErrorRow(String error, int rowNumber) {
		panel.showErrorOnThePanel(error, rowNumber);
	}

	public void changeEnabledStateOfDeleteButtonInFirstRow(boolean visible) {
		panel.getRangesPanel()
			 .changeEnabledStateOfLastElementInRow(0, visible);
	}

	public void showErrorInNewDialog(String error) {
		panel.getDialog()
			 .showMessageDialog(error);
	}

	public void closeLearningStartDialog() {
		panel.getDialog()
			 .getContainer()
			 .dispose();
	}

	public void createRangeRow() {
		panel.addRowToRangesPanel();
	}

	public void insertRangeRow(int nextRowNumber, AbstractSimpleRow newRow) {
		panel.getRangesPanel()
			 .insertRow(nextRowNumber, newRow);
		panel.getRangesPanel()
			 .updateView();
	}

	public void addRangeRow(AbstractSimpleRow newRow) {
		panel.getRangesPanel()
			 .addRow(newRow);
		panel.getRangesPanel()
			 .updateView();
	}

	public void scrollRangesPanelToRow(int rowNumber) {
		SwingUtilities.invokeLater(new Runnable() {
			// TODO swing utilities
			@Override
			public void run() {
				MainPanel rangesPanel = panel.getRangesPanel();
				rangesPanel.getPanel()
						   .scrollRectToVisible(rangesPanel.getRows()
														   .get(rowNumber)
														   .getBounds());
			}
		});
	}

	public void scrollRangesPanelToBottom(JScrollPane rangesPanelScrollPane) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// TODO swing utilities
				rangesPanelScrollPane.getVerticalScrollBar()
									 .setValue(
											 rangesPanelScrollPane.getVerticalScrollBar()
																  .getMaximum());
			}
		});
	}

	public void setProblematicWordsAmountText(
			TypeOfWordForRepeating typeOfWordForRepeating,
			int numberOfProblematicWords) {
		panel.getProblematicWordsAmountLabel()
			 .setText(createProblematicWordsAmountText(typeOfWordForRepeating,
					 numberOfProblematicWords));

	}

	private String createProblematicWordsAmountText(
			TypeOfWordForRepeating typeOfWordForRepeating,
			int numberOfProblematicWords) {
		return String.format(Prompts.PROBLEMATIC_WORDS_AMOUNT,
				JapaneseWritingUtilities.getTextForTypeOfWordForRepeating(
						typeOfWordForRepeating), numberOfProblematicWords);

	}
}

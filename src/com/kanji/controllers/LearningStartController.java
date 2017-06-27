package com.kanji.controllers;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JTextField;

import com.kanji.Row.RepeatingInformation;
import com.kanji.Row.RepeatingList;
import com.kanji.constants.ExceptionsMessages;
import com.kanji.constants.Options;
import com.kanji.myList.MyList;
import com.kanji.panels.LearningStartPanel;
import com.kanji.range.Range;
import com.kanji.range.SetOfRanges;
import com.kanji.windows.ApplicationWindow;

public class LearningStartController {

	private MyList<RepeatingList> repeatsList;
	private SetOfRanges rangesToRepeat;
	private int numberOfWords;
	private int sumOfWords;
	private ApplicationWindow parentFrame; // TODO rethink if it better suits in
											// learning start panel instead
	private LearningStartPanel learningStartPanel;
	private List<Range> rangeOfKanjiInRow;

	public LearningStartController(MyList<RepeatingList> repeatList, int numberOfWords,
			ApplicationWindow parentFrame, LearningStartPanel learningStartPanel) {
		this.parentFrame = parentFrame;
		this.repeatsList = repeatList;
		this.numberOfWords = numberOfWords;
		this.learningStartPanel = learningStartPanel;
		rangeOfKanjiInRow = new ArrayList<>();
	}

	public void updateProblematicKanjiNumber(boolean isProblematicKanjiCheckboxSelected) {
		int problematicKanjis = getProblematicKanjiNumber();
		if (isProblematicKanjiCheckboxSelected)
			sumOfWords += problematicKanjis;
		else
			sumOfWords -= problematicKanjis;
		learningStartPanel.updateSumOfWords(getSumOfWords());

	}

	public int getProblematicKanjiNumber() {
		return parentFrame.getProblematicKanjis().size();
	}

	public void handleKeyTyped(KeyEvent e, boolean problematicCheckboxSelected, int rowNumber) {
		if ((e.getKeyChar() == KeyEvent.VK_ENTER)) {
			validateAndStart(problematicCheckboxSelected);
		}
		else if (!(e.getKeyChar() + "").matches("\\d")) {
			e.consume();
			learningStartPanel.showErrorIfNotExists(ExceptionsMessages.valueIsNotNumber, rowNumber);
		}
	}

	public void handleKeyReleased(KeyEvent e, JTextField to, JTextField from,
			boolean problematicCheckboxSelected, int rowNumber) {

		int valueFrom = 0;
		int valueTo = 0;
		if (to.getText().isEmpty() || from.getText().isEmpty()) {
			return;
		}
		else {
			valueFrom = Integer.parseInt(from.getText());
			valueTo = Integer.parseInt(to.getText());
		}

		String error = "";
		if (valueTo <= valueFrom) {
			error = ExceptionsMessages.rangeToValueLessThanRangeFromValue;
		}
		else if (isNumberHigherThanMaximum(valueFrom) || isNumberHigherThanMaximum(valueTo))
			error = ExceptionsMessages.rangeValueTooHigh;
		else if (valueTo > numberOfWords) {
			error = ExceptionsMessages.rangeValueHigherThanMaximumKanjiNumber;
		}

		// TODO separate it in 2 methods: validate and update
		if (error.isEmpty()) {
			rangeOfKanjiInRow.set(rowNumber, new Range(valueFrom, valueTo));
			recalculateSumOfKanji(problematicCheckboxSelected);
			learningStartPanel.removeErrorIfExists(rowNumber);
			learningStartPanel.updateSumOfWords(getSumOfWords());
		}
		else {
			learningStartPanel.showErrorIfNotExists(error, rowNumber);
		}

	}

	private boolean isNumberHigherThanMaximum(int number) {
		return number > numberOfWords;
	}

	public void removeRange(int rowNumber, boolean problematicCheckboxSelected) {
		rangeOfKanjiInRow.remove(rowNumber);
		recalculateSumOfKanji(problematicCheckboxSelected);
		learningStartPanel.updateSumOfWords(getSumOfWords());
	}

	private void recalculateSumOfKanji(boolean problematicCheckboxSelected) {
		try {
			SetOfRanges s = new SetOfRanges();
			for (Range r : rangeOfKanjiInRow) {
				s.addRange(r);
			}
			sumOfWords = 0;
			if (problematicCheckboxSelected) {
				sumOfWords += getProblematicKanjiNumber();
			}
			this.sumOfWords += s.sumRangeInclusive();

		}
		catch (IllegalArgumentException ex) {
			// We keep the message for untill approve button is clicked //TODO
			// refactor it
		}

	}

	public void validateAndStart(boolean problematicCheckboxSelected) {
		rangesToRepeat = sumRanges();
		if (rangesToRepeat.toString().isEmpty() && !problematicCheckboxSelected) {
			learningStartPanel.showErrorDialog(ExceptionsMessages.noInputSupplied);
			return;
		}

		addToRepeatsListOrShowError(problematicCheckboxSelected);
		learningStartPanel.switchToRepeatingPanel();
	}

	private void addToRepeatsListOrShowError(boolean problematicCheckboxSelected) {
		Calendar calendar = Calendar.getInstance();
		String repeatingInfo = "";
		if (problematicCheckboxSelected) {
			repeatingInfo = Options.problematicKanjiOption;
			if (rangesToRepeat.getRangesAsList().size() > 0) {
				repeatingInfo += ", ";
			}
		}
		repeatingInfo += rangesToRepeat;
		repeatingInfo += ".";
		parentFrame.setRepeatingInformation(
				new RepeatingInformation(repeatingInfo, calendar.getTime(), false));
		repeatsList.scrollToBottom();
	}

	public void switchPanels(boolean problematicCheckboxSelected) {
		parentFrame.showCardPanel(ApplicationWindow.LEARNING_PANEL);
		parentFrame.setWordsRangeToRepeat(rangesToRepeat, problematicCheckboxSelected);
	}

	private SetOfRanges sumRanges() {
		SetOfRanges setOfRanges = new SetOfRanges();
		for (Range r : rangeOfKanjiInRow) {
			setOfRanges.addRange(r);
		}
		return setOfRanges;

	}

	public int getSumOfWords() {
		return sumOfWords;
	}

	public void addRangesRow() {
		rangeOfKanjiInRow.add(new Range(-1, 0));
	}

}

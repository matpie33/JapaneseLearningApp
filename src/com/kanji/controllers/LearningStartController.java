package com.kanji.controllers;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

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
		rangesToRepeat = sumRanges();
		Set<Integer> problematics = parentFrame.getProblematicKanjis();
		int direction;
		if (isProblematicKanjiCheckboxSelected)
			direction = 1;
		else
			direction = -1;
		for (Integer i : problematics) {
			if (!rangesToRepeat.isValueInsideThisSet(i)) {
				sumOfWords += direction;
				System.out.println("this value is not in set: " + i);
			}
		}
		learningStartPanel.updateSumOfWords(getSumOfWords());
	}

	public int getProblematicKanjiNumber() {
		return parentFrame.getProblematicKanjis().size();
	}

	public void handleKeyTyped(KeyEvent e, boolean problematicCheckboxSelected, int rowNumber) {
		if (e.getKeyChar() == 'p') {
			e.consume();
		}
		else if (!(e.getKeyChar() + "").matches("\\d")) {
			e.consume();
			learningStartPanel.showErrorIfNotExists(ExceptionsMessages.valueIsNotNumber, rowNumber);
		}
	}

	public void handleKeyReleased(KeyEvent e, JTextField to, JTextField from,
			boolean problematicCheckboxSelected, int rowNumber) {

		if (ignoredKeyWasPressedOrOneOfInputsIsEmpty(e, to, from)) {
			return;
		}

		int valueFrom = Integer.parseInt(from.getText());
		int valueTo = Integer.parseInt(to.getText());
		String error = validateInput(valueFrom, valueTo);

		if (error.isEmpty()) {
			updateKanjiNumber(rowNumber, valueFrom, valueTo, problematicCheckboxSelected);
			learningStartPanel.removeErrorIfExists(rowNumber);
		}
		else {
			learningStartPanel.showErrorIfNotExists(error, rowNumber);
		}

	}

	private boolean ignoredKeyWasPressedOrOneOfInputsIsEmpty(KeyEvent e, JTextField to,
			JTextField from) {
		return e.getKeyChar() == KeyEvent.VK_P || to.getText().isEmpty()
				|| from.getText().isEmpty();
	}

	private String validateInput(int rangeStart, int rangeEnd) {
		String error = "";
		if (rangeStart == 0) {
			error = ExceptionsMessages.rangeStartHaveToBePositive;
		}
		else if (rangeEnd <= rangeStart) {
			error = ExceptionsMessages.rangeToValueLessThanRangeFromValue;
		}
		else if (isNumberHigherThanMaximum(rangeStart) || isNumberHigherThanMaximum(rangeEnd))
			error = ExceptionsMessages.rangeValueTooHigh;
		else if (rangeEnd > numberOfWords) {
			error = ExceptionsMessages.rangeValueHigherThanMaximumKanjiNumber;
		}
		return error;
	}

	private boolean isNumberHigherThanMaximum(int number) {
		return number > numberOfWords;
	}

	private void updateKanjiNumber(int rowNumber, int valueFrom, int valueTo,
			boolean problematicCheckboxSelected) {
		rangeOfKanjiInRow.set(rowNumber, new Range(valueFrom, valueTo));
		recalculateSumOfKanji(problematicCheckboxSelected);
		learningStartPanel.updateSumOfWords(getSumOfWords());
	}

	public void removeRange(int rowNumber, boolean problematicCheckboxSelected) {
		rangeOfKanjiInRow.remove(rowNumber);
		recalculateSumOfKanji(problematicCheckboxSelected);
		learningStartPanel.updateSumOfWords(getSumOfWords());
	}

	private void recalculateSumOfKanji(boolean problematicKanjisSelected) {
		SetOfRanges s = new SetOfRanges();
		for (Range r : rangeOfKanjiInRow) {
			if (!r.isEmpty()) {
				s.addRange(r);
			}
		}
		sumOfWords = 0;
		this.sumOfWords += s.sumRangeInclusive();
		if (problematicKanjisSelected) {
			sumOfWords += getProblematicKanjiNumber();
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
		rangeOfKanjiInRow.add(new Range(0, 0));
	}

	public String concatenateErrors(String... errors) {
		String concatenated = "";
		for (String error : errors) {
			concatenated += error;
			concatenated += "\n";
		}
		return concatenated;
	}

}

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
import com.kanji.dataObjects.RangesRow;
import com.kanji.myList.MyList;
import com.kanji.panels.LearningStartPanel;
import com.kanji.range.SetOfRanges;
import com.kanji.windows.ApplicationWindow;

public class LearningStartController {

	private MyList<RepeatingList> repeatsList;
	private SetOfRanges rangesToRepeat;
	private int numberOfWords;
	private int sumOfWords;
	// TODO this should be some controller or something
	private ApplicationWindow parentFrame;
	private LearningStartPanel learningStartPanel;
	private List<String> errors;
	private List<RangesRow> rangesRows;
	private int problematicLabelRow;

	public LearningStartController(MyList<RepeatingList> repeatList, int numberOfWords,
			ApplicationWindow parentFrame, LearningStartPanel learningStartPanel) {
		this.parentFrame = parentFrame;
		this.repeatsList = repeatList;
		this.numberOfWords = numberOfWords;
		this.learningStartPanel = learningStartPanel;
		rangesRows = new ArrayList<>();
		errors = new ArrayList<>();
	}

	// TODO ???? 2 methods with similar names
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
			}
		}
		learningStartPanel.updateSumOfWords(getSumOfWords());
		if (isProblematicKanjiCheckboxSelected) {
			int rowNumber = learningStartPanel.showLabelWithProblematicKanjis();
			problematicLabelRow = rowNumber;
		}
		else {
			learningStartPanel.hideLabelWithProblematicKanjis(problematicLabelRow);
			updateRowsNumbers(problematicLabelRow - 1, -1);
		}

	}

	public int getProblematicKanjiNumber() {
		return parentFrame.getProblematicKanjis().size();
	}

	public void addRow(int rowNumber, JTextField from, JTextField to) {
		RangesRow rangesRow = new RangesRow(from, to, rowNumber);
		rangesRows.add(rangesRow);
	}

	public void increaseProblematicLabelRowNumber() {
		problematicLabelRow++;
	}

	public void handleKeyTyped(KeyEvent e, JTextField to, JTextField from,
			boolean problematicCheckboxSelected) {
		if (!(e.getKeyChar() + "").matches("\\d")) {
			e.consume();
		}
	}

	private void removeError(RangesRow rangesRow) {
		int rowNumber = rangesRow.getTextFieldsRowNumber();
		learningStartPanel.removeRowFromPanel(rowNumber + 1);
		updateRowsNumbers(rowNumber, -1);
	}

	private void showError(RangesRow rangesRow, String error) {
		int rowNumber = rangesRow.getTextFieldsRowNumber();
		learningStartPanel.showErrorOnThePanel(error, rowNumber + 1);
		rangesRow.setError(error);
		updateRowsNumbers(rowNumber, 1);
	}

	private RangesRow findRowWithTextFields(JTextField textFieldFrom, JTextField textFieldTo) {
		for (RangesRow row : rangesRows) {
			if (row.gotTextFields(textFieldFrom, textFieldTo)) {
				return row;
			}
		}
		return null;
	}

	public void handleKeyReleased(KeyEvent e, JTextField to, JTextField from,
			boolean problematicCheckboxSelected) {

		if (handleEmptyTextFields(e, to, from, problematicCheckboxSelected)) {
			return;
		}
		boolean fromTextFieldWasFocused = from.hasFocus();

		int valueFrom = Integer.parseInt(from.getText());
		int valueTo = Integer.parseInt(to.getText());
		String error = validateRangesInput(valueFrom, valueTo);
		RangesRow rowWithTextFields = findRowWithTextFields(from, to);
		if (error.isEmpty()) {
			rowWithTextFields.setRangeValues(valueFrom, valueTo);
			if (rowWithTextFields.errorNotEmpty()) {
				removeError(rowWithTextFields);
				rowWithTextFields.setError("");
			}
		}
		else {
			rowWithTextFields.setRangeValues(0, 0);
			if (rowWithTextFields.errorNotEmpty() && !rowWithTextFields.getError().equals(error)) {
				removeError(rowWithTextFields);
			}
			if (!rowWithTextFields.getError().equals(error)) {
				showError(rowWithTextFields, error);
				if (fromTextFieldWasFocused) {
					from.requestFocusInWindow();
				}
				else {
					to.requestFocusInWindow();
				}

			}
		}
		updateKanjiNumber(problematicCheckboxSelected);
	}

	public void updateRowsNumbers(int fromRowNumber, int positiveOrNegativeValue) {
		for (RangesRow row : rangesRows) {
			if (row.getTextFieldsRowNumber() > fromRowNumber) {
				row.setRowNumber(row.getTextFieldsRowNumber() + positiveOrNegativeValue);
			}
		}
	}

	private boolean handleEmptyTextFields(KeyEvent e, JTextField to, JTextField from,
			boolean problematicCheckboxSelected) {
		if (from.getText().isEmpty() || to.getText().isEmpty()) {
			if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
				RangesRow rowWithTextFields = findRowWithTextFields(from, to);
				rowWithTextFields.setRangeValues(0, 0);
				updateKanjiNumber(problematicCheckboxSelected);
			}
			return true;
		}

		return false;
	}

	private String validateRangesInput(int rangeStart, int rangeEnd) {
		String error = "";
		if (rangeStart == 0) {
			error = ExceptionsMessages.rangeStartHaveToBePositive;
		}
		else if (rangeEnd <= rangeStart) {
			error = ExceptionsMessages.rangeToValueLessThanRangeFromValue;
		}
		else if (isNumberHigherThanMaximum(rangeStart) || isNumberHigherThanMaximum(rangeEnd)) {
			error = ExceptionsMessages.rangeValueHigherThanMaximumKanjiNumber;
			error += " (" + numberOfWords + ").";
		}

		return error;
	}

	private boolean isNumberHigherThanMaximum(int number) {
		return number > numberOfWords;
	}

	private void updateKanjiNumber(boolean problematicCheckboxSelected) {
		recalculateSumOfKanji(problematicCheckboxSelected);
		learningStartPanel.updateSumOfWords(getSumOfWords());
	}

	public void removeRangeRow(JTextField from, JTextField to,
			boolean problematicCheckboxSelected) {

		RangesRow rowWithTextFields = findRowWithTextFields(from, to);
		int rowWithTextFieldsNumber = rowWithTextFields.getTextFieldsRowNumber();
		boolean wasError = rowWithTextFields.errorNotEmpty();
		learningStartPanel.removeRow(rowWithTextFieldsNumber);
		if (wasError) {
			learningStartPanel.removeRow(rowWithTextFieldsNumber);
		}

		int decreaseBy = -1;
		if (wasError) {
			decreaseBy = -2;
		}
		updateRowsNumbers(rowWithTextFieldsNumber, decreaseBy);
		rangesRows.remove(rowWithTextFields);
		if (getNumberOfRangesRows() == 1) {
			learningStartPanel.changeVisibilityOfDeleteButtonInFirstRow(false);
		}
		recalculateSumOfKanji(problematicCheckboxSelected);
		learningStartPanel.updateSumOfWords(getSumOfWords());
	}

	private void recalculateSumOfKanji(boolean problematicKanjisSelected) {
		SetOfRanges s = new SetOfRanges();
		for (RangesRow r : rangesRows) {
			if (!r.getRange().isEmpty()) {
				s.addRange(r.getRange());
			}
		}
		sumOfWords = 0;
		this.sumOfWords += s.sumRangeInclusive();
		if (problematicKanjisSelected) {
			sumOfWords += getProblematicKanjiNumber();
		}
	}

	private void validateAndStart(boolean problematicCheckboxSelected) {

		rangesToRepeat = sumRanges(); // TODO problem when someone types range
										// and immediately presses enter -
										// ranges to repeat is empty because no
										// key released was found
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

	public void switchPanels(boolean problematicCheckboxSelected) { // TODO
																	// naming is
																	// not
																	// accurate
		parentFrame.showCardPanel(ApplicationWindow.LEARNING_PANEL);
		parentFrame.setWordsRangeToRepeat(rangesToRepeat, problematicCheckboxSelected);

	}

	private SetOfRanges sumRanges() { // TODO this is duplicate
		SetOfRanges setOfRanges = new SetOfRanges();
		for (RangesRow r : rangesRows) {
			if (!r.getRange().isEmpty()) {
				setOfRanges.addRange(r.getRange());
			}

		}
		return setOfRanges;

	}

	public int getSumOfWords() {
		return sumOfWords;
	}

	private String concatenateErrors() {
		String concatenated = "";
		for (String error : errors) {
			concatenated += error;
			concatenated += "\n";
		}
		return concatenated;
	}

	public int getNumberOfRangesRows() {
		return rangesRows.size();
	}

	public boolean gotErrors() {
		List<String> errors = new ArrayList<>();
		boolean gotError = false;
		for (RangesRow r : rangesRows) {
			if (r.errorNotEmpty()) {
				errors.add(r.getError());
				gotError = true;
			}
		}
		this.errors = errors;
		return gotError;
	}

	public void showErrorsOrStart(boolean problematicCheckboxSelected) {
		if (gotErrors()) {
			String errors = concatenateErrors();
			learningStartPanel.showErrorDialog(errors);
		}
		else {
			validateAndStart(problematicCheckboxSelected);
		}
	}

}

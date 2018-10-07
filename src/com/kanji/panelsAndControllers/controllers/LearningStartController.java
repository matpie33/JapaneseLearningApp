package com.kanji.panelsAndControllers.controllers;

import com.guimaker.row.AbstractSimpleRow;
import com.kanji.constants.enums.IncrementSign;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.constants.strings.Labels;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.listElements.RepeatingData;
import com.kanji.model.RangesRow;
import com.kanji.panelsAndControllers.panelUpdaters.LearningStartPanelUpdater;
import com.kanji.panelsAndControllers.panels.LearningStartPanel;
import com.kanji.panelsAndControllers.validation.LearningStartPanelInputValidation;
import com.kanji.range.SetOfRanges;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

public class LearningStartController {

	private SetOfRanges rangesToRepeat;
	private int sumOfWords;
	private LearningStartPanel learningStartPanel;
	private Map<Integer, String> errors;
	private List<RangesRow> rangesRows;
	private int problematicLabelRow;
	private ApplicationController applicationController;
	private LearningStartPanelInputValidation inputValidation;
	private LearningStartPanelUpdater panelUpdater;

	public LearningStartController(int numberOfWords,
			ApplicationController applicationController,
			LearningStartPanel learningStartPanel) {
		this.applicationController = applicationController;
		this.learningStartPanel = learningStartPanel;
		rangesRows = new ArrayList<>();
		errors = new HashMap<>();
		inputValidation = new LearningStartPanelInputValidation(
				numberOfWords);
		panelUpdater = new LearningStartPanelUpdater(learningStartPanel);
	}

	private void updateSumOfWords(boolean isProblematicWordsCheckboxSelected) {
		rangesToRepeat = addAllRangesToSet();
		IncrementSign incrementSign = isProblematicWordsCheckboxSelected ?
				IncrementSign.PLUS :
				IncrementSign.MINUS;
		addOrSubtractProblematicWordsFromSum(incrementSign);
		panelUpdater.updateSumOfWords(getSumOfWords());
	}

	private void addProblematicWordsNotification(
			boolean isProblematicWordsCheckboxSelected) {
		if (isProblematicWordsCheckboxSelected) {
			panelUpdater.addProblematicWordsNotification();
		}
		else {
			panelUpdater
					.removeProblematicWordsNotification(problematicLabelRow);
			updateRowsNumbers(problematicLabelRow - 1, -1);
		}
	}

	private void addOrSubtractProblematicWordsFromSum(
			IncrementSign incrementSign) {
		if (applicationController.getActiveWordsList().getListElementClass()
				.equals(Kanji.class)) {
			updateSumBasedOnProblematicKanjis(incrementSign);
		}
		else {
			updateSumBasedOnProblematicWords(incrementSign);
		}

	}

	private void updateSumBasedOnProblematicWords(IncrementSign incrementSign) {
		Set<JapaneseWord> problematicWords = applicationController
				.getProblematicJapaneseWords();
		sumOfWords += incrementSign.getSignValue() * problematicWords.size();
		//TODO figure out if problematic word is or is not inside selected range
	}

	private void updateSumBasedOnProblematicKanjis(
			IncrementSign incrementSign) {
		Set<Kanji> problematics = applicationController.getProblematicKanjis();
		for (Kanji i : problematics) {
			if (!rangesToRepeat.isValueInsideThisSet(i.getId())) {
				sumOfWords += incrementSign.getSignValue();
			}
		}
	}

	public int getProblematicWordsNumber() {
		return applicationController
				.getProblematicWordsAmountBasedOnCurrentTab();
	}

	public void addRow(int rowNumber, JTextComponent from, JTextComponent to) {
		RangesRow rangesRow = new RangesRow(from, to, rowNumber);
		rangesRows.add(rangesRow);
	}

	private void removeError(RangesRow rangesRow) {
		int rowNumber = rangesRow.getTextFieldsRowNumber();
		errors.remove(rowNumber);
		panelUpdater.removeRow(rowNumber + 1);
		updateRowsNumbers(rowNumber, -1);
		rangesRow.setError("");
	}

	private void showError(RangesRow rangesRow, String error) {
		int rowNumber = rangesRow.getTextFieldsRowNumber();
		panelUpdater.showErrorRow(error, rowNumber + 1);
		panelUpdater.scrollRangesPanelToRow(rowNumber + 1);
		rangesRow.setError(error);
		updateRowsNumbers(rowNumber, 1);
	}

	private RangesRow findRowWithTextFields(JTextComponent textFieldFrom,
			JTextComponent textFieldTo) {
		for (RangesRow row : rangesRows) {
			if (row.gotTextFields(textFieldFrom, textFieldTo)) {
				return row;
			}
		}
		return null;
	}

	private void handleKeyReleased(KeyEvent e, JTextComponent to,
			JTextComponent from, boolean problematicCheckboxSelected) {

		if (from.getText().isEmpty() || to.getText().isEmpty()) {
			if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
				resetRangeForRowAndUpdateSum(to, from,
						problematicCheckboxSelected);
			}
		}
		else {
			processTextFieldsInputAfterKeyRelease(to, from,
					problematicCheckboxSelected);
		}

	}

	private void resetRangeForRowAndUpdateSum(JTextComponent to,
			JTextComponent from, boolean problematicCheckboxSelected) {
		RangesRow rowWithTextFields = findRowWithTextFields(from, to);
		rowWithTextFields.setRangeValues(0, 0);
		updateNumberOfSelectedWords(problematicCheckboxSelected);
	}

	private void processTextFieldsInputAfterKeyRelease(JTextComponent to,
			JTextComponent from, boolean problematicCheckboxSelected) {
		Component focusedComponent = learningStartPanel.getDialog()
				.getContainer().getFocusOwner();
		int valueFrom = Integer.parseInt(from.getText());
		int valueTo = Integer.parseInt(to.getText());
		String error = inputValidation
				.validateRangesInput(valueFrom, valueTo);
		RangesRow rowWithTextFields = findRowWithTextFields(from, to);

		if (rowWithTextFields.errorNotEmpty()) {
			removeError(rowWithTextFields);
		}
		if (!error.isEmpty()) {
			rowWithTextFields.setRangeValues(0, 0);
			showError(rowWithTextFields, error);
			focusedComponent.requestFocusInWindow();
		}
		else {
			rowWithTextFields.setRangeValues(valueFrom, valueTo);
		}
		updateNumberOfSelectedWords(problematicCheckboxSelected);
	}

	private void updateRowsNumbers(int fromRowNumber, int updateRowNumberBy) {
		for (RangesRow row : rangesRows) {
			if (row.getTextFieldsRowNumber() > fromRowNumber) {
				row.setRowNumber(
						row.getTextFieldsRowNumber() + updateRowNumberBy);
			}
		}
	}

	private void updateNumberOfSelectedWords(
			boolean problematicCheckboxSelected) {
		recalculateSumOfWords(problematicCheckboxSelected);
		panelUpdater.updateSumOfWords(getSumOfWords());
	}

	private void updateAfterRowRemoval(boolean problematicCheckboxSelected,
			RangesRow rowWithTextFields) {
		int rowWithTextFieldsNumber = rowWithTextFields
				.getTextFieldsRowNumber();
		int updateRowNumbersBy = rowWithTextFields.errorNotEmpty() ? -2 : -1;
		updateRowsNumbers(rowWithTextFieldsNumber, updateRowNumbersBy);

		if (getNumberOfRangesRows() == 1) {
			panelUpdater.changeVisibilityOfDeleteButtonInFirstRow(false);
		}
		recalculateSumOfWords(problematicCheckboxSelected);
		panelUpdater.updateSumOfWords(getSumOfWords());
	}

	private void removeRow(RangesRow rowWithTextFields) {
		boolean wasError = rowWithTextFields.errorNotEmpty();
		int rowWithTextFieldsNumber = rowWithTextFields
				.getTextFieldsRowNumber();
		panelUpdater.removeRow(rowWithTextFieldsNumber);
		if (wasError) {
			panelUpdater.removeRow(rowWithTextFieldsNumber);
		}
		rangesRows.remove(rowWithTextFields);
	}

	private void recalculateSumOfWords(boolean problematicWordsSelected) {
		rangesToRepeat = addAllRangesToSet();
		sumOfWords = 0;
		this.sumOfWords += rangesToRepeat.sumRangeInclusive();

		if (problematicWordsSelected) {
			addOrSubtractProblematicWordsFromSum(IncrementSign.PLUS);
		}
	}

	private void validateAndStart(boolean problematicCheckboxSelected) {

		if (isInputEmptyAfterStartButtonPress(problematicCheckboxSelected))
			return;

		createRepeatingInformation(problematicCheckboxSelected);
		panelUpdater.closeLearningStartDialog();
		applicationController.initiateWordsLists(rangesToRepeat,
				problematicCheckboxSelected);
		applicationController.startRepeating();
	}

	private boolean isInputEmptyAfterStartButtonPress(
			boolean problematicCheckboxSelected) {
		rangesToRepeat = addAllRangesToSet();
		if (rangesToRepeat.toString().isEmpty()) {
			makeSureTheresNoInput(problematicCheckboxSelected);
			//TODO do it on "enter key listener", process only the currently
			// focused row
		}
		if (rangesToRepeat.toString().isEmpty()
				&& !problematicCheckboxSelected) {
			panelUpdater
					.showErrorInNewDialog(ExceptionsMessages.NO_INPUT_SUPPLIED);
			return true;
		}
		return false;
	}

	private void makeSureTheresNoInput(boolean problematicCheckboxSelected) {
		for (RangesRow range : rangesRows) {
			processTextFieldsInputAfterKeyRelease(range.getTextFieldTo(),
					range.getTextFieldFrom(), problematicCheckboxSelected);
		}
	}

	private void createRepeatingInformation(boolean problematicCheckboxSelected) {
		String repeatingInformation = "";
		if (problematicCheckboxSelected) {
			repeatingInformation = Labels.PROBLEMATIC_WORDS_OPTION;
			if (rangesToRepeat.getRangesAsList().size() > 0) {
				repeatingInformation += ", ";
			}
		}
		repeatingInformation += rangesToRepeat;
		repeatingInformation += ".";
		applicationController.setRepeatingInformation(
				new RepeatingData(repeatingInformation, LocalDateTime.now(), false));
	}


	private SetOfRanges addAllRangesToSet() {
		SetOfRanges setOfRanges = new SetOfRanges();
		for (RangesRow r : rangesRows) {
			if (!r.getRange().isEmpty()) {
				setOfRanges.addRange(r.getRange());
			}

		}
		return setOfRanges;
	}

	private int getSumOfWords() {
		return sumOfWords;
	}

	private String concatenateErrors() {
		String concatenated = "";
		for (Map.Entry<Integer, String> error : errors.entrySet()) {
			concatenated += String.format(ExceptionsMessages.ERROR_IN_ROW,
					error.getKey()+1, error.getValue());
			concatenated += "\n\n";
		}
		return concatenated;
	}

	private int getNumberOfRangesRows() {
		return rangesRows.size();
	}

	private boolean gotErrors() {
		boolean gotError = false;
		for (RangesRow r : rangesRows) {
			if (r.errorNotEmpty()) {
				errors.put(r.getTextFieldsRowNumber(), r.getError());
				gotError = true;
			}
		}
		return gotError;
	}

	private void showErrorsOrStart(boolean problematicCheckboxSelected) {
		if (gotErrors()) {
			String errors = concatenateErrors();
			panelUpdater.showErrorInNewDialog(errors);
		}
		else {
			validateAndStart(problematicCheckboxSelected);
		}
	}

	public ItemListener createListenerAddProblematicWords(
			AbstractButton problematicWordsCheckbox) {
		return new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean problematicWordsCheckboxSelected = problematicWordsCheckbox
						.isSelected();
				problematicLabelRow = learningStartPanel.getRangesPanel()
						.getNumberOfRows();
				updateSumOfWords(problematicWordsCheckboxSelected);
				addProblematicWordsNotification(
						problematicWordsCheckboxSelected);

			}
		};
	}

	public KeyAdapter createListenerForKeyTyped(
			AbstractButton problematicCheckbox, JTextComponent from,
			JTextComponent to) {
		return new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				inputValidation.
						validateTypedKey(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				handleKeyReleased(e, to, from,
						problematicCheckbox.isSelected());
			}

		};
	}

	public AbstractAction createActionDeleteRow(
			AbstractButton problematicCheckbox, JTextComponent from,
			JTextComponent to) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RangesRow rowWithTextFields = findRowWithTextFields(from, to);
				removeRow(rowWithTextFields);
				updateAfterRowRemoval(problematicCheckbox.isSelected(),
						rowWithTextFields);
			}
		};
	}

	public AbstractAction createActionAddRow() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panelUpdater.createRangeRow();
			}
		};
	}

	public AbstractAction createActionStartLearning(
			AbstractButton problematicCheckbox) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showErrorsOrStart(problematicCheckbox.isSelected());
			}
		};
	}

	public AbstractAction createActionSelectProblematicCheckbox(
			AbstractButton problematicCheckbox) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (problematicCheckbox.isEnabled()) {
					problematicCheckbox
							.setSelected(!problematicCheckbox.isSelected());
				}
			}
		};
	}

	public void enableOrDisableProblematicCheckbox(
			AbstractButton problematicCheckbox) {
		if (getProblematicWordsNumber() == 0) {
			problematicCheckbox.setEnabled(false);
		}
	}

	public void addRowToRangesPanel(JTextComponent fieldFrom,
			JTextComponent fieldTo, AbstractSimpleRow newRow) {
		boolean problematicCheckboxSelected = learningStartPanel
				.getProblematicWordsCheckbox().isSelected();
		int nextRowNumber = learningStartPanel.getRangesPanel()
				.getNumberOfRows();
		if (problematicCheckboxSelected) {
			nextRowNumber -= 1;
		}
		addRow(nextRowNumber, fieldFrom, fieldTo);

		if (problematicCheckboxSelected) {
			problematicLabelRow++;
			panelUpdater.insertRangeRow(nextRowNumber, newRow);
		}
		else {
			panelUpdater.addRangeRow(newRow);
		}

	}

	public void updateAfterAddingRangesRow(JScrollPane scrollPane,
			JTextComponent fieldFrom) {
		panelUpdater.changeVisibilityOfDeleteButtonInFirstRow(
				getNumberOfRangesRows() != 1);
		panelUpdater.scrollRangesPanelToBottom(scrollPane);
		fieldFrom.requestFocusInWindow();

	}

}

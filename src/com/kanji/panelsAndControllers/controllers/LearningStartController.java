package com.kanji.panelsAndControllers.controllers;

import com.guimaker.row.AbstractSimpleRow;
import com.kanji.constants.enums.IncrementSign;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.constants.strings.Labels;
import com.kanji.constants.strings.Prompts;
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
	private ApplicationController applicationController;
	private LearningStartPanelInputValidation inputValidation;
	private LearningStartPanelUpdater panelUpdater;
	private AbstractButton problematicWordsCheckbox;

	public LearningStartController(int numberOfWords,
			ApplicationController applicationController,
			LearningStartPanel learningStartPanel) {
		this.applicationController = applicationController;
		this.learningStartPanel = learningStartPanel;
		rangesRows = new ArrayList<>();
		errors = new HashMap<>();
		inputValidation = new LearningStartPanelInputValidation(numberOfWords);
		panelUpdater = new LearningStartPanelUpdater(learningStartPanel);
	}

	private void updateSumOfWords() {
		rangesToRepeat = addAllRangesToSet();
		IncrementSign incrementSign = problematicWordsCheckbox.isSelected() ?
				IncrementSign.PLUS :
				IncrementSign.MINUS;
		addOrSubtractProblematicWordsFromSum(incrementSign);
		panelUpdater.updateSumOfWords(getSumOfWords());
	}

	private void addProblematicWordsNotification() {
		if (problematicWordsCheckbox.isSelected()) {
			panelUpdater.addProblematicWordsNotification();
		}
		else {
			panelUpdater.removeProblematicWordsNotification();
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

	public void addRow(JTextComponent from, JTextComponent to) {
		RangesRow rangesRow = new RangesRow(from, to);
		rangesRows.add(rangesRow);
	}

	private void removeError(RangesRow rangesRow) {
		int rowNumber = learningStartPanel.getIndexOfRangesRow(rangesRow);
		errors.remove(rowNumber);
		panelUpdater.removeRow(rowNumber + 1);
		rangesRow.setError("");
	}

	private void showError(RangesRow rangesRow, String error) {
		int rowNumber = learningStartPanel.getIndexOfRangesRow(rangesRow);
		panelUpdater.showErrorRow(error, rowNumber + 1);
		panelUpdater.scrollRangesPanelToRow(rowNumber + 1);
		rangesRow.setError(error);
	}

	private RangesRow findRowWithTextFields(JTextComponent textFieldFrom,
			JTextComponent textFieldTo) {
		for (RangesRow row : rangesRows) {
			if (row.gotTextFields(textFieldFrom, textFieldTo)) {
				return row;
			}
		}
		throw new IllegalArgumentException(
				"Row with textfields not found: " + "" + textFieldFrom.getText()
						+ ", " + textFieldTo.getText());
	}

	private void handleKeyReleased(KeyEvent e, JTextComponent to,
			JTextComponent from) {
		if (from.getText().isEmpty() || to.getText().isEmpty()) {
			if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
				resetRangeForRowAndUpdateSum(to, from);
			}
		}
		else {
			processTextFieldsInputAfterKeyRelease(to, from);
		}
	}

	private void resetRangeForRowAndUpdateSum(JTextComponent to,
			JTextComponent from) {
		RangesRow rowWithTextFields = findRowWithTextFields(from, to);
		rowWithTextFields.setRangeValues(0, 0);
		updateNumberOfSelectedWords();
	}

	private void processTextFieldsInputAfterKeyRelease(JTextComponent to,
			JTextComponent from) {
		Component focusedComponent = learningStartPanel.getDialog()
				.getContainer().getFocusOwner();
		int valueFrom = Integer.parseInt(from.getText());
		int valueTo = Integer.parseInt(to.getText());
		String error = inputValidation.validateRangesInput(valueFrom, valueTo);
		RangesRow rowWithTextFields = findRowWithTextFields(from, to);

		if (rowWithTextFields.hasError()) {
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
		updateNumberOfSelectedWords();
	}

	private void updateNumberOfSelectedWords() {
		recalculateSumOfWords();
		panelUpdater.updateSumOfWords(getSumOfWords());
	}

	private void updateAfterRowRemoval() {
		if (getNumberOfRangesRows() == 1) {
			panelUpdater.changeEnabledStateOfDeleteButtonInFirstRow(false);
		}
		recalculateSumOfWords();
		panelUpdater.updateSumOfWords(getSumOfWords());
	}

	private void removeRow(RangesRow rangesRow) {
		boolean wasError = rangesRow.hasError();
		int rowWithTextFieldsNumber = learningStartPanel
				.getIndexOfRangesRow(rangesRow);
		panelUpdater.removeRow(rowWithTextFieldsNumber);
		if (wasError) {
			panelUpdater.removeRow(rowWithTextFieldsNumber);
		}
		rangesRows.remove(rangesRow);
	}

	private void recalculateSumOfWords() {
		rangesToRepeat = addAllRangesToSet();
		sumOfWords = 0;
		this.sumOfWords += rangesToRepeat.sumRangeInclusive();

		if (problematicWordsCheckbox.isSelected()) {
			addOrSubtractProblematicWordsFromSum(IncrementSign.PLUS);
		}
	}

	private void validateAndStart() {

		if (isInputEmptyAfterStartButtonPress())
			return;

		createRepeatingInformation();
		panelUpdater.closeLearningStartDialog();
		applicationController.initiateWordsLists(rangesToRepeat,
				problematicWordsCheckbox.isSelected());
		applicationController.startRepeating();
	}

	private boolean isInputEmptyAfterStartButtonPress() {
		rangesToRepeat = addAllRangesToSet();
		if (rangesToRepeat.toString().isEmpty()) {
			makeSureTheresNoInput();
			//TODO do it on "enter key listener", process only the currently
			// focused row
		}
		if (rangesToRepeat.toString().isEmpty() && !problematicWordsCheckbox
				.isSelected()) {
			panelUpdater
					.showErrorInNewDialog(ExceptionsMessages.NO_INPUT_SUPPLIED);
			return true;
		}
		return false;
	}

	private void makeSureTheresNoInput() {
		for (RangesRow range : rangesRows) {
			JTextComponent textFieldTo = range.getTextFieldTo();
			JTextComponent textFieldFrom = range.getTextFieldFrom();
			if (textFieldFrom.getText().isEmpty() || textFieldTo.getText()
					.isEmpty()) {
				continue;
			}
			processTextFieldsInputAfterKeyRelease(textFieldTo, textFieldFrom);
		}
	}

	private void createRepeatingInformation() {
		String repeatingInformation = "";
		if (problematicWordsCheckbox.isSelected()) {
			repeatingInformation = Labels.PROBLEMATIC_WORDS_OPTION;
			if (rangesToRepeat.getRangesAsList().size() > 0) {
				repeatingInformation += ", ";
			}
		}
		repeatingInformation += rangesToRepeat;
		repeatingInformation += ".";
		applicationController.setRepeatingInformation(
				new RepeatingData(repeatingInformation, LocalDateTime.now(),
						false));
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
		for (RangesRow rangesRow : rangesRows) {
			if (!rangesRow.hasError()) {
				continue;
			}
			int rowNumber = learningStartPanel.getIndexOfRangesRow(rangesRow);
			concatenated += String
					.format(ExceptionsMessages.ERROR_IN_ROW, rowNumber + 1,
							rangesRow.getError());
			concatenated += "\n\n";
		}
		return concatenated;
	}

	private int getNumberOfRangesRows() {
		return rangesRows.size();
	}

	private void showErrorsOrStart() {
		String errors = concatenateErrors();
		if (errors.isEmpty()) {
			validateAndStart();
		}
		else {
			panelUpdater.showErrorInNewDialog(errors);
		}
	}

	public ItemListener createListenerAddProblematicWords(
			AbstractButton problematicWordsCheckbox) {
		this.problematicWordsCheckbox = problematicWordsCheckbox;
		return new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				updateSumOfWords();
				addProblematicWordsNotification();

			}
		};
	}

	public KeyAdapter createListenerForKeyTyped(JTextComponent from,
			JTextComponent to) {
		return new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				inputValidation.validateTypedKey(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				handleKeyReleased(e, to, from);
			}

		};
	}

	public AbstractAction createActionDeleteRow(JTextComponent from,
			JTextComponent to) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RangesRow rowWithTextFields = findRowWithTextFields(from, to);
				removeRow(rowWithTextFields);
				updateAfterRowRemoval();
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

	public AbstractAction createActionStartLearning() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showErrorsOrStart();
			}
		};
	}

	public AbstractAction createActionSelectProblematicCheckbox() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (problematicWordsCheckbox.isEnabled()) {
					problematicWordsCheckbox.setSelected(
							!problematicWordsCheckbox.isSelected());
				}
			}
		};
	}

	public void enableOrDisableProblematicCheckbox() {
		if (getProblematicWordsNumber() == 0) {
			problematicWordsCheckbox.setEnabled(false);
		}
	}

	public void addRowToRangesPanel(JTextComponent fieldFrom,
			JTextComponent fieldTo, AbstractSimpleRow newRow) {
		boolean problematicCheckboxSelected = problematicWordsCheckbox
				.isSelected();
		int nextRowNumber = learningStartPanel.getRangesPanel()
				.getNumberOfRows();
		if (problematicCheckboxSelected) {
			nextRowNumber -= 1;
		}
		addRow(fieldFrom, fieldTo);

		if (problematicCheckboxSelected) {
			panelUpdater.insertRangeRow(nextRowNumber, newRow);
		}
		else {
			panelUpdater.addRangeRow(newRow);
		}

	}

	public void updateAfterAddingRangesRow(JScrollPane scrollPane,
			JTextComponent fieldFrom) {
		panelUpdater.changeEnabledStateOfDeleteButtonInFirstRow(
				getNumberOfRangesRows() != 1);
		panelUpdater.scrollRangesPanelToBottom(scrollPane);
		fieldFrom.requestFocusInWindow();

	}

	public String getProblematicWordsLabelText() {
		Class wordClass = applicationController.getActiveWordsList()
				.getWordInitializer().initializeElement().getClass();
		if (wordClass.equals(Kanji.class)) {
			return Prompts.PROBLEMATIC_KANJI;
		}
		else {
			return Prompts.PROBLEMATIC_WORDS;
		}
	}

}

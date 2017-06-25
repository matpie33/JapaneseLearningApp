package com.kanji.controllers;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.Calendar;

import javax.swing.JPanel;
import javax.swing.JTextField;

import com.guimaker.panels.MainPanel;
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
	private ApplicationWindow parentFrame;
	private LearningStartPanel learningStartPanel;

	public LearningStartController(MyList<RepeatingList> repeatList, int numberOfWords,
			ApplicationWindow parentFrame, LearningStartPanel learningStartPanel) {
		this.parentFrame = parentFrame;
		this.repeatsList = repeatList;
		this.numberOfWords = numberOfWords;
		this.learningStartPanel = learningStartPanel;
	}

	public void updateProblematicKanjiNumber(boolean isProblematicKanjiCheckboxSelected) {
		int problematicKanjis = getProblematicKanjiNumber();
		if (isProblematicKanjiCheckboxSelected)
			sumOfWords += problematicKanjis;
		else
			sumOfWords -= problematicKanjis;
		System.out.println("should update");

	}

	public int getProblematicKanjiNumber() {
		return parentFrame.getProblematicKanjis().size();
	}

	public void handleKeyTyped(KeyEvent e, JPanel otherPanel, boolean problematicCheckboxSelected,
			int rowNumber) throws Exception {
		// TODO don't give me jpanel, give me data
		if ((e.getKeyChar() == KeyEvent.VK_ENTER)) {
			validateAndStart(otherPanel, problematicCheckboxSelected);
		}
		else if (!(e.getKeyChar() + "").matches("\\d")) {
			e.consume();
			learningStartPanel.showErrorIfNotExists(ExceptionsMessages.valueIsNotNumber, rowNumber);
		}
	}

	public String handleKeyReleased(KeyEvent e, JTextField to, JTextField from,
			MainPanel container) {
		int valueFrom = 0;
		int valueTo = 0;
		if (to.getText().isEmpty() || from.getText().isEmpty())
			return "";
		else {
			valueFrom = Integer.parseInt(from.getText());
			valueTo = Integer.parseInt(to.getText());
		}

		if (valueTo <= valueFrom) {
			return ExceptionsMessages.rangeToValueLessThanRangeFromValue;
		}
		else if (isNumberHigherThanMaximum(valueFrom) || isNumberHigherThanMaximum(valueTo))
			return ExceptionsMessages.rangeValueTooHigh;
		return "";

	}

	private boolean isNumberHigherThanMaximum(int number) {
		return number > numberOfWords;
	}

	public void recalculateSumOfKanji(JPanel container, boolean problematicCheckboxSelected) {
		try {
			SetOfRanges s = validateInputs(container);
			sumOfWords = 0;
			if (problematicCheckboxSelected) {
				sumOfWords += getProblematicKanjiNumber(); // TODO duplicated
															// code
			}
			this.sumOfWords += s.sumRangeInclusive();
			System.out.println(s);
			System.out.println(sumOfWords);

		}
		catch (IllegalArgumentException ex) {
			// We keep the message for untill approve button is clicked
		}

	}

	public void validateAndStart(JPanel panel, boolean problematicCheckboxSelected)
			throws Exception {
		rangesToRepeat = validateInputs(panel);
		addToRepeatsListOrShowError(rangesToRepeat, problematicCheckboxSelected);
		learningStartPanel.switchToRepeatingPanel();
	}

	private void addToRepeatsListOrShowError(SetOfRanges setOfRanges,
			boolean problematicCheckboxSelected) throws Exception {
		if (setOfRanges.toString().isEmpty() && !problematicCheckboxSelected)
			throw new Exception(ExceptionsMessages.noInputSupplied);

		Calendar calendar = Calendar.getInstance();

		RepeatingList l = (RepeatingList) repeatsList.getWords();
		System.out.println("L: " + l);
		String repeatingInfo = "";
		if (problematicCheckboxSelected) {
			repeatingInfo = Options.problematicKanjiOption;
			if (setOfRanges.getRangesAsList().size() > 0) {
				repeatingInfo += ", ";
			}
			else {
				repeatingInfo += ".";
			}
		}
		repeatingInfo += setOfRanges;
		parentFrame.setRepeatingInformation(
				new RepeatingInformation(repeatingInfo, calendar.getTime(), false));
		repeatsList.scrollToBottom();

	}

	public void switchPanels(boolean problematicCheckboxSelected) {
		parentFrame.showCardPanel(ApplicationWindow.LEARNING_PANEL);
		parentFrame.setWordsRangeToRepeat(rangesToRepeat, problematicCheckboxSelected);
	}

	private SetOfRanges validateInputs(JPanel panel) {

		SetOfRanges setOfRanges = new SetOfRanges();
		boolean wasSetModifiedTotally = false;
		for (Component p : panel.getComponents()) {
			JPanel row;
			if (p instanceof JPanel) {
				row = (JPanel) p;
			}
			else
				continue;

			boolean wasSetModifiedInInteration = getRangeFromRowAndAddToSet(row, setOfRanges);
			wasSetModifiedTotally = wasSetModifiedTotally || wasSetModifiedInInteration;

		}

		return setOfRanges;

	}

	public int getSumOfWords() {
		return sumOfWords;
	}

	private boolean getRangeFromRowAndAddToSet(JPanel row, SetOfRanges set)
			throws IllegalArgumentException {
		boolean alteredSet = false;
		int textFieldsCounter = 1;
		int rangeStart = 0;
		int rangeEnd = 0;

		for (Component c : row.getComponents()) {
			if (c instanceof JTextField) {
				if (((JTextField) c).getText().isEmpty())
					break;
				if (textFieldsCounter == 1)
					rangeStart = getValueFromTextField((JTextField) c);
				else
					rangeEnd = getValueFromTextField((JTextField) c);
				textFieldsCounter++;
			}
			if (textFieldsCounter > 2) {
				if (rangeEnd > numberOfWords) {
					throw new IllegalArgumentException("Too much"); // TODO
																	// clean it
				}
				Range r = new Range(rangeStart, rangeEnd);
				alteredSet = set.addRange(r);
				textFieldsCounter = 1;
			}
		}
		return alteredSet;

	}

	private int getValueFromTextField(JTextField textField) {
		return Integer.parseInt(textField.getText());
	}

}

package com.kanji.panelsLogic;

import java.awt.Component;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.kanji.Row.RepeatingList;
import com.kanji.constants.ExceptionsMessages;
import com.kanji.graphicInterface.MainPanel;
import com.kanji.myList.MyList;
import com.kanji.panels.LearningStartPanel;
import com.kanji.range.Range;
import com.kanji.range.SetOfRanges;
import com.kanji.window.BaseWindow;

public class LearningStartLogic {
	
	private LearningStartPanel panel;
	private int sumOfWords;
	private int numberOfWords;
	private MyList<RepeatingList> repeatsList;
	private SetOfRanges setOfRanges;
	
	public LearningStartLogic (LearningStartPanel parent, int numberOfWords, MyList repeatsList){
		this.repeatsList = repeatsList;
		panel=parent;
		sumOfWords=0;
		this.numberOfWords = numberOfWords;
	}
	
	public void addProblematicKanjis(){
		int problematicKanjis = getProblematicKanjiNumber();
		if (panel.getProblematicCheckBox().isSelected())
			sumOfWords += problematicKanjis;
		else
			sumOfWords -= problematicKanjis;
		panel.updateSumOfWords(sumOfWords);
	}
	
	private int getProblematicKanjiNumber() {
		return panel.getParentFrame().getProblematicKanjis().size();		
	}

	public void recalculateSumOfKanji(JPanel container) {
		try {
			SetOfRanges s = validateInputs();
			sumOfWords = 0;
			if (panel.getProblematicCheckBox().isSelected()) {
				sumOfWords += getProblematicKanjiNumber(); // TODO duplicated
															// code
			}
			this.sumOfWords += s.sumRangeInclusive();
			panel.updateSumOfWords(sumOfWords);
			System.out.println("Suma: "+sumOfWords);

		} catch (IllegalArgumentException ex) {
			// We keep the message for untill approve button is clicked
		}

	}
	
	public SetOfRanges validateInputs() {

		setOfRanges = new SetOfRanges();
		JPanel panel = this.panel.getRangesPanel().getPanel();
		boolean wasSetModifiedTotally = false;
		for (Component p : panel.getComponents()) {
			JPanel row;
			if (p instanceof JPanel) {
				row = (JPanel) p;
			} 
			else continue;
			
			boolean wasSetModifiedInInteration = getRangeFromRowAndAddToSet(row, setOfRanges);
			wasSetModifiedTotally = wasSetModifiedTotally || wasSetModifiedInInteration;

		}

		return setOfRanges;
	}
	
	private boolean getRangeFromRowAndAddToSet(JPanel row, SetOfRanges set) throws IllegalArgumentException {
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
					panel.getParentFrame().showMessageDialog(ExceptionsMessages.rangeValueTooHigh, true);
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
	
	public boolean isNumberHigherThanMaximum(int number) {
		return number > numberOfWords;
	}

	public void deleteRow(MainPanel container, JPanel rowToDelete) {

		container.removeRow(rowToDelete);
		if (container.getNumberOfRows() == 1) {
			JPanel firstRow = container.getRows().get(0);
			for (Component c : firstRow.getComponents()) {
				if (c instanceof JButton)
					firstRow.remove(c);
			}
		}
		recalculateSumOfKanji(container.getPanel());

	}
	
	public void addToRepeatsListOrShowError() {
		if (setOfRanges.getRangesAsString().isEmpty()){
			panel.getParentFrame().showMessageDialog(ExceptionsMessages.noInputSupplied, true);
		}

		Calendar calendar = Calendar.getInstance();
		RepeatingList l = (RepeatingList) repeatsList.getWords();
		System.out.println("L: " + l);
		repeatsList.getWords().add(setOfRanges.getRangesAsString(), calendar.getTime(), false);
		repeatsList.scrollToBottom();
		BaseWindow parent = panel.getParentFrame();
		parent.showCardPanel(BaseWindow.LEARNING_PANEL);
		parent.setWordsRangeToRepeat(setOfRanges, panel.getProblematicCheckBox().isSelected());
		parent.getNewDialog().dispose();

	}
	
	
}

package com.kanji.myList;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.kanji.constants.ExceptionsMessages;
import com.kanji.listSearching.PropertyChecker;
import com.kanji.listSearching.SearchingDirection;
import com.kanji.utilities.ElementMaker;
import com.kanji.windows.DialogWindow;

public class MyList<Word> {
	private List<JPanel> panels;
	private DialogWindow parent;
	private ElementMaker elementsMaker;
	private RowsCreator<Word> rowCreator;
	private ListWordsController<Word> listController;

	public MyList(DialogWindow parentDialog, ElementMaker element, RowsCreator<Word> rowCreator) {

		this.elementsMaker = element;
		this.parent = parentDialog;
		listController = rowCreator.getController();

		rowCreator.setList(this);
		this.rowCreator = rowCreator;
		initiate();

	}

	private void initiate() {
		// this.wordsAndID = new LinkedHashMap<Integer,String>();
		this.panels = new LinkedList<>();
	}

	public boolean addWord(Word word) {
		rowCreator.createRow(word);
		return listController.add(word);

	}

	public boolean addWordsList(List<Word> words) {
		boolean added = false;
		for (Word word : words) {
			added = addWord(word) || added;
		}
		return added;
	}

	public <Property> void findAndHighlightRowBasedOnProperty(
			PropertyChecker<Property, Word> propertyChecker, Property searchedPropertyValue,
			SearchingDirection searchDirection, DialogWindow parentDialog) {
		int rowNumber = findRowNumberBasedOnProperty(propertyChecker, searchedPropertyValue,
				searchDirection, parentDialog);
		if (rowNumber < 0) {
			return;
		}
		rowCreator.highlightRowAndScroll(rowNumber);
		return;
	}

	public <PropertyType> Word findRowBasedOnProperty(
			PropertyChecker<PropertyType, Word> propertyChecker, PropertyType searchedPropertyValue,
			SearchingDirection searchDirection, DialogWindow parentDialog) {
		int rowNumber = findRowNumberBasedOnProperty(propertyChecker, searchedPropertyValue,
				searchDirection, parentDialog);
		return listController.getWordInRow(rowNumber);
	}

	public <PropertyType> int findRowNumberBasedOnProperty(
			PropertyChecker<PropertyType, Word> propertyChecker, PropertyType searchedPropertyValue,
			SearchingDirection searchDirection, DialogWindow parentDialog) {
		int lastRowToSearch = rowCreator.getHighlightedRowNumber();
		int incrementValue = searchDirection.getIncrementationValue();
		for (int rowNumber = lastRowToSearch
				+ incrementValue; rowNumber != lastRowToSearch; rowNumber += incrementValue) {
			if (isRowNumberOutOfRange(rowNumber)) {
				rowNumber = setRowNumberToTheOtherEndOfList(rowNumber);
			}
			else {
				Word word = listController.getWordInRow(rowNumber);
				if (propertyChecker.isPropertyFound(searchedPropertyValue, word)) {
					return rowNumber;
				}
			}
		}
		Word highlightedWord = getHighlightedWord();
		if (propertyChecker.isPropertyFound(searchedPropertyValue, highlightedWord)) {
			parentDialog.showMessageDialog(ExceptionsMessages.wordAlreadyHighlightedException);
		}
		else {
			parentDialog.showMessageDialog(ExceptionsMessages.wordNotFoundMessage);
		}
		return -1;
	}

	private Word getHighlightedWord() {
		int highlightedRow = rowCreator.getHighlightedRowNumber();
		if (highlightedRow < 0) {
			return null;
		}
		Word word = listController.getWordInRow(highlightedRow);
		return word;
	}

	private boolean isRowNumberOutOfRange(int rowNumber) {
		return (rowNumber < 0) || (rowNumber > listController.getNumberOfWords() - 1);
	}

	private int setRowNumberToTheOtherEndOfList(int rowNumber) {
		if (rowNumber < 0) {
			return listController.getNumberOfWords();
		}
		if (rowNumber >= listController.getNumberOfWords()) {
			return -1;
		}
		return rowNumber;
	}

	public void setWords(Word parameters) {
		cleanWords();
		// scrollToBottom();
	}

	public void scrollToBottom() {
		rowCreator.scrollToBottom();
	}

	public void cleanWords() {
		cleanAll();
	}

	private void cleanAll() {
		this.panels.clear();
		System.out.println("clean");
	}

	public void save() {
		this.elementsMaker.save();
	}

	public boolean showMessage(String message) {
		return parent.showConfirmDialog(message);
	}

	public JPanel getPanel() {
		return rowCreator.getPanel();
	}

	public int getNumberOfWords() {
		return listController.getNumberOfWords();
	}

	public List<Word> getWords() {
		return listController.getWords();
	}

	public ListWordsController<Word> getListController() {
		return listController;
	}

	public JScrollPane getScrollPane() {
		return rowCreator.getScrollPane();
	}

	public DialogWindow getParent() {
		return parent;
	}

}

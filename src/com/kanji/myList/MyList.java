package com.kanji.myList;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.kanji.Row.KanjiInformation;
import com.kanji.Row.RepeatingInformation;
import com.kanji.constants.ExceptionsMessages;
import com.kanji.constants.Prompts;
import com.kanji.controllers.ApplicationController;
import com.kanji.listSearching.PropertyManager;
import com.kanji.listSearching.SearchingDirection;
import com.kanji.windows.DialogWindow;

public class MyList<Word> {
	private DialogWindow parent;
	private ApplicationController applicationController;
	private ListWordsController<Word> listController;

	public MyList(DialogWindow parentDialog, ApplicationController applicationController,
			ListRowMaker<Word> listRowMaker, String title) {
		this.applicationController = applicationController;
		this.parent = parentDialog;
		listRowMaker.setList(this);
		listController = new ListWordsController<>(listRowMaker, title, this);
	}

	public boolean addWord(Word word) {
		return listController.add(word);

	}

	public boolean addWordsList(List<Word> words) {
		boolean added = false;
		for (Word word : words) {
			added = addWord(word) || added;
		}
		return added;
	}

	public void highlightRow(int rowNumber) {
		listController.highlightRowAndScroll(rowNumber, false);
	}

	public <Property> void findAndHighlightRowBasedOnPropertyStartingFromHighlightedWord(
			PropertyManager<Property, Word> propertyChecker, Property searchedPropertyValue,
			SearchingDirection searchDirection, DialogWindow parentDialog) {
		int rowNumber = findRowNumberBasedOnPropertyStartingFromHighlightedWord(propertyChecker,
				searchedPropertyValue, searchDirection, parentDialog);
		if (rowNumber < 0) {
			return;
		}
		listController.highlightRowAndScroll(rowNumber, true);
		return;
	}

	public <Property> Word findRowBasedOnPropertyStartingFromHighlightedWord(
			PropertyManager<Property, Word> propertyChecker, Property searchedPropertyValue,
			SearchingDirection searchDirection, DialogWindow parentDialog) {
		int rowNumber = findRowNumberBasedOnPropertyStartingFromHighlightedWord(propertyChecker,
				searchedPropertyValue, searchDirection, parentDialog);
		return listController.getWordInRow(rowNumber);
	}

	private <Property> int findRowNumberBasedOnProperty(
			PropertyManager<Property, Word> propertyChecker, Property searchedPropertyValue,
			SearchingDirection searchDirection, DialogWindow parentDialog,
			boolean startFromBeginningOfList) {
		int lastRowToSearch;
		int incrementValue = searchDirection.getIncrementationValue();
		if (startFromBeginningOfList) {
			lastRowToSearch = 0;
		}
		else {
			lastRowToSearch = listController.getHighlightedRowNumber() + incrementValue;
		}

		int rowNumber = lastRowToSearch;
		do {
			if (isRowNumberOutOfRange(rowNumber)) {
				rowNumber = setRowNumberToTheOtherEndOfList(rowNumber);
			}
			else {
				Word word = listController.getWordInRow(rowNumber);
				if (propertyChecker.isPropertyFound(searchedPropertyValue, word)) {
					return rowNumber;
				}
			}
			rowNumber += incrementValue;
		}
		while (rowNumber != lastRowToSearch);

		Word highlightedWord = getHighlightedWord();
		if (highlightedWord != null
				&& propertyChecker.isPropertyFound(searchedPropertyValue, highlightedWord)) {
			parentDialog.showMessageDialog(ExceptionsMessages.WORD_ALREADY_HIGHLIGHTED_EXCEPTION);
			return listController.getHighlightedRowNumber();
		}
		else {
			parentDialog.showMessageDialog(ExceptionsMessages.WORD_NOT_FOUND_EXCEPTION);
			return -1;
		}
	}

	public <Property> int findRowNumberBasedOnPropertyStartingFromHighlightedWord(
			PropertyManager<Property, Word> propertyChecker, Property searchedPropertyValue,
			SearchingDirection searchDirection, DialogWindow parentDialog) {
		return findRowNumberBasedOnProperty(propertyChecker, searchedPropertyValue, searchDirection,
				parentDialog, false);
	}

	public <Property> int findRowNumberBasedOnPropertyStartingFromBeginningOfList(
			PropertyManager<Property, Word> propertyChecker, Property searchedPropertyValue,
			SearchingDirection searchDirection, DialogWindow parentDialog) {
		return findRowNumberBasedOnProperty(propertyChecker, searchedPropertyValue, searchDirection,
				parentDialog, true);
	}

	public <Property> Word findRowBasedOnPropertyStartingFromBeginningOfList(
			PropertyManager<Property, Word> propertyChecker, Property searchedPropertyValue,
			SearchingDirection searchDirection, DialogWindow parentDialog) {
		int rowNumber = findRowNumberBasedOnProperty(propertyChecker, searchedPropertyValue,
				searchDirection, parentDialog, true);
		return listController.getWordInRow(rowNumber);
	}

	private Word getHighlightedWord() {
		int highlightedRow = listController.getHighlightedRowNumber();
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
		listController.scrollToBottom();
	}

	public void cleanWords() {
		listController.clear();
	}

	public void save() {
		this.applicationController.save();
	}

	public boolean showMessage(String message) {
		return parent.showConfirmDialog(message);
	}

	public JPanel getPanel() {
		return listController.getPanel();
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

	public DialogWindow getParent() {
		return parent;
	}

	public void removeRow(Word word) {
		listController.remove(word);
	}

	public JButton createButtonRemove(Word word) {
		JButton remove = new JButton("-");
		remove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String rowSpecificPrompt = "";
				if (word instanceof KanjiInformation) {
					rowSpecificPrompt = Prompts.KANJI_ROW;
				}
				if (word instanceof RepeatingInformation) {
					rowSpecificPrompt = Prompts.REPEATING_ELEMENT;
				}

				if (!showMessage(String.format(Prompts.DELETE_ELEMENT, rowSpecificPrompt))) {
					return;
				}
				removeRow(word);
				save();
			}
		});
		return remove;
	}

	public <Property> void replaceProperty(PropertyManager<Property, Word> propertyChecker,
			Property oldValue, DialogWindow parentDialog, Property newValue) {
		Word kanjiToChange = findRowBasedOnPropertyStartingFromHighlightedWord(propertyChecker,
				oldValue, SearchingDirection.FORWARD, parentDialog);
		propertyChecker.replaceValueOfProperty(newValue, kanjiToChange);
		listController.getWords();
		save();
	}

	public <Property> boolean isPropertyDefined(PropertyManager<Property, Word> propertyManager,
			Property propertyToCheck) {
		return listController.isPropertyDefined(propertyManager, propertyToCheck);
	}

	public Word getWordInRow(int rowNumber1Based) {
		return listController.getWordInRow(rowNumber1Based);
	}

}

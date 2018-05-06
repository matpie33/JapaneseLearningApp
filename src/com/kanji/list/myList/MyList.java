package com.kanji.list.myList;

import com.guimaker.enums.MoveDirection;
import com.guimaker.listeners.SwitchBetweenInputsFailListener;
import com.guimaker.panels.MainPanel;
import com.kanji.constants.enums.InputGoal;
import com.kanji.constants.enums.MovingDirection;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.listElements.ListElementInitializer;
import com.kanji.model.WordInMyListExistence;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.util.List;

public class MyList<Word extends ListElement> {
	private DialogWindow parent;
	private ApplicationController applicationController;
	private ListWordsController<Word> listController;
	private ListElementInitializer<Word> wordInitializer;
	private Class listElementClass;
	private String title;
	private ListRowCreator<Word> listRowCreator;

	public MyList(DialogWindow parentDialog,
			ApplicationController applicationController,
			ListRowCreator<Word> listRowCreator, String title,
			ListConfiguration listConfiguration,
			ListElementInitializer wordInitializer) {
		this.listRowCreator = listRowCreator;
		this.applicationController = applicationController;
		this.parent = parentDialog;
		listController = new ListWordsController<>(listConfiguration,
				listRowCreator, title, applicationController, wordInitializer);
		this.wordInitializer = wordInitializer;
		this.title = title;
	}

	public MyList(DialogWindow parentDialog,
			ApplicationController applicationController,
			ListRowCreator<Word> listRowCreator, String title,
			ListElementInitializer wordInitializer) {
		this(parentDialog, applicationController, listRowCreator, title,
				new ListConfiguration(), wordInitializer);
	}

	public void addSwitchBetweenInputsFailListener(
			SwitchBetweenInputsFailListener listener) {
		listController.addSwitchBetweenInputsFailListener(listener);
	}

	public ListElementInitializer<Word> getWordInitializer() {
		return wordInitializer;
	}

	public ListRowCreator<Word> getListRowCreator() {
		return listRowCreator;
	}

	public void inheritScrollPane() {
		listController.inheritScrollPane();
	}

	public void scrollToTop() {
		listController.scrollToTop();
	}

	public String getTitle() {
		return title;
	}

	public Class getListElementClass() {
		if (listElementClass == null) {
			listElementClass = createWord().getClass();
		}
		return listElementClass;
	}

	public boolean addWord(Word word) {
		return listController.add(word, InputGoal.EDIT);
	}

	public boolean addWord(Word word, InputGoal inputGoal) {
		return listController.add(word, inputGoal);
	}

	public Word createWord() {
		Word word = wordInitializer.initializeElement();
		listElementClass = word.getClass();
		return word;
	}

	public void highlightRow(int rowNumber) {
		highlightRow(rowNumber, false);
	}

	public void highlightRow(int rowNumber, boolean clearLastHighlightedWord) {
		listController
				.highlightRowAndScroll(rowNumber, clearLastHighlightedWord);
	}

	public <Property> void findAndHighlightRowBasedOnPropertyStartingFromHighlightedWord(
			ListElementPropertyManager<Property, Word> propertyChecker,
			Property searchedPropertyValue, MovingDirection searchDirection) {
		int rowNumber = findRowNumberBasedOnPropertyStartingFromHighlightedWord(
				propertyChecker, searchedPropertyValue, searchDirection);
		if (rowNumber < 0) {
			return;
		}
		listController.highlightRowAndScroll(rowNumber, true);
		return;
	}

	public <Property> Word findRowBasedOnPropertyStartingFromHighlightedWord(
			ListElementPropertyManager<Property, Word> propertyChecker,
			Property searchedPropertyValue, MovingDirection searchDirection) {
		int rowNumber = findRowNumberBasedOnPropertyStartingFromHighlightedWord(
				propertyChecker, searchedPropertyValue, searchDirection);
		if (rowNumber != -1) {
			listController.getWordInRow(rowNumber);
		}
		return null;
	}

	private <Property> int findRowNumberBasedOnProperty(
			ListElementPropertyManager<Property, Word> propertyChecker,
			Property searchedPropertyValue, MovingDirection searchDirection,
			boolean checkHighlightedWordToo, boolean displayMessage) {

		int lastRowToSearch = 0;
		int incrementValue = searchDirection.getIncrementationValue();
		if (!checkHighlightedWordToo) {
			lastRowToSearch = listController.getHighlightedRowNumber() >= 0 ?
					listController.getHighlightedRowNumber() :
					0;
		}

		int rowNumber = checkHighlightedWordToo ?
				0 :
				listController.getHighlightedRowNumber() >= 0 ?
						lastRowToSearch + incrementValue :
						0;
		boolean shouldContinueSearching;
		do {
			if (isRowNumberOutOfRange(rowNumber)) {
				rowNumber = setRowNumberToTheOtherEndOfList(rowNumber);
			}
			else {
				Word word = listController.getWordInRow(rowNumber);
				if (propertyChecker
						.isPropertyFound(searchedPropertyValue, word)) {
					return rowNumber;
				}
			}
			rowNumber += incrementValue;
			shouldContinueSearching = checkHighlightedWordToo ?
					rowNumber < listController.getNumberOfWords() :
					rowNumber != lastRowToSearch;
		}
		while (shouldContinueSearching);

		Word highlightedWord = getHighlightedWord();
		if (!checkHighlightedWordToo && highlightedWord != null
				&& propertyChecker
				.isPropertyFound(searchedPropertyValue, highlightedWord)) {
			if (displayMessage) {
				parent.showMessageDialog(
						ExceptionsMessages.WORD_ALREADY_HIGHLIGHTED_EXCEPTION);
			}

			//TODO do not hardcode the message here - sometimes I don't want to display it
			return listController.getHighlightedRowNumber();
		}
		else {
			if (displayMessage) {
				parent.showMessageDialog(
						ExceptionsMessages.WORD_NOT_FOUND_EXCEPTION);
			}

			return -1;
		}
	}

	public <Property> int findRowNumberBasedOnPropertyStartingFromHighlightedWord(
			ListElementPropertyManager<Property, Word> propertyChecker,
			Property searchedPropertyValue, MovingDirection searchDirection) {
		return findRowNumberBasedOnProperty(propertyChecker,
				searchedPropertyValue, searchDirection, false, true);
	}

	public <Property> Word findRowBasedOnPropertyStartingFromBeginningOfList(
			ListElementPropertyManager<Property, Word> propertyChecker,
			Property searchedPropertyValue, MovingDirection searchDirection,
			boolean displayMessage) {
		int rowNumber = findRowNumberBasedOnProperty(propertyChecker,
				searchedPropertyValue, searchDirection, true, displayMessage);
		if (rowNumber != -1) {
			return listController.getWordInRow(rowNumber);
		}
		else {
			return null;
		}

	}

	public <Property> WordInMyListExistence<Word> doesWordWithPropertyExist(
			Property property,
			ListElementPropertyManager<Property, Word> propertyManager,
			Word wordToExclude) {
		for (int indexOfWord = 0;
			 indexOfWord < getWords().size(); indexOfWord++) {
			Word word = getWords().get(indexOfWord);
			if (word == wordToExclude) {
				continue;
			}
			if (propertyManager.isPropertyFound(property, word)) {
				return new WordInMyListExistence<>(true, word, indexOfWord + 1);
			}
		}
		return new WordInMyListExistence<Word>(false, null, -1);
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
		return (rowNumber < 0) || (rowNumber
				> listController.getNumberOfWords() - 1);
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

	public void scrollToBottom() {
		listController.scrollToBottom();
	}

	public void cleanWords() {
		listController.clear();
	}

	public void save() {
		this.applicationController.saveProject();
	}

	public JPanel getPanel() {

		return listController.getPanel();
	}

	public int getNumberOfWords() {
		return listController.getNumberOfWords();
	}

	public boolean areAllWordsHighlighted() {
		return listController.getWordsByHighlight(true).size()
				== getNumberOfWords();
	}

	public List<Word> getWords() {
		return listController.getWords();
	}

	public int get1BasedRowNumberOfWord(Word word) {
		return getWords().indexOf(word) + 1;
	}

	public WordInMyListExistence<Word> isWordDefined(Word word) {
		return listController.isWordDefined(word);
	}

	public Word getWordInRow(int rowNumber1Based) {
		return listController.getWordInRow(rowNumber1Based - 1);
	}

	public List<Word> getHighlightedWords() {
		return listController.getWordsByHighlight(true);
	}

	public List<Word> getNotHighlightedWords() {
		return listController.getWordsByHighlight(false);
	}

	public int getMaximumDisplayedWords() {
		return listController.getMaximumWordsToShow();
	}

	public void showWordsStartingFromRow(int firstRowToLoad) {
		listController.showWordsStartingFromRow(firstRowToLoad);
	}

	public void clearHighlightedWords() {
		listController.clearHighlightedWords();
	}

	public boolean hasSelectedInput() {
		return listController.getRowWithSelectedInput() != null;
	}

	public MainPanel getPanelWithSelectedInput() {
		return listController.getPanelWithSelectedInput();
	}

	public void selectNextInputInSameRow() {
		getPanelWithSelectedInput().selectNextInputInSameRow();
	}

	public void selectPreviousInputInSameRow() {
		getPanelWithSelectedInput().selectPreviousInputInSameRow();
	}

	public void selectInputBelowCurrent() {
		listController.selectPanelBelowOrAboveSelected(MoveDirection.BELOW);
	}

	public void selectInputAboveCurrent() {
		listController
				.selectPanelBelowOrAboveSelected(MoveDirection.ABOVE);
	}

	public JTextComponent getSelectedInput() {
		return getPanelWithSelectedInput().getSelectedInput();
	}
}

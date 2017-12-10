package com.kanji.myList;

import java.util.List;
import java.util.Map;

import javax.swing.*;

import com.kanji.enums.ListElementType;
import com.kanji.listElements.*;
import com.kanji.strings.ExceptionsMessages;
import com.kanji.controllers.ApplicationController;
import com.kanji.listSearching.PropertyManager;
import com.kanji.enums.SearchingDirection;
import com.kanji.windows.DialogWindow;

public class MyList<Word extends ListElement> {
	private DialogWindow parent;
	private ApplicationController applicationController;
	private ListWordsController<Word> listController;
	private JPanel parentPanel;
	private List<ListElementData<Word>> listElementData;
	private ListElementInitializer<Word> wordInitializer;

	public MyList(DialogWindow parentDialog, ApplicationController applicationController,
			ListRowMaker<Word> listRowMaker, String title, boolean enableWordAdding,
			List<ListElementData<Word>> listElementData, ListElementInitializer wordInitializer) {
		this.applicationController = applicationController;
		this.parent = parentDialog;
		parentPanel = new JPanel();
		this.listElementData = listElementData;
		listController = new ListWordsController<>(this, enableWordAdding, listRowMaker, parentPanel, title, applicationController);
		this.wordInitializer = wordInitializer;
	}

	public boolean addWord(Word word) {
		return listController.add(word);
	}

	public Word createWord (){
		return wordInitializer.initializeElement();
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
			SearchingDirection searchDirection) {
		int rowNumber = findRowNumberBasedOnPropertyStartingFromHighlightedWord(propertyChecker,
				searchedPropertyValue, searchDirection);
		if (rowNumber < 0) {
			return;
		}
		listController.highlightRowAndScroll(rowNumber, true);
		return;
	}

	public <Property> Word findRowBasedOnPropertyStartingFromHighlightedWord(
			PropertyManager<Property, Word> propertyChecker, Property searchedPropertyValue,
			SearchingDirection searchDirection) {
		int rowNumber = findRowNumberBasedOnPropertyStartingFromHighlightedWord(propertyChecker,
				searchedPropertyValue, searchDirection);
		return listController.getWordInRow(rowNumber);
	}

	private <Property> int findRowNumberBasedOnProperty(
			PropertyManager<Property, Word> propertyChecker, Property searchedPropertyValue,
			SearchingDirection searchDirection, boolean checkHighlightedWordToo) {

		int lastRowToSearch = 0;
		int incrementValue = searchDirection.getIncrementationValue();
		if (!checkHighlightedWordToo) {
			lastRowToSearch = listController.getHighlightedRowNumber() >= 0? listController.getHighlightedRowNumber(): 0 ;
		}

		int rowNumber = checkHighlightedWordToo? 0: listController.getHighlightedRowNumber() >= 0 ? lastRowToSearch + incrementValue: 0;
		boolean shouldContinueSearching;
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
			shouldContinueSearching = checkHighlightedWordToo? rowNumber<listController.getNumberOfWords(): rowNumber != lastRowToSearch;
		}
		while (shouldContinueSearching);

		Word highlightedWord = getHighlightedWord();
		if (!checkHighlightedWordToo && highlightedWord != null
				&& propertyChecker.isPropertyFound(searchedPropertyValue, highlightedWord)) {
			parent.showMessageDialog(ExceptionsMessages.WORD_ALREADY_HIGHLIGHTED_EXCEPTION);
			return listController.getHighlightedRowNumber();
		}
		else{
			parent.showMessageDialog(ExceptionsMessages.WORD_NOT_FOUND_EXCEPTION);
			return -1;
		}
	}

	public <Property> int findRowNumberBasedOnPropertyStartingFromHighlightedWord(
			PropertyManager<Property, Word> propertyChecker, Property searchedPropertyValue,
			SearchingDirection searchDirection) {
		return findRowNumberBasedOnProperty(propertyChecker, searchedPropertyValue, searchDirection,
				 false);
	}

	public <Property> Word findRowBasedOnPropertyStartingFromBeginningOfList(
			PropertyManager<Property, Word> propertyChecker, Property searchedPropertyValue,
			SearchingDirection searchDirection) {
		int rowNumber = findRowNumberBasedOnProperty(propertyChecker, searchedPropertyValue,
				searchDirection, true);
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

	public boolean areAllWordsHighlighted (){
		return listController.getWordsByHighlight(true).size() == getNumberOfWords();
	}

	public List<Word> getWords() {
		return listController.getWords();
	}

	public ListWordsController<Word> getListController() {
		return listController;
	}

	public <Property> void replaceProperty(PropertyManager<Property, Word> propertyChecker,
			Property oldValue, Property newValue) {
		Word kanjiToChange = findRowBasedOnPropertyStartingFromBeginningOfList(propertyChecker,
				oldValue, SearchingDirection.FORWARD);
		propertyChecker.replaceValueOfProperty(newValue, kanjiToChange);
		listController.getWords();
		save();
	}

	public <Property> boolean isPropertyDefined(PropertyManager<Property, Word> propertyManager,
			Property propertyToCheck) {
		return listController.isPropertyDefined(propertyManager, propertyToCheck);
		//TODO remove this method
	}

	public boolean isWordDefined (Word word){
		return listController.isWordDefined(word);
	}

	public Word getWordInRow(int rowNumber1Based) {
		return listController.getWordInRow(rowNumber1Based);
	}

	public List<Word> getHighlightedWords(){
		return listController.getWordsByHighlight(true);
	}

	public List<Word> getNotHighlightedWords(){
		return listController.getWordsByHighlight(false);
	}

	public List <ListElementData<Word>> getListElementData (){
		return listElementData;
	}


}

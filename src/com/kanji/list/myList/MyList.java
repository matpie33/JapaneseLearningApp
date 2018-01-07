package com.kanji.list.myList;

import java.util.List;

import javax.swing.*;

import com.kanji.list.listElements.*;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.constants.enums.SearchingDirection;
import com.kanji.windows.DialogWindow;

public class MyList<Word extends ListElement> {
	private DialogWindow parent;
	private ApplicationController applicationController;
	private ListWordsController<Word> listController;
	private List<ListElementData<Word>> listElementData;
	private ListElementInitializer<Word> wordInitializer;
	private Class listElementClass;
	private String title;

	public MyList(DialogWindow parentDialog, ApplicationController applicationController,
			ListRowMaker<Word> listRowMaker, String title, boolean enableWordAdding,
			List<ListElementData<Word>> listElementData, ListElementInitializer wordInitializer) {
		this.applicationController = applicationController;
		this.parent = parentDialog;
		this.listElementData = listElementData;
		listController = new ListWordsController<>(this, enableWordAdding, listRowMaker, title, applicationController);
		this.wordInitializer = wordInitializer;
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public Class getListElementClass (){
		if (listElementClass == null){
			listElementClass = createWord().getClass();
		}
		return listElementClass;
	}

	public boolean addWord(Word word) {
		return listController.add(word);
	}

	public Word createWord (){
		Word word = wordInitializer.initializeElement();
		listElementClass = word.getClass();
		return word;
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
			ListElementPropertyManager<Property, Word> propertyChecker, Property searchedPropertyValue,
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
			ListElementPropertyManager<Property, Word> propertyChecker, Property searchedPropertyValue,
			SearchingDirection searchDirection) {
		int rowNumber = findRowNumberBasedOnPropertyStartingFromHighlightedWord(propertyChecker,
				searchedPropertyValue, searchDirection);
		return listController.getWordInRow(rowNumber);
	}

	private <Property> int findRowNumberBasedOnProperty(
			ListElementPropertyManager<Property, Word> propertyChecker, Property searchedPropertyValue,
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
			ListElementPropertyManager<Property, Word> propertyChecker, Property searchedPropertyValue,
			SearchingDirection searchDirection) {
		return findRowNumberBasedOnProperty(propertyChecker, searchedPropertyValue, searchDirection,
				 false);
	}

	public <Property> Word findRowBasedOnPropertyStartingFromBeginningOfList(
			ListElementPropertyManager<Property, Word> propertyChecker, Property searchedPropertyValue,
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

	public <Property> void replaceProperty(ListElementPropertyManager<Property, Word> propertyChecker,
			Property oldValue, Property newValue) {
		Word kanjiToChange = findRowBasedOnPropertyStartingFromBeginningOfList(propertyChecker,
				oldValue, SearchingDirection.FORWARD);
		propertyChecker.replaceValueOfProperty(newValue, kanjiToChange);
		listController.getWords();
		save();
	}

	public <Property> boolean isPropertyDefined(ListElementPropertyManager<Property, Word> listElementPropertyManager,
			Property propertyToCheck) {
		return listController.isPropertyDefined(listElementPropertyManager, propertyToCheck);
		//TODO remove this method
	}

	public boolean isWordDefined (Word word){
		return listController.isWordDefined(word);
	}

	public Word getWordInRow(int rowNumber1Based) {
		return listController.getWordInRow(rowNumber1Based-1);
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
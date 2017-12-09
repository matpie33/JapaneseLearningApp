package com.kanji.controllers;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import com.kanji.listElements.KanjiInformation;
import com.kanji.listElements.ListElement;
import com.kanji.listElements.ListElementFactory;
import com.kanji.listSearching.PropertyManager;
import com.kanji.strings.ExceptionsMessages;
import com.kanji.listSearching.KanjiIdChecker;
import com.kanji.myList.MyList;
import com.kanji.windows.DialogWindow;

import java.awt.event.ActionEvent;
import java.util.Map;

public class InsertWordController<Word extends ListElement> {

	private MyList<Word> list;
	private DialogWindow parentDialog;
	private ApplicationController applicationController;

	public InsertWordController(MyList<Word> list,
			ApplicationController applicationController) {
		this.list = list;
		this.applicationController = applicationController;
	}

	public void setParentDialog(DialogWindow parent) {
		parentDialog = parent;
	}

	private void validateAndAddWordIfValid(
			Map<JTextComponent, PropertyManager> inputToPropertyManager) {
		Word word = list.createWord();
		boolean allInputsValid = true;
		for (Map.Entry<JTextComponent, PropertyManager> entry: inputToPropertyManager.entrySet()){
			PropertyManager propertyManager  = entry.getValue();
			JTextComponent textComponent = entry.getKey();
			allInputsValid = propertyManager.tryToReplacePropertyWithValueFromInput(
					textComponent, word);
			if (!allInputsValid){
				textComponent.selectAll();
				textComponent.requestFocusInWindow();
				break;
			}
		}
		if (allInputsValid){
			boolean isItNewWord = addWordToList(word);
			if (isItNewWord){
				applicationController.saveProject();
			}
		}
	}

	private boolean isIdValidNumber(String number) {
		boolean valid = number.matches("\\d+");

		if (!valid)
			parentDialog.showMessageDialog(ExceptionsMessages.NUMBER_FORMAT_EXCEPTION);
		return valid;
	}

	private boolean addWordToList(Word word) {
		boolean addedWord = !list.isWordDefined(word);
		if (addedWord) {
			list.addWord(word);
			list.scrollToBottom();


		}
		else {
			parentDialog.showMessageDialog(ExceptionsMessages.WORD_ALREADY_DEFINED_EXCEPTION);
		}
		return addedWord;
	}

	public AbstractAction createActionValidateAndAddWord (
			Map<JTextComponent, PropertyManager> inputToPropertyManager){
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				validateAndAddWordIfValid(inputToPropertyManager);
			}
		};
	}

}

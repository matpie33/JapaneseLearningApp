package com.kanji.panelsAndControllers.controllers;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import com.kanji.list.listElements.ListElement;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.list.myList.MyList;
import com.kanji.windows.DialogWindow;

import java.awt.event.ActionEvent;
import java.util.Map;

public class InsertKanjiController<Word extends ListElement> {

	private MyList<Word> list;
	private DialogWindow parentDialog;
	private ApplicationController applicationController;

	public InsertKanjiController(MyList<Word> list,
			ApplicationController applicationController) {
		this.list = list;
		this.applicationController = applicationController;
	}

	public void setParentDialog(DialogWindow parent) {
		parentDialog = parent;
	}

	private void validateAndAddWordIfValid(
			Map<JTextComponent, ListElementPropertyManager> inputToPropertyManager) {
		Word word = list.createWord();
		boolean allInputsValid = true;
		for (Map.Entry<JTextComponent, ListElementPropertyManager> entry: inputToPropertyManager.entrySet()){
			ListElementPropertyManager listElementPropertyManager = entry.getValue();
			JTextComponent component = entry.getKey();
			allInputsValid = listElementPropertyManager.tryToReplacePropertyWithValueFromTextInput(
					component, word);
			if (!allInputsValid){
				component.selectAll();
				component.requestFocusInWindow();
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

	private boolean addWordToList(Word word) {
		boolean addedWord = !list.isWordDefined(word);
		if (addedWord) {
			list.addWord(word);
			list.scrollToBottom();


		}
		else {
			parentDialog.showMessageDialog(ExceptionsMessages.KANJI_KEYWORD_ALREADY_DEFINED_EXCEPTION);
		}
		return addedWord;
	}

	public AbstractAction createActionValidateAndAddWord (
			Map<JTextComponent, ListElementPropertyManager> inputToPropertyManager){
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				validateAndAddWordIfValid(inputToPropertyManager);
			}
		};
	}

}

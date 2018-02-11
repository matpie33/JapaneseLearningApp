package com.kanji.panelsAndControllers.controllers;

import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.myList.MyList;
import com.kanji.model.WordInMyListExistence;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
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
		for (Map.Entry<JTextComponent, ListElementPropertyManager> entry : inputToPropertyManager
				.entrySet()) {
			ListElementPropertyManager listElementPropertyManager = entry
					.getValue();
			JTextComponent component = entry.getKey();
			allInputsValid = listElementPropertyManager
					.tryToReplacePropertyWithValueFromTextInput(component,
							word);
			if (!allInputsValid) {
				component.selectAll();
				component.requestFocusInWindow();
				break;
			}
		}
		if (allInputsValid) {
			boolean isItNewWord = addWordToList(word);
			if (isItNewWord) {
				applicationController.saveProject();
			}
		}
	}

	private boolean addWordToList(Word word) {
		WordInMyListExistence<Word> wordExistenceInList = list
				.isWordDefined(word);
		if (!wordExistenceInList.exists()) {
			list.addWord(word);
			list.scrollToBottom();

		}
		else {
			parentDialog.showMessageDialog(
					String.format(ExceptionsMessages.WORD_ALREADY_EXISTS,
							list.get1BasedRowNumberOfWord(
									wordExistenceInList.getWord())));
		}
		return !wordExistenceInList.exists();
	}

	public AbstractAction createActionValidateAndAddWord(
			Map<JTextComponent, ListElementPropertyManager> inputToPropertyManager) {
		return new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) {
				validateAndAddWordIfValid(inputToPropertyManager);
			}
		};
	}

}

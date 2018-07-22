package com.kanji.panelsAndControllers.controllers;

import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.listeners.InputValidationListener;
import com.kanji.list.myList.MyList;
import com.kanji.model.PropertyPostValidationData;
import com.kanji.panelsAndControllers.panels.InsertWordPanel;
import com.kanji.utilities.ThreadUtilities;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;

public class InsertWordController<Word extends ListElement>
		implements InputValidationListener<Word> {

	private MyList<Word> list;
	private DialogWindow parentDialog;
	private ApplicationController applicationController;
	private InsertWordPanel<Word> insertWordPanel;
	private boolean addingWordWasRequested = false;

	public InsertWordController(MyList<Word> list,
			ApplicationController applicationController,
			InsertWordPanel insertWordPanel) {
		this.insertWordPanel = insertWordPanel;
		this.list = list;
		this.applicationController = applicationController;
	}

	public void setParentDialog(DialogWindow parent) {
		parentDialog = parent;
	}

	private void addWordIfItsNew(Word word) {
		if (word.isEmpty()) {
			parentDialog
					.showMessageDialog(ExceptionsMessages.NO_INPUT_SUPPLIED);
			return;
		}
		ThreadUtilities.callOnOtherThread(()-> {
			boolean addedWord = addWordToList(word);
			if (addedWord) {
				applicationController.saveProject();
			}
		});
	}

	private boolean addWordToList(Word word) {
		boolean addedWord = list.addWord(word);
		if (addedWord) {
			list.scrollToBottom();
			//TODO remove from this method show message - it should just add word and return boolean
		}
		else {
			list.highlightRow(list.get1BasedRowNumberOfWord(word) - 1, true);
			parentDialog.showMessageDialog(
					String.format(ExceptionsMessages.WORD_ALREADY_EXISTS,
							list.get1BasedRowNumberOfWord(word)), false);
		}
		return addedWord;
	}

	public AbstractAction createActionValidateFocusedElement() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Component focusOwner = FocusManager.getCurrentManager().getFocusOwner();
				if (focusOwner instanceof JTextComponent){
					validateFocusedElement();
					addingWordWasRequested = true;
				}
				else{
					addWord();
				}
			}
		};

	}

	private void validateFocusedElement() {
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.clearGlobalFocusOwner();
	}

	@Override
	public <WordProperty> void inputValidated(
			PropertyPostValidationData<WordProperty, Word> postValidationData) {
		if (addingWordWasRequested && postValidationData.isValid()) {
			addWord();
		}
		addingWordWasRequested = false;
	}

	private void addWord() {
		addWordIfItsNew(insertWordPanel.getWord());
		insertWordPanel.reinitializePanel();
	}
}

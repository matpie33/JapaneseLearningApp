package com.kanji.panelsAndControllers.controllers;

import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.myList.MyList;
import com.kanji.model.WordInMyListExistence;
import com.kanji.panelsAndControllers.panels.InsertWordPanel;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class InsertWordController<Word extends ListElement> {

	private MyList<Word> list;
	private DialogWindow parentDialog;
	private ApplicationController applicationController;
	private InsertWordPanel<Word> insertWordPanel;

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
		boolean addedWord = addWordToList(word);
		if (addedWord) {
			applicationController.saveProject();
		}
	}

	private boolean addWordToList(Word word) {
		WordInMyListExistence<Word> doesWordExistInMyList = list
				.isWordDefined(word);
		if (!doesWordExistInMyList.exists()) {
			list.addWord(word);
			list.scrollToBottom();
			//TODO remove from this method show message - it should just add word and return boolean
		}
		else {
			list.highlightRow(list.get1BasedRowNumberOfWord(
					doesWordExistInMyList.getWord()) - 1, true);
			parentDialog.showMessageDialog(
					String.format(ExceptionsMessages.WORD_ALREADY_EXISTS,
							list.get1BasedRowNumberOfWord(
									doesWordExistInMyList.getWord())));
		}
		return !doesWordExistInMyList.exists();
	}

	public AbstractAction createActionValidateAndAddWord() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(() -> {
					KeyboardFocusManager.getCurrentKeyboardFocusManager()
							.clearGlobalFocusOwner();
					SwingUtilities.invokeLater(() -> {
						addWordIfItsNew(insertWordPanel.getWord());
						insertWordPanel.reinitializePanel();
					});
				});

			}
		};

	}

}

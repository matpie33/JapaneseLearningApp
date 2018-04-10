package com.kanji.panelsAndControllers.controllers;

import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.myList.MyList;
import com.kanji.model.WordInMyListExistence;
import com.kanji.panelsAndControllers.panels.InsertJapaneseWordPanel;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class InsertJapaneseWordController {

	private MyList<JapaneseWordInformation> list;
	private DialogWindow parentDialog;
	private ApplicationController applicationController;
	private InsertJapaneseWordPanel insertJapaneseWordPanel;

	public InsertJapaneseWordController(MyList<JapaneseWordInformation> list,
			ApplicationController applicationController,
			InsertJapaneseWordPanel insertJapaneseWordPanel) {
		this.insertJapaneseWordPanel = insertJapaneseWordPanel;
		this.list = list;
		this.applicationController = applicationController;
	}

	public void setParentDialog(DialogWindow parent) {
		parentDialog = parent;
	}

	private void validateAndAddWordIfValid(
			JapaneseWordInformation japaneseWordInformation) {
		boolean isItNewWord = addWordToList(japaneseWordInformation);
		if (isItNewWord) {
			applicationController.saveProject();
		}
	}

	private boolean addWordToList(JapaneseWordInformation word) {
		WordInMyListExistence<JapaneseWordInformation> doesWordExistInMyList = list
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
						validateAndAddWordIfValid(
								insertJapaneseWordPanel.getWord());
						insertJapaneseWordPanel.reinitializePanel();
					});
				});

			}
		};

	}

}

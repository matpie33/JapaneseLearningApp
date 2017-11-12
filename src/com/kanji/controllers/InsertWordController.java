package com.kanji.controllers;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import com.kanji.listElements.KanjiInformation;
import com.kanji.strings.ExceptionsMessages;
import com.kanji.listSearching.KanjiIdChecker;
import com.kanji.myList.MyList;
import com.kanji.windows.DialogWindow;

import java.awt.event.ActionEvent;

public class InsertWordController {

	private MyList<KanjiInformation> list;
	private DialogWindow parentDialog;
	private ApplicationController applicationController;

	public InsertWordController(MyList<KanjiInformation> list,
			ApplicationController applicationController) {
		this.list = list;
		this.applicationController = applicationController;
	}

	public void setParentDialog(DialogWindow parent) {
		parentDialog = parent;
	}

	public void validateAndAddWordIfValid(JTextComponent numberInputText, JTextComponent wordInputText) {

		String numberInput = numberInputText.getText();
		String wordInput = wordInputText.getText();
		if (isIdValidNumber(numberInput)) {
			int number = Integer.parseInt(numberInput);
			boolean addedWord = addWordToList(wordInput, number);
			if (addedWord) {
				applicationController.saveProject();
				wordInputText.selectAll();
				wordInputText.requestFocusInWindow();
			}
			else {
				numberInputText.selectAll();
				numberInputText.requestFocusInWindow();
			}
		}

	}

	private boolean isIdValidNumber(String number) {
		boolean valid = number.matches("\\d+");

		if (!valid)
			parentDialog.showMessageDialog(ExceptionsMessages.NUMBER_FORMAT_EXCEPTION);
		return valid;
	}

	private boolean addWordToList(String word, int number) {
		boolean addedWord = !list.isPropertyDefined(new KanjiIdChecker(), number);
		if (addedWord) {
			list.addWord(new KanjiInformation(word, number));
			list.scrollToBottom();
		}
		else {
			parentDialog.showMessageDialog(
					String.format(ExceptionsMessages.ID_ALREADY_DEFINED_EXCEPTION, number));
		}
		return addedWord;
	}

	public AbstractAction createActionValidateAndAddWord (JTextComponent insertNumberTextComponent,
			JTextComponent insertWordTextComponent){
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				validateAndAddWordIfValid(insertNumberTextComponent, insertWordTextComponent);
			}
		};
	}

}

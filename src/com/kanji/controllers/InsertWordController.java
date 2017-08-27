package com.kanji.controllers;

import javax.swing.JTextField;

import com.kanji.Row.KanjiInformation;
import com.kanji.constants.ExceptionsMessages;
import com.kanji.myList.MyList;
import com.kanji.windows.DialogWindow;

public class InsertWordController {

	private MyList<KanjiInformation> list;
	private DialogWindow parentDialog;

	public InsertWordController(MyList<KanjiInformation> list) {
		this.list = list;
	}

	public void setParentDialog(DialogWindow parent) {
		parentDialog = parent;
	}

	public void validateAndAddWordIfValid(JTextField numberInputText, JTextField wordInputText) {

		String numberInput = numberInputText.getText();
		String wordInput = wordInputText.getText();
		if (isIdValidNumber(numberInput)) {
			int number = Integer.parseInt(numberInput);
			boolean addedWord = addWordToList(wordInput, number);
			if (addedWord) {
				parentDialog.save();
				wordInputText.selectAll();
				wordInputText.requestFocusInWindow();
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
		boolean addedWord = list.addWord(new KanjiInformation(word, number));
		if (addedWord) {
			list.scrollToBottom();
		}
		return addedWord;
	}

}

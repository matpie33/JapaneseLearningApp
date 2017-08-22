package com.kanji.controllers;

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

	public boolean validateAndAddWordIfValid(String numberInput, String wordInput) {

		if (isIdValidNumber(numberInput)) {
			int number = Integer.parseInt(numberInput);
			boolean addedWord = addWordToList(wordInput, number);
			if (addedWord) {
				parentDialog.save();
			}
			return true;
		}
		return false;

	}

	private boolean isIdValidNumber(String number) {
		boolean valid = number.matches("\\d+");

		if (!valid)
			parentDialog.showMessageDialog(ExceptionsMessages.numberFormatException);
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

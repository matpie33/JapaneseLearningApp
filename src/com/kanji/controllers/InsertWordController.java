package com.kanji.controllers;

import com.kanji.constants.ExceptionsMessages;
import com.kanji.myList.MyList;
import com.kanji.row.KanjiWords;
import com.kanji.windows.DialogWindow;

public class InsertWordController {

	private MyList list;
	private DialogWindow parentDialog;

	public InsertWordController(MyList list) {
		this.list = list;
	}

	public void setParentDialog(DialogWindow parent) {
		parentDialog = parent;
	}

	public boolean validateAndAddWordIfValid(String numberInput, String wordInput) {

		if (isIdValidNumber(numberInput)) {
			int number = Integer.parseInt(numberInput);
			if (isWordAndIdUndefinedYet(wordInput, number)) {
				addWordToList(wordInput, number);
				parentDialog.save();
				return true;
			}
		}
		return false;
	}

	private boolean isIdValidNumber(String number) {
		boolean valid = number.matches("\\d+");

		if (!valid)
			parentDialog.showMsgDialog(ExceptionsMessages.numberFormatException);
		return valid;
	}

	private boolean isWordAndIdUndefinedYet(String word, int number) {
		return (isWordIdUndefinedYet(number) && isWordUndefinedYet(word));
	}

	private boolean isWordIdUndefinedYet(int number) {
		boolean defined = ((KanjiWords) list.getWords()).isIdDefined(number);
		if (defined)
			parentDialog.showMsgDialog(ExceptionsMessages.idAlreadyDefinedException);
		return !defined;
	}

	private boolean isWordUndefinedYet(String word) {
		boolean defined = ((KanjiWords) list.getWords()).isWordDefined(word);
		if (defined)
			parentDialog.showMsgDialog(ExceptionsMessages.wordAlreadyDefinedException);
		return !defined;
	}

	private void addWordToList(String word, int number) {
		((KanjiWords) list.getWords()).addNewRow(word, number);
		list.scrollToBottom();
	}

}

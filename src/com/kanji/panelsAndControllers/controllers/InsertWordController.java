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
			Map<JComponent, ListElementPropertyManager> inputToPropertyManager) {
		Word word = list.createWord();
		boolean allInputsValid = true;
		for (Map.Entry<JComponent, ListElementPropertyManager> entry: inputToPropertyManager.entrySet()){
			ListElementPropertyManager listElementPropertyManager = entry.getValue();
			JComponent component = entry.getKey();
			JTextComponent textComponent = null;
			if (component instanceof JTextComponent){
				textComponent = (JTextComponent) component;
			}
			else if (component instanceof JComboBox){
				JComboBox comboBox = (JComboBox) component;
				textComponent = new JTextField();
				Object selectedComboboxValue = comboBox.getSelectedItem();
				if (selectedComboboxValue instanceof String){
					textComponent.setText((String)selectedComboboxValue);
				}

			}
			allInputsValid = listElementPropertyManager.tryToReplacePropertyWithValueFromTextInput(
					textComponent.getText(), word);
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
			Map<JComponent, ListElementPropertyManager> inputToPropertyManager){
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				validateAndAddWordIfValid(inputToPropertyManager);
			}
		};
	}

}

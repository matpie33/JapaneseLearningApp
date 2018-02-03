package com.kanji.panelsAndControllers.controllers;

import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.list.listElementPropertyManagers.JapaneseWordWritingsChecker;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listRows.RowInJapaneseWordInformations;
import com.kanji.list.myList.MyList;
import com.kanji.model.KanaAndKanjiTextFields;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InsertJapaneseWordController {

	private MyList<JapaneseWordInformation> list;
	private DialogWindow parentDialog;
	private ApplicationController applicationController;
	private RowInJapaneseWordInformations rowInJapaneseWordInformation;

	public InsertJapaneseWordController(RowInJapaneseWordInformations rowInJapaneseWordInformation,
			MyList<JapaneseWordInformation> list,	ApplicationController applicationController) {
		this.rowInJapaneseWordInformation = rowInJapaneseWordInformation;
		this.list = list;
		this.applicationController = applicationController;
	}

	public void setParentDialog(DialogWindow parent) {
		parentDialog = parent;
	}

	private void validateAndAddWordIfValid(
			Map<JTextComponent, List<JTextComponent>> kanaToKanjiWritings,
			Map<JTextComponent, ListElementPropertyManager<?, JapaneseWordInformation>> textsWithPropertyManagers,
			JComboBox partOfSpeech) {
		JapaneseWordInformation japaneseWordInformation =
				JapaneseWordInformation.getInitializer().initializeElement();
		boolean allInputsValid = true;


		for (Map.Entry<JTextComponent, ListElementPropertyManager<?, JapaneseWordInformation>>
				textWithPropertyManager: textsWithPropertyManagers.entrySet()){
			JTextComponent textComponent = textWithPropertyManager.getKey();
			allInputsValid = textWithPropertyManager.getValue()
					.tryToReplacePropertyWithValueFromTextInput(textComponent,
					japaneseWordInformation);
			//TODO this part is common with insert word controller, try to use 1 and create
			// an interface for setting properties done in below loop
			if (!allInputsValid){
				parentDialog.showMessageDialog(ExceptionsMessages.INCORRECT_TEXT_INPUT+
					textWithPropertyManager.getValue().getInvalidPropertyReason() + ".");
				textComponent.selectAll();
				textComponent.requestFocusInWindow();
				break;
			}
		}
		JapaneseWordWritingsChecker writingsChecker = new JapaneseWordWritingsChecker(
				rowInJapaneseWordInformation);
		for (Map.Entry<JTextComponent, List <JTextComponent>> entry:
				kanaToKanjiWritings.entrySet()){
			JTextComponent kanaText = entry.getKey();
			List <JTextComponent> kanjiTexts = entry.getValue();
			List <JTextComponent> allTextFields = new ArrayList<>();
			allTextFields.addAll(kanjiTexts);
			allTextFields.add(kanaText);
			KanaAndKanjiTextFields kanaAndKanjiTextFields = new KanaAndKanjiTextFields(
					kanaText, kanjiTexts);
			writingsChecker.setProperty(japaneseWordInformation, kanaAndKanjiTextFields);

		}
		if (allInputsValid){
			PartOfSpeech partOfSpeechObject = PartOfSpeech.getPartOfSpeachByPolishMeaning(
					(String)partOfSpeech.getSelectedItem()
			);
			japaneseWordInformation.setPartOfSpeech(partOfSpeechObject);
			boolean isItNewWord = addWordToList(japaneseWordInformation);
			if (isItNewWord){
				applicationController.saveProject();
			}
		}
	}

	private boolean addWordToList(JapaneseWordInformation word) {
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

	public AbstractAction createActionValidateAndAddWord(
			Map<JTextComponent, List<JTextComponent>> kanaToKanjiWritings,
			Map<JTextComponent, ListElementPropertyManager<?, JapaneseWordInformation>> textsWithPropertyManagers,
			JComboBox partOfSpeech) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				validateAndAddWordIfValid(kanaToKanjiWritings, textsWithPropertyManagers,
						partOfSpeech);
			}
		};
	}

}

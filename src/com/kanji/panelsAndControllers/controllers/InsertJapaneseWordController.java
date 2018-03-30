package com.kanji.panelsAndControllers.controllers;

import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElementPropertyManagers.JapaneseWordMeaningChecker;
import com.kanji.list.listElementPropertyManagers.JapaneseWordWritingsChecker;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listRows.RowInJapaneseWordInformations;
import com.kanji.list.myList.MyList;
import com.kanji.model.KanaAndKanjiStrings;
import com.kanji.model.WordInMyListExistence;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class InsertJapaneseWordController {

	private MyList<JapaneseWordInformation> list;
	private DialogWindow parentDialog;
	private ApplicationController applicationController;
	private RowInJapaneseWordInformations rowInJapaneseWordInformation;

	public InsertJapaneseWordController(
			RowInJapaneseWordInformations rowInJapaneseWordInformation,
			MyList<JapaneseWordInformation> list,
			ApplicationController applicationController) {
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
		JapaneseWordInformation japaneseWordInformation = JapaneseWordInformation
				.getInitializer().initializeElement();
		boolean allInputsValid = true;

		for (Map.Entry<JTextComponent, ListElementPropertyManager<?, JapaneseWordInformation>> textWithPropertyManager : textsWithPropertyManagers
				.entrySet()) {
			JTextComponent textComponent = textWithPropertyManager.getKey();
			allInputsValid = textWithPropertyManager.getValue()
					.tryToReplacePropertyWithValueFromTextInput(textComponent,
							japaneseWordInformation);
			//TODO use "is property defined" here too, so that we can know, what
			//exactly was duplicated

			//TODO this part is common with insert word controller, try to use 1 and create
			// an interface for setting properties done in below loop
			if (!allInputsValid) {
				parentDialog.showMessageDialog(
						textWithPropertyManager.getValue()
								.getInvalidPropertyReason());
				textComponent.selectAll();
				textComponent.requestFocusInWindow();
				return;
			}

		}
		JapaneseWordWritingsChecker writingsChecker = new JapaneseWordWritingsChecker(
				rowInJapaneseWordInformation.getJapaneseWordPanelCreator(),
				true);
		for (Map.Entry<JTextComponent, List<JTextComponent>> entry : kanaToKanjiWritings
				.entrySet()) {
			JTextComponent kanaText = entry.getKey();
			List<JTextComponent> kanjiTexts = entry.getValue();
			List<JTextComponent> allTextFields = new ArrayList<>();
			allTextFields.addAll(kanjiTexts);
			allTextFields.add(kanaText);
			KanaAndKanjiStrings kanaAndKanjiStrings = new KanaAndKanjiStrings(
					kanaText, kanjiTexts, "", false);
			writingsChecker.setProperty(japaneseWordInformation, Arrays.asList(
					new KanaAndKanjiStrings[] { kanaAndKanjiStrings }));

		}
		if (allInputsValid) {
			PartOfSpeech partOfSpeechObject = PartOfSpeech
					.getPartOfSpeachByPolishMeaning(
							(String) partOfSpeech.getSelectedItem());
			japaneseWordInformation.setPartOfSpeech(partOfSpeechObject);
			boolean isItNewWord = addWordToList(japaneseWordInformation);
			if (isItNewWord) {
				clearTextfieldsAndFocusFirstOne(textsWithPropertyManagers,
						kanaToKanjiWritings);

				applicationController.saveProject();
			}
		}
	}

	private void clearTextfieldsAndFocusFirstOne(
			Map<JTextComponent, ListElementPropertyManager<?, JapaneseWordInformation>> textsWithPropertyManagers,
			Map<JTextComponent, List<JTextComponent>> kanaToKanjiWritings) {
		//TODO seems not the best idea, try to use one map
		for (Map.Entry<JTextComponent, ListElementPropertyManager<?, JapaneseWordInformation>> propertyManagerEntry : textsWithPropertyManagers
				.entrySet()) {
			propertyManagerEntry.getKey().setText("");
			if (propertyManagerEntry.getValue().getClass()
					.equals(JapaneseWordMeaningChecker.class)) {
				SwingUtilities.invokeLater(() -> propertyManagerEntry.getKey()
						.requestFocusInWindow());
			}
		}
		for (Map.Entry<JTextComponent, List<JTextComponent>> kanaToKanjiMap : kanaToKanjiWritings
				.entrySet()) {
			for (JTextComponent textComponent : kanaToKanjiMap.getValue()) {
				textComponent.setText(Prompts.KANJI_TEXT);
			}
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

	public AbstractAction createActionValidateAndAddWord(
			Map<JTextComponent, List<JTextComponent>> kanaToKanjiWritings,
			Map<JTextComponent, ListElementPropertyManager<?, JapaneseWordInformation>> textsWithPropertyManagers,
			JComboBox partOfSpeech) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				validateAndAddWordIfValid(kanaToKanjiWritings,
						textsWithPropertyManagers, partOfSpeech);
			}
		};
	}

}

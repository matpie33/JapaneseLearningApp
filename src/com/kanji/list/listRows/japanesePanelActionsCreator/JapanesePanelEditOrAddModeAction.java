package com.kanji.list.listRows.japanesePanelActionsCreator;

import com.kanji.constants.enums.JapanesePanelDisplayMode;
import com.kanji.constants.enums.WordSearchOptions;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElementPropertyManagers.JapaneseWordWritingsChecker;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;

public class JapanesePanelEditOrAddModeAction {

	private DialogWindow parentDialog;
	private JapanesePanelDisplayMode japanesePanelDisplayMode;
	private ApplicationController applicationController;

	public JapanesePanelEditOrAddModeAction(
			ApplicationController applicationController,
			DialogWindow parentDialog,
			JapanesePanelDisplayMode japanesePanelDisplayMode) {
		this.parentDialog = parentDialog;
		this.japanesePanelDisplayMode = japanesePanelDisplayMode;
		this.applicationController = applicationController;
	}

	public void addWordMeaningTextFieldListeners(
			JTextComponent wordMeaningTextField,
			JapaneseWordInformation japaneseWordInformation) {
		JapanesePanelActions
				.addWordMeaningPropertyChangeListener(wordMeaningTextField,
						japaneseWordInformation,
						WordSearchOptions.BY_FULL_EXPRESSION, parentDialog,
						applicationController.getJapaneseWords());
	}

	public JTextComponent withKanaValidation(JTextComponent kanaTextField,
			JapaneseWriting japaneseWriting,
			JapaneseWordInformation japaneseWordInformation) {
		addJapaneseWritingTextFieldListener(kanaTextField, japaneseWriting,
				japaneseWordInformation, Prompts.KANA_TEXT, true);
		return kanaTextField;
	}

	public JTextComponent withKanjiValidation(
			JTextComponent kanjiWritingTextField,
			JapaneseWriting japaneseWriting,
			JapaneseWordInformation japaneseWordInformation) {
		addJapaneseWritingTextFieldListener(kanjiWritingTextField,
				japaneseWriting, japaneseWordInformation, Prompts.KANJI_TEXT,
				false);
		return kanjiWritingTextField;
	}

	private void addJapaneseWritingTextFieldListener(
			JTextComponent japaneseWritingTextField,
			JapaneseWriting japaneseWriting,
			JapaneseWordInformation japaneseWordInformation,
			String promptOnEmpty, boolean kanaChecker) {
		boolean isKanaRequired = japanesePanelDisplayMode
				.isKanaTextFieldRequired();
		JapanesePanelActions.addPropertyChangeHandler(japaneseWritingTextField,
				japaneseWordInformation, isKanaRequired, promptOnEmpty,
				new JapaneseWordWritingsChecker(japaneseWriting, isKanaRequired,
						kanaChecker, japaneseWritingTextField.getText()),
				ExceptionsMessages.JAPANESE_WORD_WRITINGS_ALREADY_DEFINED,
				parentDialog, applicationController.getJapaneseWords());
	}

	public void addPartOfSpeechListener(JComboBox partOfSpeechCombobox,
			JapaneseWordInformation japaneseWordInformation) {
		JapanesePanelActions.addSavingOnSelectionListener(partOfSpeechCombobox,
				japaneseWordInformation, applicationController);
	}
}

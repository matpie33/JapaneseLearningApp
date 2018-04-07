package com.kanji.list.listRows.japanesePanelActionsCreator;

import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listElements.JapaneseWriting;

import javax.swing.*;
import javax.swing.text.JTextComponent;

public interface JapanesePanelActionCreatingService {

	public void addWordMeaningTextFieldListeners(
			JTextComponent wordMeaningTextField,
			JapaneseWordInformation japaneseWordInformation);

	public JTextComponent withKanaValidation(JTextComponent kanaTextField,
			JapaneseWriting japaneseWriting,
			JapaneseWordInformation japaneseWordInformation);

	public JTextComponent withKanjiValidation(JTextComponent kanaTextField,
			JapaneseWriting japaneseWriting,
			JapaneseWordInformation japaneseWordInformation);

	public void addPartOfSpeechListener(JComboBox partOfSpeechCombobox,
			JapaneseWordInformation japaneseWordInformation);
}

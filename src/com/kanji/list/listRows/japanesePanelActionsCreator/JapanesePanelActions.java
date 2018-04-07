package com.kanji.list.listRows.japanesePanelActionsCreator;

import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.enums.WordSearchOptions;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.list.listElementPropertyManagers.JapaneseWordMeaningChecker;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.myList.ListPropertyChangeHandler;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Locale;

public class JapanesePanelActions {

	public static void addPropertyChangeHandler(JTextComponent textComponent,
			JapaneseWordInformation japaneseWordInformation,
			boolean kanaRequired, String defaultValue,
			ListElementPropertyManager<?, JapaneseWordInformation> propertyChangeHandler,
			String exceptionMessage, DialogWindow parentDialog,
			MyList<JapaneseWordInformation> wordsList) {
		textComponent.addFocusListener(
				new ListPropertyChangeHandler<>(japaneseWordInformation,
						wordsList, parentDialog, propertyChangeHandler,
						exceptionMessage, defaultValue, kanaRequired));
	}

	public static void addWordMeaningPropertyChangeListener(
			JTextComponent wordMeaningTextField,
			JapaneseWordInformation japaneseWordInformation,
			WordSearchOptions meaningSearchOptions, DialogWindow parentDialog,
			MyList<JapaneseWordInformation> wordsList) {
		addPropertyChangeHandler(wordMeaningTextField, japaneseWordInformation,
				true, "", new JapaneseWordMeaningChecker(meaningSearchOptions),
				ExceptionsMessages.JAPANESE_WORD_MEANING_ALREADY_DEFINED,
				parentDialog, wordsList);
	}

	public static JTextComponent withSwitchToJapaneseActionOnClick(
			JTextComponent textComponent) {
		textComponent.addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent e) {
				textComponent.getInputContext().selectInputMethod(Locale.JAPAN);
				textComponent.getInputContext().setCharacterSubsets(
						new Character.Subset[] {
								Character.UnicodeBlock.HIRAGANA });
				super.focusGained(e);
			}

			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				textComponent.getInputContext()
						.selectInputMethod(Locale.getDefault());
			}
		});
		return textComponent;
	}

	public static void addSavingOnSelectionListener(
			JComboBox partOfSpeechCombobox,
			JapaneseWordInformation japaneseWordInformation,
			ApplicationController applicationController) {
		partOfSpeechCombobox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() != ItemEvent.SELECTED) {
					return;
				}
				String newValue = (String) e.getItem();
				japaneseWordInformation.setPartOfSpeech(
						PartOfSpeech.getPartOfSpeachByPolishMeaning(newValue));
				applicationController.saveProject();
			}
		});
	}
}

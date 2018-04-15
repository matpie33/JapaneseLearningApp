package com.kanji.list.listRows.japanesePanelActionsCreator;

import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.enums.WordSearchOptions;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.list.listElementPropertyManagers.JapaneseWordMeaningChecker;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listRows.japanesePanelCreator.TextFieldSelectionHandler;
import com.kanji.list.myList.ListPropertyChangeHandler;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class JapanesePanelActions {

	private Map<JTextComponent, ListElementPropertyManager> textFieldsWithPropertyManagers = new HashMap<>();

	private boolean isForSearchDialog;


	public Map<JTextComponent, ListElementPropertyManager> getTextFieldsWithPropertyManagers() {
		return textFieldsWithPropertyManagers;
	}

	public void addPropertyChangeHandler(JTextComponent textComponent,
			JapaneseWord japaneseWord, boolean kanaRequired,
			String defaultValue,
			ListElementPropertyManager<?, JapaneseWord> propertyChangeHandler,
			String exceptionMessage, DialogWindow parentDialog,
			MyList<JapaneseWord> wordsList) {
		textComponent.addFocusListener(
				new ListPropertyChangeHandler<>(japaneseWord, wordsList,
						parentDialog, propertyChangeHandler, exceptionMessage,
						defaultValue, kanaRequired));
		if (isForSearchDialog) {
			textFieldsWithPropertyManagers
					.put(textComponent, propertyChangeHandler);
		}

	}

	public void addWordMeaningPropertyChangeListener(
			JTextComponent wordMeaningTextField, JapaneseWord japaneseWord,
			WordSearchOptions meaningSearchOptions, DialogWindow parentDialog,
			MyList<JapaneseWord> wordsList) {
		addPropertyChangeHandler(wordMeaningTextField, japaneseWord, true, "",
				new JapaneseWordMeaningChecker(
						meaningSearchOptions),
				ExceptionsMessages.JAPANESE_WORD_MEANING_ALREADY_DEFINED,
				parentDialog, wordsList);
	}

	public JTextComponent withSwitchToJapaneseActionOnClick(
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

	public void addSavingOnSelectionListener(JComboBox partOfSpeechCombobox,
			JapaneseWord japaneseWord,
			ApplicationController applicationController) {
		partOfSpeechCombobox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() != ItemEvent.SELECTED) {
					return;
				}
				String newValue = (String) e.getItem();
				japaneseWord.setPartOfSpeech(
						PartOfSpeech.getPartOfSpeachByPolishMeaning(newValue));
				applicationController.saveProject();
			}
		});
	}

	public JTextComponent selectableTextfield(JTextComponent textComponent,
			TextFieldSelectionHandler selectionHandler) {
		textComponent.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (e.getSource() instanceof JTextComponent) {
					selectionHandler
							.toggleSelection((JTextComponent) e.getSource());
				}

			}
		});
		return textComponent;
	}

	public void setIsForSearchDialog(
			boolean rememberTextfieldsAndPropertyManagers) {
		this.isForSearchDialog = rememberTextfieldsAndPropertyManagers;
	}
}

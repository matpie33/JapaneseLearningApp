package com.kanji.list.listRows.japanesePanelActionsCreator;

import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.enums.WordSearchOptions;
import com.kanji.list.listElementPropertyManagers.JapaneseWordMeaningChecker;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElementPropertyManagers.japaneseWordWritings.JapaneseWordWritingsChecker;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listRows.japanesePanelCreator.TextFieldSelectionHandler;
import com.kanji.list.myList.ListPropertyChangeHandler;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.utilities.JapaneseWritingUtilities;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class JapanesePanelActions {

	private Map<JTextComponent, ListElementPropertyManager> textFieldsWithPropertyManagers = new HashMap<>();
	private Map<JapaneseWriting, JapaneseWordWritingsChecker> writingToCheckerMap = new HashMap<>();
	private DialogWindow parentDialog;
	private ApplicationController applicationController;

	public JapanesePanelActions(DialogWindow parentDialog,
			ApplicationController applicationController) {
		this.parentDialog = parentDialog;
		this.applicationController = applicationController;
	}

	public Map<JTextComponent, ListElementPropertyManager> getTextFieldsWithPropertyManagers() {
		return textFieldsWithPropertyManagers;
	}

	public JTextComponent withJapaneseWritingValidation(
			JTextComponent textComponent, JapaneseWriting japaneseWriting,
			JapaneseWord japaneseWord, boolean isKana,
			boolean isForSearchDialog) {
		JapaneseWordWritingsChecker checker = getOrCreateCheckerFor(
				japaneseWriting, isForSearchDialog);
		if (isKana) {
			checker.setKanaInput(textComponent);
		}
		else {
			checker.addKanjiInput(textComponent);
		}
		addPropertyChangeHandler(textComponent, japaneseWord,
				!isForSearchDialog,
				JapaneseWritingUtilities.getDefaultValueForWriting(isKana),
				checker, parentDialog, applicationController.getJapaneseWords(),
				isForSearchDialog);
		return textComponent;
	}

	private JapaneseWordWritingsChecker getOrCreateCheckerFor(
			JapaneseWriting writing, boolean isForSearchDialog) {
		JapaneseWordWritingsChecker checker = writingToCheckerMap.get(writing);
		if (checker == null) {
			checker = new JapaneseWordWritingsChecker(writing,
					!isForSearchDialog);
			writingToCheckerMap.put(writing, checker);
		}
		return checker;
	}

	public void addPropertyChangeHandler(JTextComponent textComponent,
			JapaneseWord japaneseWord, boolean kanaRequired,
			String defaultValue,
			ListElementPropertyManager<?, JapaneseWord> propertyChangeHandler,
			DialogWindow parentDialog, MyList<JapaneseWord> wordsList,
			boolean isForSearchDialog) {
		textComponent.addFocusListener(
				new ListPropertyChangeHandler<>(japaneseWord, wordsList,
						parentDialog, propertyChangeHandler, defaultValue,
						kanaRequired, kanaRequired));
		if (isForSearchDialog) {
			textFieldsWithPropertyManagers
					.put(textComponent, propertyChangeHandler);
		}

	}

	public void addWordMeaningPropertyChangeListener(
			JTextComponent wordMeaningTextField, JapaneseWord japaneseWord,
			WordSearchOptions meaningSearchOptions, DialogWindow parentDialog,
			MyList<JapaneseWord> wordsList, boolean isForSearchDialog) {
		addPropertyChangeHandler(wordMeaningTextField, japaneseWord, true, "",
				new JapaneseWordMeaningChecker(meaningSearchOptions),
				parentDialog, wordsList, isForSearchDialog);
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

}

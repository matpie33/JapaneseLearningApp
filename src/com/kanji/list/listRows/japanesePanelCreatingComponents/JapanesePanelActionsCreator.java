package com.kanji.list.listRows.japanesePanelCreatingComponents;

import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.enums.WordSearchOptions;
import com.kanji.list.listElementPropertyManagers.JapaneseWordMeaningChecker;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElementPropertyManagers.japaneseWordWritings.JapaneseWordChecker;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.myList.ListPropertyChangeHandler;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.utilities.JapaneseWritingUtilities;
import com.kanji.windows.DialogWindow;
import javafx.util.Pair;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.*;
import java.util.*;

public class JapanesePanelActionsCreator {

	private List<Pair<JapaneseWord, JapaneseWordChecker>> checkersForJapaneseWords = new ArrayList<>();
	private DialogWindow parentDialog;
	private ApplicationController applicationController;
	private JapaneseWordMeaningChecker wordMeaningChecker;

	public JapaneseWordMeaningChecker getWordMeaningChecker() {
		return wordMeaningChecker;
	}

	public JapanesePanelActionsCreator(DialogWindow parentDialog,
			ApplicationController applicationController) {
		this.parentDialog = parentDialog;
		this.applicationController = applicationController;
	}

	public Map<JTextComponent, ListElementPropertyManager> getInputManagersForInputs() {
		Map<JTextComponent, ListElementPropertyManager> propertyManagersForInputs = new HashMap<>();
		for (Pair<JapaneseWord, JapaneseWordChecker> japaneseWordChecker : checkersForJapaneseWords) {
			if (japaneseWordChecker.getValue().isForSearchWord()) {
				propertyManagersForInputs.putAll(japaneseWordChecker.getValue()
						.getInputToCheckerMap());
			}
		}
		return propertyManagersForInputs;
	}

	public JTextComponent withJapaneseWritingValidation(
			JTextComponent textComponent, JapaneseWriting japaneseWriting,
			JapaneseWord japaneseWord, boolean isKana,
			boolean isForSearchDialog) {
		JapaneseWordChecker checker = getOrCreateCheckerFor(japaneseWriting,
				japaneseWord, isForSearchDialog);
		if (isKana) {
			checker.addKanaInput(textComponent, japaneseWriting);
		}
		else {
			checker.addKanjiInput(textComponent, japaneseWriting);
		}
		addPropertyChangeHandler(textComponent, japaneseWord,
				!isForSearchDialog,
				JapaneseWritingUtilities.getDefaultValueForWriting(isKana),
				checker, parentDialog,
				applicationController.getJapaneseWords(), !isForSearchDialog);
		return textComponent;
	}

	private JapaneseWordChecker getOrCreateCheckerFor(JapaneseWriting writing,
			JapaneseWord word, boolean isForSearchDialog) {

		for (Pair<JapaneseWord, JapaneseWordChecker> checkerForJapaneseWord : checkersForJapaneseWords) {
			if (checkerForJapaneseWord.getKey().containsWriting(writing)) {
				return checkerForJapaneseWord.getValue();
			}
		}

		JapaneseWordChecker checker = new JapaneseWordChecker(
				!isForSearchDialog);
		checkersForJapaneseWords.add(new Pair<>(word, checker));
		return checker;
	}

	public void addPropertyChangeHandler(JTextComponent textComponent,
			JapaneseWord japaneseWord, boolean kanaRequired,
			String defaultValue,
			ListElementPropertyManager<?, JapaneseWord> propertyChangeHandler,
			DialogWindow parentDialog, MyList<JapaneseWord> wordsList,
			boolean addingWord) {
		textComponent.addFocusListener(
				new ListPropertyChangeHandler<>(japaneseWord, wordsList,
						parentDialog, propertyChangeHandler, defaultValue,
						kanaRequired, addingWord));

	}

	public void addWordMeaningPropertyChangeListener(
			JTextComponent wordMeaningTextField, JapaneseWord japaneseWord,
			WordSearchOptions meaningSearchOptions, boolean addingWord) {
		wordMeaningChecker = new JapaneseWordMeaningChecker(
				meaningSearchOptions);
		addPropertyChangeHandler(wordMeaningTextField, japaneseWord, true, "",
				wordMeaningChecker, parentDialog,
				applicationController.getJapaneseWords(), addingWord);
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
			JapaneseWord japaneseWord) {
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
		textComponent.setFocusable(false);
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

	public AbstractButton updateWritingsInWordWhenDeleteWriting(
			AbstractButton buttonDelete, JapaneseWord japaneseWord,
			JapaneseWriting writing, boolean isForSearchDialog) {
		buttonDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getOrCreateCheckerFor(writing, japaneseWord, isForSearchDialog)
						.removeWriting(writing);
				japaneseWord.getWritings().remove(writing);
				applicationController.saveProject();
			}
		});
		return buttonDelete;
	}

	public JTextComponent repaintParentOnFocusLost(JTextComponent textInput) {
		textInput.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				parentDialog.getContainer().repaint();
			}
		});
		return textInput;
	}

}

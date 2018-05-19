package com.kanji.list.listRows.japanesePanelCreatingComponents;

import com.kanji.constants.enums.InputGoal;
import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.enums.WordSearchOptions;
import com.kanji.list.listElementPropertyManagers.JapaneseWordMeaningChecker;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElementPropertyManagers.japaneseWordWritings.JapaneseWordChecker;
import com.kanji.list.listElementPropertyManagers.japaneseWordWritings.JapaneseWordWritingsChecker;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listeners.InputValidationListener;
import com.kanji.list.myList.ListPropertyChangeHandler;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.utilities.JapaneseWritingUtilities;
import com.kanji.utilities.Pair;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class JapanesePanelActionsCreator {

	private List<Pair<JapaneseWord, JapaneseWordChecker>> checkersForJapaneseWords = new ArrayList<>();
	private DialogWindow parentDialog;
	private ApplicationController applicationController;
	private JapaneseWordMeaningChecker wordMeaningChecker;
	private Set<InputValidationListener<JapaneseWord>> inputValidationListeners = new HashSet<>();

	public JapanesePanelActionsCreator(DialogWindow parentDialog,
			ApplicationController applicationController) {
		this.parentDialog = parentDialog;
		this.applicationController = applicationController;
	}

	public void setInputValidationListeners(
			Set<InputValidationListener<JapaneseWord>> inputValidationListeners) {
		this.inputValidationListeners = inputValidationListeners;
	}

	public JapaneseWordMeaningChecker getWordMeaningChecker() {
		return wordMeaningChecker;
	}

	public JTextComponent switchToHandCursorOnMouseEnter(
			JTextComponent textComponent) {
		textComponent.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				parentDialog.getPanel().getPanel()
						.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseEntered(e);
				parentDialog.getPanel().getPanel()
						.setCursor(Cursor.getDefaultCursor());
			}
		});
		return textComponent;
	}

	public Map<JTextComponent, ListElementPropertyManager> getInputManagersForInputs() {
		Map<JTextComponent, ListElementPropertyManager> propertyManagersForInputs = new HashMap<>();
		for (Pair<JapaneseWord, JapaneseWordChecker> japaneseWordChecker : checkersForJapaneseWords) {
			InputGoal checkerInputGoal = japaneseWordChecker.getRight()
					.getInputGoal();
			if (checkerInputGoal.equals(InputGoal.SEARCH) || checkerInputGoal
					.equals(InputGoal.ADD)) {
				propertyManagersForInputs.putAll(japaneseWordChecker.getRight()
						.getInputToCheckerMap());
			}
		}
		return propertyManagersForInputs;
	}

	public JTextComponent withJapaneseWritingValidation(
			JTextComponent textComponent, JapaneseWriting japaneseWriting,
			JapaneseWord japaneseWord, boolean isKana, InputGoal inputGoal) {
		JapaneseWordChecker checker = getOrCreateCheckerFor(japaneseWriting,
				japaneseWord, inputGoal);
		if (isKana) {
			checker.addKanaInput(textComponent, japaneseWriting);
		}
		else {
			checker.addKanjiInput(textComponent, japaneseWriting);
		}
		addPropertyChangeHandler(textComponent, japaneseWord,
				!inputGoal.equals(InputGoal.SEARCH) && isKana,
				JapaneseWritingUtilities.getDefaultValueForWriting(isKana),
				checker, parentDialog, applicationController.getJapaneseWords(),
				inputGoal);
		return textComponent;
	}

	private JapaneseWordChecker getOrCreateCheckerFor(JapaneseWriting writing,
			JapaneseWord word, InputGoal inputGoal) {

		for (Pair<JapaneseWord, JapaneseWordChecker> checkerForJapaneseWord : checkersForJapaneseWords) {
			if (checkerForJapaneseWord.getLeft().containsWriting(writing)) {
				return checkerForJapaneseWord.getRight();
			}
		}
		JapaneseWordChecker checker = new JapaneseWordChecker(inputGoal);
		checkersForJapaneseWords.add(new Pair<>(word, checker));
		return checker;
	}

	private void addPropertyChangeHandler(JTextComponent textComponent,
			JapaneseWord japaneseWord, boolean requiredInput,
			String defaultValue,
			ListElementPropertyManager<?, JapaneseWord> propertyManager,
			DialogWindow parentDialog, MyList<JapaneseWord> wordsList,
			InputGoal inputGoal) {
		ListPropertyChangeHandler<?, JapaneseWord> propertyChangeHandler = new ListPropertyChangeHandler<>(
				japaneseWord, wordsList, parentDialog, propertyManager,
				defaultValue, requiredInput, inputGoal);
		if (inputGoal.equals(InputGoal.ADD) || inputGoal
				.equals(InputGoal.SEARCH)) {
			inputValidationListeners
					.forEach(propertyChangeHandler::addValidationListener);
		}

		textComponent.addFocusListener(propertyChangeHandler);

	}

	public void addWordMeaningPropertyChangeListener(
			JTextComponent wordMeaningTextField, JapaneseWord japaneseWord,
			WordSearchOptions meaningSearchOptions, InputGoal inputGoal) {
		wordMeaningChecker = new JapaneseWordMeaningChecker(
				meaningSearchOptions);
		addPropertyChangeHandler(wordMeaningTextField, japaneseWord, true, "",
				wordMeaningChecker, parentDialog,
				applicationController.getJapaneseWords(), inputGoal);
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

	public AbstractButton updateWritingsInWordWhenDeleteWriting(
			AbstractButton buttonDelete, JapaneseWord japaneseWord,
			JapaneseWriting writing, InputGoal inputGoal) {
		buttonDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getOrCreateCheckerFor(writing, japaneseWord, inputGoal)
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

	public JapaneseWord getWordContainingInput(JTextComponent input) {
		for (Pair<JapaneseWord, JapaneseWordChecker> wordToChecker : checkersForJapaneseWords) {
			Pair<JapaneseWriting, JapaneseWordWritingsChecker> writingToChecker = wordToChecker
					.getRight().getWritingForInput(input);
			if (writingToChecker != null) {
				return wordToChecker.getLeft();
			}
		}
		return null;
	}

}

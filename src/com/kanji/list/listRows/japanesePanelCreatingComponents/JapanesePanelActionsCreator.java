package com.kanji.list.listRows.japanesePanelCreatingComponents;

import com.kanji.constants.enums.*;
import com.kanji.list.listElementPropertyManagers.JapaneseWordMeaningChecker;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElementPropertyManagers.japaneseWordWritings.JapaneseWordChecker;
import com.kanji.list.listElementPropertyManagers.japaneseWordWritings.JapaneseWordWritingsChecker;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listeners.InputValidationListener;
import com.kanji.list.myList.ListPropertyChangeHandler;
import com.kanji.list.myList.MyList;
import com.kanji.model.AdditionalInformation;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.utilities.JapaneseWritingUtilities;
import com.kanji.utilities.Pair;
import com.kanji.utilities.ThreadUtilities;
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
	private MyList<JapaneseWord> wordsList;

	public JapanesePanelActionsCreator(DialogWindow parentDialog,
			ApplicationController applicationController) {
		this.parentDialog = parentDialog;
		this.applicationController = applicationController;
	}

	public void setWordsList(MyList<JapaneseWord> wordsList) {
		this.wordsList = wordsList;
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
		Map<JTextComponent, ListElementPropertyManager> propertyManagersForInputs = new LinkedHashMap<>();
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
			JapaneseWord japaneseWord, boolean isKana, InputGoal inputGoal,
			boolean enabled) {
		JapaneseWordChecker checker = getOrCreateCheckerFor(japaneseWriting,
				japaneseWord, inputGoal);
		if (isKana) {
			checker.addKanaInput(textComponent, japaneseWriting);
		}
		else {
			checker.addKanjiInput(textComponent, japaneseWriting);
		}
		if (enabled) {
			addPropertyChangeHandler(textComponent, japaneseWord,
					!inputGoal.equals(InputGoal.SEARCH) && isKana,
					JapaneseWritingUtilities.getDefaultValueForWriting(isKana),
					checker, parentDialog, wordsList, inputGoal);
		}

		return textComponent;
	}

	private JapaneseWordChecker getOrCreateCheckerFor(JapaneseWriting writing,
			JapaneseWord word, InputGoal inputGoal) {

		for (Pair<JapaneseWord, JapaneseWordChecker> checkerForJapaneseWord : checkersForJapaneseWords) {
			if (checkerForJapaneseWord.getLeft().equals(word)) {
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
		propertyChangeHandler.addValidationListener(wordsList);

		textComponent.addFocusListener(propertyChangeHandler);

	}

	public void addWordMeaningPropertyChangeListener(
			JTextComponent wordMeaningTextField, JapaneseWord japaneseWord,
			WordSearchOptions meaningSearchOptions, InputGoal inputGoal) {
		wordMeaningChecker = new JapaneseWordMeaningChecker(
				meaningSearchOptions);
		addPropertyChangeHandler(wordMeaningTextField, japaneseWord, true, "",
				wordMeaningChecker, parentDialog, wordsList, inputGoal);
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

	public JComboBox changeAdditionalInformationOnComboboxChange(
			JComboBox comboBox, JapaneseWord japaneseWord) {
		SwingUtilities.invokeLater(() -> {
			comboBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() != ItemEvent.SELECTED) {
						return;
					}
					String newValue = (String) comboBox.getSelectedItem();
					japaneseWord.getAdditionalInformation().setValue(newValue);
					ThreadUtilities.callOnOtherThread(
							applicationController::saveProject);
					wordsList.updateObservers(japaneseWord,
							ListElementModificationType.EDIT);
				}
			});
		});

		return comboBox;

	}

	public JComboBox<String> addAdditionalInformationOnPartOfSpeechChange(
			JComboBox additionalInformationValue,
			JLabel additionalInformationLabel, JComboBox partOfSpeechCombobox,
			JapaneseWord japaneseWord) {
		partOfSpeechCombobox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() != ItemEvent.SELECTED) {
					return;
				}
				String newValue = (String) e.getItem();
				PartOfSpeech newPartOfSpeech = PartOfSpeech
						.getPartOfSpeachByPolishMeaning(newValue);

				if (newPartOfSpeech.equals(japaneseWord.getPartOfSpeech())) {
					return;
				}
				japaneseWord.setPartOfSpeech(newPartOfSpeech);

				String[] possibleValues = newPartOfSpeech.getPossibleValues();
				boolean hasAdditionalInformation = possibleValues.length > 0;
				additionalInformationLabel.setEnabled(hasAdditionalInformation);
				additionalInformationValue.setEnabled(hasAdditionalInformation);
				additionalInformationValue.removeAllItems();
				if (hasAdditionalInformation) {
					AdditionalInformationTag additionalInformationTag = newPartOfSpeech
							.getAdditionalInformationTag();
					additionalInformationLabel
							.setText(additionalInformationTag.getLabel());
					AdditionalInformation additionalInformation = new AdditionalInformation(
							additionalInformationTag, possibleValues);
					japaneseWord
							.setAdditionalInformation(additionalInformation);
					Arrays.stream(possibleValues)
							.forEach(additionalInformationValue::addItem);
				}
				else {
					ThreadUtilities.callOnOtherThread(
							applicationController::saveProject);

				}
				wordsList.updateObservers(japaneseWord,
						ListElementModificationType.EDIT);
			}
		});
		return partOfSpeechCombobox;
	}

	public void clearMappingForWordIfExists(JapaneseWord japaneseWord) {
		checkersForJapaneseWords
				.removeIf(pair -> pair.getLeft().equals(japaneseWord));
	}

	public MyList<JapaneseWord> getWordsList() {
		return wordsList;
	}
}

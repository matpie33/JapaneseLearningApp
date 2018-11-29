package com.kanji.list.listRows.japanesePanelCreatingComponents;

import com.guimaker.application.DialogWindow;
import com.guimaker.enums.InputGoal;
import com.guimaker.enums.ListElementModificationType;
import com.guimaker.enums.WordSearchOptions;
import com.guimaker.list.ListElementPropertyManager;
import com.guimaker.list.myList.ListPropertyChangeHandler;
import com.guimaker.list.myList.MyList;
import com.guimaker.model.CommonListElements;
import com.guimaker.utilities.Pair;
import com.guimaker.utilities.ThreadUtilities;
import com.kanji.constants.enums.AdditionalInformationTag;
import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.enums.TypeOfJapaneseWriting;
import com.kanji.constants.strings.Labels;
import com.kanji.list.listElementPropertyManagers.JapaneseWordMeaningChecker;
import com.kanji.list.listElementPropertyManagers.KanaWritingChecker;
import com.kanji.list.listElementPropertyManagers.KanjiWritingChecker;
import com.kanji.list.listElementPropertyManagers.japaneseWordWritings.JapaneseWordChecker;
import com.kanji.list.listElementPropertyManagers.japaneseWordWritings.JapaneseWordWritingsChecker;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.model.AdditionalInformation;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.utilities.JapaneseWritingUtilities;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class JapanesePanelActionsCreator {

	private List<Pair<JapaneseWord, JapaneseWordChecker>> checkersForJapaneseWords = new ArrayList<>();
	private DialogWindow parentDialog;
	private ApplicationController applicationController;
	private JapaneseWordMeaningChecker wordMeaningChecker;
	private MyList<JapaneseWord> wordsList;
	private JapaneseWordChecker wordCheckerForKanaOrKanjiFilter;

	public JapanesePanelActionsCreator(DialogWindow parentDialog,
			ApplicationController applicationController) {
		this.parentDialog = parentDialog;
		this.applicationController = applicationController;
	}

	public void setWordsList(MyList<JapaneseWord> wordsList) {
		this.wordsList = wordsList;
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
				parentDialog.getPanel()
							.getPanel()
							.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseEntered(e);
				parentDialog.getPanel()
							.getPanel()
							.setCursor(Cursor.getDefaultCursor());
			}
		});
		return textComponent;
	}

	public JTextComponent getAnyKanjiInputField() {
		return checkersForJapaneseWords.get(0)
									   .getRight()
									   .getAnyKanjiInput();
	}

	public JapaneseWordChecker getWordCheckerForKanaOrKanjiFilter() {
		return wordCheckerForKanaOrKanjiFilter;
	}

	public JTextComponent withJapaneseWritingValidation(
			JTextComponent textComponent, JapaneseWriting japaneseWriting,
			JapaneseWord japaneseWord,
			TypeOfJapaneseWriting typeOfJapaneseWriting, InputGoal inputGoal,
			boolean enabled, CommonListElements commonListElements) {
		boolean isKana = typeOfJapaneseWriting.equals(
				TypeOfJapaneseWriting.KANA);
		ListElementPropertyManager propertyManager;
		if (isKana) {
			propertyManager = new KanaWritingChecker();
		}
		else {
			propertyManager = new KanjiWritingChecker();
		}

		if (typeOfJapaneseWriting.equals(TypeOfJapaneseWriting.KANA_OR_KANJI)
				&& wordCheckerForKanaOrKanjiFilter == null) {
			//TODO do I have to create this variable? is the usual way not
			// enough?
			wordCheckerForKanaOrKanjiFilter = null;
		}

		if (enabled) {
			addPropertyChangeHandler(textComponent, japaneseWriting,
					!inputGoal.equals(InputGoal.SEARCH) && isKana,
					JapaneseWritingUtilities.getDefaultValueForWriting(
							typeOfJapaneseWriting), propertyManager,
					parentDialog, commonListElements.getList(), inputGoal);
		}

		return textComponent;
	}

	private JapaneseWordChecker getOrCreateCheckerFor(JapaneseWord word,
			InputGoal inputGoal) {

		for (Pair<JapaneseWord, JapaneseWordChecker> checkerForJapaneseWord : checkersForJapaneseWords) {
			if (checkerForJapaneseWord.getLeft()
									  .equals(word)) {
				return checkerForJapaneseWord.getRight();
			}
		}
		JapaneseWordChecker checker = new JapaneseWordChecker(inputGoal);
		checkersForJapaneseWords.add(new Pair<>(word, checker));
		return checker;
	}

	private void addPropertyChangeHandler(JTextComponent textComponent,
			JapaneseWriting japaneseWriting, boolean requiredInput,
			String defaultValue,
			ListElementPropertyManager<?, JapaneseWriting> propertyManager,
			DialogWindow parentDialog, MyList<JapaneseWriting> wordsList,
			InputGoal inputGoal) {
		ListPropertyChangeHandler<?, JapaneseWriting> propertyChangeHandler = new ListPropertyChangeHandler<>(
				japaneseWriting, wordsList, parentDialog, propertyManager,
				defaultValue, requiredInput, inputGoal);
		textComponent.addFocusListener(propertyChangeHandler);

	}

	public JTextComponent withWordMeaningChangeListener(
			JTextComponent wordMeaningTextField, JapaneseWord japaneseWord,
			WordSearchOptions meaningSearchOptions, InputGoal inputGoal) {
		wordMeaningChecker = new JapaneseWordMeaningChecker(
				meaningSearchOptions);
		ListPropertyChangeHandler<?, JapaneseWord> propertyChangeHandler = new ListPropertyChangeHandler<>(
				japaneseWord, wordsList, parentDialog, wordMeaningChecker, "",
				true, inputGoal);
		wordMeaningTextField.addFocusListener(propertyChangeHandler);
		return wordMeaningTextField;
	}

	public JTextComponent withSwitchToJapaneseActionOnClick(
			JTextComponent textComponent) {
		textComponent.addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent e) {
				textComponent.getInputContext()
							 .selectInputMethod(Locale.JAPAN);
				textComponent.getInputContext()
							 .setCharacterSubsets(new Character.Subset[] {
									 Character.UnicodeBlock.HIRAGANA });
				super.focusGained(e);
			}

			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				if (textComponent.getInputContext() == null) {
					parentDialog.getContainer()
								.getInputContext()
								.selectInputMethod(Locale.getDefault());
				}
				else {
					textComponent.getInputContext()
								 .selectInputMethod(Locale.getDefault());
				}

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
				getOrCreateCheckerFor(japaneseWord, inputGoal).removeWriting(
						writing);
				japaneseWord.getWritings()
							.remove(writing);
				applicationController.save();
			}
		});
		return buttonDelete;
	}

	public JTextComponent repaintParentOnFocusLost(JTextComponent textInput) {
		textInput.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				parentDialog.getContainer()
							.repaint();
			}
		});
		return textInput;
	}

	public JapaneseWord getWordContainingInput(JTextComponent input) {
		for (Pair<JapaneseWord, JapaneseWordChecker> wordToChecker : checkersForJapaneseWords) {
			Pair<JapaneseWriting, JapaneseWordWritingsChecker> writingToChecker = wordToChecker.getRight()
																							   .getWritingForInput(
																									   input);
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
					japaneseWord.getAdditionalInformation()
								.setValue(newValue);
					ThreadUtilities.callOnOtherThread(
							applicationController::save);
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
				PartOfSpeech newPartOfSpeech = PartOfSpeech.getPartOfSpeachByPolishMeaning(
						newValue);

				if (newPartOfSpeech.equals(japaneseWord.getPartOfSpeech())) {
					return;
				}
				japaneseWord.setPartOfSpeech(newPartOfSpeech);

				String[] possibleValues = newPartOfSpeech.getPossibleValues();
				boolean hasAdditionalInformation = !possibleValues[0].equals(
						Labels.NO_ADDITIONAL_INFORMATION);
				//TODO duplicated logic for has additional information in method
				// create combobox for additional information in japanese
				// panel elements creator
				additionalInformationValue.setEnabled(hasAdditionalInformation);
				additionalInformationValue.removeAllItems();
				Arrays.stream(possibleValues)
					  .forEach(additionalInformationValue::addItem);
				AdditionalInformationTag additionalInformationTag = newPartOfSpeech.getAdditionalInformationTag();
				additionalInformationLabel.setText(
						additionalInformationTag.getLabel());
				if (hasAdditionalInformation) {
					AdditionalInformation additionalInformation = new AdditionalInformation(
							additionalInformationTag, possibleValues);
					japaneseWord.setAdditionalInformation(
							additionalInformation);

				}
				else {
					ThreadUtilities.callOnOtherThread(
							applicationController::save);

				}
				wordsList.updateObservers(japaneseWord,
						ListElementModificationType.EDIT);
			}
		});
		return partOfSpeechCombobox;
	}

	public void clearMappingForWordIfExists(JapaneseWord japaneseWord) {
		checkersForJapaneseWords.removeIf(pair -> pair.getLeft()
													  .equals(japaneseWord));
	}

	public MyList<JapaneseWord> getWordsList() {
		return wordsList;
	}
}

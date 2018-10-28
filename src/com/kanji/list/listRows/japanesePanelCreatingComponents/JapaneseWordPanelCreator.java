package com.kanji.list.listRows.japanesePanelCreatingComponents;

import com.guimaker.application.DialogWindow;
import com.guimaker.enums.*;
import com.guimaker.inputSelection.ListInputsSelectionManager;
import com.guimaker.list.ListRowData;
import com.guimaker.list.myList.ListRowDataCreator;
import com.guimaker.list.myList.MyList;
import com.guimaker.listeners.InputValidationListener;
import com.guimaker.listeners.SwitchBetweenInputsFailListener;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.ComplexRow;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.CommonListElements;
import com.guimaker.utilities.Pair;
import com.kanji.constants.strings.ListPropertiesNames;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listElements.WordParticlesData;
import com.kanji.panelsAndControllers.controllers.ApplicationController;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class JapaneseWordPanelCreator
		implements SwitchBetweenInputsFailListener {

	private JComboBox<String> partOfSpeechCombobox;
	private JTextComponent wordMeaningText;
	private JLabel wordMeaningLabel;
	private JLabel partOfSpeechLabel;
	private JLabel writingsLabel;
	private JLabel rowNumberLabel;
	private MyList<JapaneseWriting> lastWritingsListCreated;
	private ApplicationController applicationController;
	private Color labelsColor = Color.WHITE;
	private DialogWindow parentDialog;
	private JapanesePanelComponentsStore japanesePanelComponentsStore;
	private ComplexRow lastJapanesePanelMade;
	private ListInputsSelectionManager listInputsSelectionManager;
	private PanelDisplayMode displayMode;
	private List<Pair<JapaneseWord, MyList<JapaneseWriting>>> writingsLists = new ArrayList<>();
	private MyList<WordParticlesData> particlesTakenList;
	private JLabel particlesTakenLabel;
	private JLabel additionalInformationLabel;
	private JComboBox additionalInformationValue;
	//TODO it's the second place where map did not fit due to mutable keys,
	//can we do better than list of pairs?

	public JapaneseWordPanelCreator(ApplicationController applicationController,
			DialogWindow parentDialog, PanelDisplayMode displayMode) {
		//TODO parent dialog is not needed without validation i.e. in view mode
		this.displayMode = displayMode;
		japanesePanelComponentsStore = new JapanesePanelComponentsStore(
				applicationController, parentDialog);
		this.applicationController = applicationController;
		this.parentDialog = parentDialog;
		listInputsSelectionManager = new ListInputsSelectionManager();
	}

	public PanelDisplayMode getDisplayMode() {
		return displayMode;
	}

	public ListInputsSelectionManager getListInputsSelectionManager() {
		return listInputsSelectionManager;
	}

	public void setRowNumberLabel(JLabel label) {
		rowNumberLabel = label;
	}

	public void setLabelsColor(Color color) {
		labelsColor = color;
	}

	public ListRowData<JapaneseWord> addJapanesePanelToExistingPanel(
			MainPanel existingPanel, JapaneseWord japaneseWord,
			InputGoal inputGoal, CommonListElements commonListElements,
			boolean inheritScrollBar) {
		determineDisplayMode(inputGoal);
		japanesePanelComponentsStore.getActionCreator()
									.clearMappingForWordIfExists(japaneseWord);
		createElements(japaneseWord, inputGoal, inheritScrollBar);
		return addElementsToPanel(existingPanel, commonListElements, inputGoal);

	}

	private void determineDisplayMode(InputGoal inputGoal) {
		if (inputGoal.equals(InputGoal.NO_INPUT)) {
			displayMode = PanelDisplayMode.VIEW;
		}
		else {
			displayMode = PanelDisplayMode.EDIT;
		}
	}

	private void createElements(JapaneseWord japaneseWord, InputGoal inputGoal,
			boolean inheritScrollBar) {
		if (rowNumberLabel != null) {
			rowNumberLabel.setForeground(labelsColor);
		}

		JapanesePanelElementsCreator elementsCreator = japanesePanelComponentsStore.getElementsCreator();

		wordMeaningLabel = elementsCreator.createWordMeaningLabel(labelsColor);
		wordMeaningText = elementsCreator.createWordMeaningText(japaneseWord,
				displayMode, inputGoal);
		partOfSpeechLabel = elementsCreator.createPartOfSpeechLabel(
				labelsColor);
		particlesTakenList = elementsCreator.createParticlesDataList(
				japaneseWord, displayMode);
		additionalInformationLabel = elementsCreator.createAdditionalInformationLabel(
				japaneseWord, labelsColor);
		additionalInformationValue = elementsCreator.createComboboxForAdditionalInformation(
				japaneseWord);
		partOfSpeechCombobox = elementsCreator.createComboboxForPartOfSpeech(
				japaneseWord.getPartOfSpeech(), additionalInformationLabel,
				additionalInformationValue, japaneseWord);
		lastWritingsListCreated = createWritingsList(japaneseWord, inputGoal,
				inheritScrollBar);
		writingsLabel = elementsCreator.createWritingsLabel(labelsColor);
		particlesTakenLabel = elementsCreator.createParticlesTakenLabel(
				labelsColor);
	}

	private MyList<JapaneseWriting> createWritingsList(
			JapaneseWord japaneseWord, InputGoal inputGoal,
			boolean inheritScrollBar) {
		lastWritingsListCreated = japanesePanelComponentsStore.getElementsCreator()
															  .createJapaneseWritingsList(
																	  japaneseWord,
																	  inheritScrollBar,
																	  parentDialog,
																	  displayMode,
																	  listInputsSelectionManager,
																	  japanesePanelComponentsStore.getPanelCreatingService(
																			  displayMode));
		writingsLists.add(new Pair<>(japaneseWord, lastWritingsListCreated));

		if (!japaneseWord.isEmpty()) {
			parentDialog.getPanel()
						.addNavigableByKeyboardList(lastWritingsListCreated);
		}

		if (japaneseWord.getWritings()
						.isEmpty()) {
			japaneseWord.addWritingsForKana("", "");
		}
		japaneseWord.getWritings()
					.forEach(word -> lastWritingsListCreated.addWord(word,
							inputGoal));
		lastWritingsListCreated.addSwitchBetweenInputsFailListener(this);
		return lastWritingsListCreated;
	}

	@Override
	public void switchBetweenInputsFailed(JTextComponent input,
			MoveDirection direction) {
		if (direction.equals(MoveDirection.BELOW) && !displayMode.equals(
				PanelDisplayMode.VIEW)) {
			MyList<JapaneseWriting> writingsListToAddWriting = findListThatFailedInSwitchingBetweenInputs(
					input);
			writingsListToAddWriting.addWord(JapaneseWriting.getInitializer()
															.initializeElement());
			writingsListToAddWriting.scrollToBottom();
		}
		if ((direction.equals(MoveDirection.LEFT) || direction.equals(
				MoveDirection.RIGHT)) && displayMode.equals(
				PanelDisplayMode.VIEW)) {
			MyList<JapaneseWriting> listThatFailed = findListThatFailedInSwitchingBetweenInputs(
					input);
			listThatFailed.getPanelWithSelectedInput()
						  .clearSelectedInput();
		}
	}

	private MyList<JapaneseWriting> findListThatFailedInSwitchingBetweenInputs(
			JTextComponent input) {
		JapaneseWord wordContainingInput = japanesePanelComponentsStore.getActionCreator()
																	   .getWordContainingInput(
																			   input);
		MyList<JapaneseWriting> writingsListToAddWriting = null;
		if (wordContainingInput != null) {
			for (Pair<JapaneseWord, MyList<JapaneseWriting>> wordWithWritings : writingsLists) {
				if (wordWithWritings.getLeft()
									.equals(wordContainingInput)) {
					writingsListToAddWriting = wordWithWritings.getRight();
					break;
				}
			}
		}
		else {
			writingsListToAddWriting = lastWritingsListCreated;
		}
		return writingsListToAddWriting;
	}

	private ListRowData<JapaneseWord> addElementsToPanel(
			MainPanel japaneseWordPanel, CommonListElements commonListElements,
			InputGoal inputGoal) {
		JPanel writingsListPanel = lastWritingsListCreated.getPanel();
		lastJapanesePanelMade = SimpleRowBuilder.createRowStartingFromColumn(0,
				FillType.NONE, Anchor.NORTH,
				commonListElements.getRowNumberLabel(), wordMeaningLabel,
				wordMeaningText)
												.nextRow(partOfSpeechLabel,
														partOfSpeechCombobox)
												.setColumnToPutRowInto(1)
												.nextRow(
														additionalInformationLabel,
														additionalInformationValue)
												.nextRow(writingsLabel,
														writingsListPanel)
												.nextRow(particlesTakenLabel,
														particlesTakenList.getPanel())
												.onlyAddIf(displayMode.equals(
														PanelDisplayMode.EDIT)
														||
														particlesTakenList.getNumberOfWords()
																> 0)
												.nextRow(
														commonListElements.getButtonDelete())
												.onlyAddIf(!displayMode.equals(
														PanelDisplayMode.VIEW))
												.nextRow(
														commonListElements.getButtonEdit())
												.onlyAddIf(displayMode.equals(
														PanelDisplayMode.VIEW))
												.nextRow(
														commonListElements.getFinishEditing())
												.onlyAddIf(inputGoal.equals(
														InputGoal.EDIT_TEMPORARILY));
		japaneseWordPanel.addRowsOfElementsInColumn(lastJapanesePanelMade);
		ListRowDataCreator<JapaneseWord> rowDataCreator = new ListRowDataCreator<>(
				japaneseWordPanel);
		rowDataCreator.addPropertyData(
				ListPropertiesNames.JAPANESE_WORD_MEANING, wordMeaningText,
				japanesePanelComponentsStore.getActionCreator()
											.getWordMeaningChecker());

		rowDataCreator.addPropertyData(
				ListPropertiesNames.JAPANESE_WORD_WRITINGS,
				japanesePanelComponentsStore.getElementsCreator()
											.getKanaOrKanjiInputForFiltering(),
				japanesePanelComponentsStore.getActionCreator()
											.getWordCheckerForKanaOrKanjiFilter());
		return rowDataCreator.getListRowData();
	}

	public void focusMeaningTextfield() {
		SwingUtilities.invokeLater(
				() -> wordMeaningText.requestFocusInWindow());
	}

	public JapaneseWordPanelCreator copy() {
		JapaneseWordPanelCreator wordPanelCreator = new JapaneseWordPanelCreator(
				applicationController, parentDialog, PanelDisplayMode.EDIT);
		wordPanelCreator.setWordsList(
				japanesePanelComponentsStore.getWordsList());
		return wordPanelCreator;
	}

	public void addValidationListeners(
			Set<InputValidationListener<JapaneseWord>> validationListeners) {
		japanesePanelComponentsStore.addValidationListeners(
				validationListeners);
	}

	public void setWordsList(MyList<JapaneseWord> list) {
		japanesePanelComponentsStore.setWordsList(list);
	}
}

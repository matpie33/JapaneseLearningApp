package com.kanji.list.listRows.japanesePanelCreatingComponents;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.MoveDirection;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.inputSelection.ListInputsSelectionManager;
import com.guimaker.listeners.SwitchBetweenInputsFailListener;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.ComplexRow;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.enums.InputGoal;
import com.kanji.constants.enums.WordSearchOptions;
import com.kanji.constants.strings.Labels;
import com.kanji.constants.strings.ListPropertiesNames;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listRows.RowInJapaneseWritingsList;
import com.kanji.list.listeners.InputValidationListener;
import com.kanji.list.myList.ListConfiguration;
import com.kanji.list.myList.ListRowData;
import com.kanji.list.myList.ListRowDataCreator;
import com.kanji.list.myList.MyList;
import com.kanji.model.WordParticlesData;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.utilities.CommonGuiElementsCreator;
import com.guimaker.utilities.CommonListElements;
import com.kanji.utilities.Pair;
import com.kanji.windows.DialogWindow;

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
	private MyList<JapaneseWord> list;
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
		if (inputGoal.equals(InputGoal.NO_INPUT)) {
			displayMode = PanelDisplayMode.VIEW;
		}
		else {
			displayMode = PanelDisplayMode.EDIT;
		}
		createElements(japaneseWord, inputGoal, inheritScrollBar);
		addActions(japaneseWord, inputGoal);
		return addElementsToPanel(existingPanel, commonListElements, inputGoal);

	}

	private void createElements(JapaneseWord japaneseWord, InputGoal inputGoal,
			boolean inheritScrollBar) {
		if (rowNumberLabel != null) {
			rowNumberLabel.setForeground(labelsColor);
		}
		japanesePanelComponentsStore.getActionCreator()
				.clearMappingForWordIfExists(japaneseWord);
		wordMeaningLabel = GuiElementsCreator.createLabel(
				new ComponentOptions().text(Labels.WORD_MEANING)
						.foregroundColor(labelsColor));
		wordMeaningText = CommonGuiElementsCreator
				.createShortInput(japaneseWord.getMeaning(), displayMode);

		partOfSpeechLabel = GuiElementsCreator.createLabel(
				new ComponentOptions().text(Labels.PART_OF_SPEECH)
						.foregroundColor(labelsColor));
		createParticlesTakenList(japaneseWord);

		additionalInformationLabel = japanesePanelComponentsStore
				.getElementsCreator()
				.createAdditionalInformationLabel(japaneseWord, labelsColor);
		additionalInformationValue = japanesePanelComponentsStore
				.getElementsCreator()
				.createComboboxForAdditionalInformation(japaneseWord);
		partOfSpeechCombobox = japanesePanelComponentsStore.getElementsCreator()
				.createComboboxForPartOfSpeech(japaneseWord.getPartOfSpeech(),
						additionalInformationLabel, additionalInformationValue,
						japaneseWord);
		lastWritingsListCreated = createWritingsList(japaneseWord, inputGoal,
				inheritScrollBar);
		writingsLabel = GuiElementsCreator.createLabel(
				new ComponentOptions().text(Labels.WRITING_WAYS_IN_JAPANESE)
						.foregroundColor(labelsColor));
		particlesTakenLabel = GuiElementsCreator.createLabel(
				new ComponentOptions().text(Labels.TAKING_PARTICLE)
						.foregroundColor(labelsColor));
	}

	private void createParticlesTakenList(JapaneseWord japaneseWord) {
		particlesTakenList = japanesePanelComponentsStore.getElementsCreator()
				.createParticlesDataList(japaneseWord, displayMode);
	}

	private void addActions(JapaneseWord japaneseWord, InputGoal inputGoal) {
		JapanesePanelActionsCreator actionCreatingService = japanesePanelComponentsStore
				.getActionCreator();
		if (displayMode.equals(PanelDisplayMode.VIEW)) {
			return;
		}
		actionCreatingService
				.addWordMeaningPropertyChangeListener(wordMeaningText,
						japaneseWord, inputGoal.equals(InputGoal.SEARCH) ?
								WordSearchOptions.BY_WORD_FRAGMENT :
								WordSearchOptions.BY_FULL_EXPRESSION,
						inputGoal);
	}

	private MyList<JapaneseWriting> createWritingsList(JapaneseWord
			japaneseWord,
			InputGoal inputGoal, boolean inheritScrollBar) {
		lastWritingsListCreated = createJapaneseWritingsList(japaneseWord,
				inheritScrollBar);
		writingsLists.add(new Pair<>(japaneseWord, lastWritingsListCreated));

		parentDialog.getPanel()
				.addNavigableByKeyboardList(lastWritingsListCreated);
		if (japaneseWord.getWritings().isEmpty()) {
			japaneseWord.addWritingsForKana("", "");
		}
		japaneseWord.getWritings().forEach(
				word -> lastWritingsListCreated.addWord(word, inputGoal));
		lastWritingsListCreated.addSwitchBetweenInputsFailListener(this);
		return lastWritingsListCreated;
	}

	@Override
	public void switchBetweenInputsFailed(JTextComponent input,
			MoveDirection direction) {
		if (direction.equals(MoveDirection.BELOW) && !displayMode
				.equals(PanelDisplayMode.VIEW)) {
			MyList<JapaneseWriting> writingsListToAddWriting = findListThatFailedInSwitchingBetweenInputs(
					input);
			writingsListToAddWriting.addWord(
					JapaneseWriting.getInitializer().initializeElement());
			writingsListToAddWriting.scrollToBottom();
		}
		if ((direction.equals(MoveDirection.LEFT) || direction
				.equals(MoveDirection.RIGHT)) && displayMode
				.equals(PanelDisplayMode.VIEW)) {
			MyList<JapaneseWriting> listThatFailed = findListThatFailedInSwitchingBetweenInputs(
					input);
			listThatFailed.getPanelWithSelectedInput().clearSelectedInput();
		}
	}

	private MyList<JapaneseWriting> findListThatFailedInSwitchingBetweenInputs(
			JTextComponent input) {
		JapaneseWord wordContainingInput = japanesePanelComponentsStore
				.getActionCreator().getWordContainingInput(input);
		MyList<JapaneseWriting> writingsListToAddWriting = null;
		if (wordContainingInput != null) {
			for (Pair<JapaneseWord, MyList<JapaneseWriting>> wordWithWritings : writingsLists) {
				if (wordWithWritings.getLeft().equals(wordContainingInput)) {
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

	private MyList<JapaneseWriting> createJapaneseWritingsList(
			JapaneseWord japaneseWord, boolean inheritScrollBar) {
		return new MyList<>(parentDialog, applicationController,
				new RowInJapaneseWritingsList(japanesePanelComponentsStore
						.getPanelCreatingService(displayMode), japaneseWord,
						displayMode), Labels.WRITING_WAYS_IN_JAPANESE,
				new ListConfiguration().enableWordAdding(false)
						.displayMode(displayMode)
						.inheritScrollbar(inheritScrollBar)
						.enableWordSearching(false)
						.parentListAndWordContainingThisList(
								applicationController.getJapaneseWords(),
								japaneseWord)
						.showButtonsLoadNextPreviousWords(false)
						.scrollBarFitsContent(false)
						.allInputsSelectionManager(listInputsSelectionManager)
						.skipTitle(true), JapaneseWriting.getInitializer());
	}

	private ListRowData<JapaneseWord> addElementsToPanel(
			MainPanel japaneseWordPanel, CommonListElements commonListElements,
			InputGoal inputGoal) {
		JPanel writingsListPanel = lastWritingsListCreated.getPanel();
		lastJapanesePanelMade = SimpleRowBuilder
				.createRowStartingFromColumn(0, FillType.NONE, Anchor.NORTH,
						commonListElements.getRowNumberLabel(),
						wordMeaningLabel, wordMeaningText)
				.nextRow(partOfSpeechLabel, partOfSpeechCombobox)
				.setColumnToPutRowInto(1)
				.nextRow(additionalInformationLabel, additionalInformationValue)
				.nextRow(writingsLabel, writingsListPanel)
				.nextRow(particlesTakenLabel, particlesTakenList.getPanel())
				.onlyAddIf(displayMode.equals(PanelDisplayMode.EDIT)
						|| particlesTakenList.getNumberOfWords() > 0)
				.nextRow(commonListElements.getButtonDelete())
				.onlyAddIf(!displayMode.equals(PanelDisplayMode.VIEW))
				.nextRow(commonListElements.getButtonEdit())
				.onlyAddIf(displayMode.equals(PanelDisplayMode.VIEW))
				.nextRow(commonListElements.getFinishEditing())
				.onlyAddIf(inputGoal.equals(InputGoal.EDIT_TEMPORARILY));
		japaneseWordPanel.addRowsOfElementsInColumn(lastJapanesePanelMade);
		ListRowDataCreator<JapaneseWord> rowDataCreator = new ListRowDataCreator<>(
				japaneseWordPanel);
		rowDataCreator
				.addPropertyData(ListPropertiesNames.JAPANESE_WORD_MEANING,
						wordMeaningText,
						japanesePanelComponentsStore.getActionCreator()
								.getWordMeaningChecker());

		rowDataCreator
				.addPropertyData(ListPropertiesNames.JAPANESE_WORD_WRITINGS,
						japanesePanelComponentsStore.getElementsCreator()
								.getKanaOrKanjiInputForFiltering(),
						japanesePanelComponentsStore.getActionCreator()
								.getWordCheckerForKanaOrKanjiFilter());
		return rowDataCreator.getListRowData();
	}

	public void focusMeaningTextfield() {
		SwingUtilities
				.invokeLater(() -> wordMeaningText.requestFocusInWindow());
	}

	public JapaneseWordPanelCreator copy() {
		JapaneseWordPanelCreator wordPanelCreator = new JapaneseWordPanelCreator(
				applicationController, parentDialog, PanelDisplayMode.EDIT);
		wordPanelCreator
				.setWordsList(japanesePanelComponentsStore.getWordsList());
		return wordPanelCreator;
	}

	public void addValidationListeners(
			Set<InputValidationListener<JapaneseWord>> validationListeners) {
		japanesePanelComponentsStore
				.addValidationListeners(validationListeners);
	}

	public void setWordsList(MyList<JapaneseWord> list) {
		japanesePanelComponentsStore.setWordsList(list);
	}
}

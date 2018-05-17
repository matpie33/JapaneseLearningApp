package com.kanji.list.listRows.japanesePanelCreatingComponents;

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
import com.kanji.constants.enums.InputGoal;
import com.kanji.constants.enums.WordSearchOptions;
import com.kanji.constants.strings.Labels;
import com.kanji.constants.strings.ListPropertiesNames;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.listRows.RowInJapaneseWritingsList;
import com.kanji.list.listeners.InputValidationListener;
import com.kanji.list.myList.ListConfiguration;
import com.kanji.list.myList.ListRowData;
import com.kanji.list.myList.ListRowDataCreator;
import com.kanji.list.myList.MyList;
import com.kanji.model.WordParticlesData;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.utilities.CommonGuiElementsCreator;
import com.kanji.utilities.CommonListElements;
import com.kanji.utilities.Pair;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JapaneseWordPanelCreator
		implements SwitchBetweenInputsFailListener {

	private JComboBox partOfSpeechCombobox;
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
	//TODO it's the second place where map did not fit due to mutable keys,
	//can we do better than list of pairs?

	public JapaneseWordPanelCreator(ApplicationController applicationController,
			DialogWindow parentDialog, PanelDisplayMode displayMode) {
		this.displayMode = displayMode;
		japanesePanelComponentsStore = new JapanesePanelComponentsStore(
				applicationController, parentDialog, displayMode);
		this.applicationController = applicationController;
		this.parentDialog = parentDialog;
		listInputsSelectionManager = new ListInputsSelectionManager();
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
		createElements(japaneseWord, inputGoal, inheritScrollBar);
		addActions(japaneseWord, inputGoal);
		return addElementsToPanel(existingPanel, commonListElements, inputGoal);

	}

	private void createElements(JapaneseWord japaneseWord, InputGoal inputGoal,
			boolean inheritScrollBar) {
		if (rowNumberLabel != null) {
			rowNumberLabel.setForeground(labelsColor);
		}
		wordMeaningLabel = GuiElementsCreator.createLabel(
				new ComponentOptions().text(Labels.WORD_MEANING)
						.foregroundColor(labelsColor));
		wordMeaningText = CommonGuiElementsCreator
				.createShortInput(japaneseWord.getMeaning());
		partOfSpeechLabel = GuiElementsCreator.createLabel(
				new ComponentOptions().text(Labels.PART_OF_SPEECH)
						.foregroundColor(labelsColor));
		createParticlesTakenList(japaneseWord);
		partOfSpeechCombobox = japanesePanelComponentsStore.getElementsMaker()
				.createComboboxForPartOfSpeech(japaneseWord.getPartOfSpeech());
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
		particlesTakenList = japanesePanelComponentsStore.getElementsMaker()
				.createParticlesDataList(japaneseWord);
	}

	private void addActions(JapaneseWord japaneseWord, InputGoal inputGoal) {
		JapanesePanelActionsCreator actionCreatingService = japanesePanelComponentsStore
				.getActionCreator();
		actionCreatingService
				.addWordMeaningPropertyChangeListener(wordMeaningText,
						japaneseWord, inputGoal.equals(InputGoal.SEARCH) ?
								WordSearchOptions.BY_WORD_FRAGMENT :
								WordSearchOptions.BY_FULL_EXPRESSION,
						inputGoal);
		actionCreatingService.addSavingOnSelectionListener(partOfSpeechCombobox,
				japaneseWord);
	}

	public MyList<JapaneseWriting> createWritingsList(JapaneseWord japaneseWord,
			InputGoal inputGoal, boolean inheritScrollBar) {
		lastWritingsListCreated = createJapaneseWritingsList(japaneseWord,
				inheritScrollBar);
		writingsLists.add(new Pair<>(japaneseWord, lastWritingsListCreated));
		lastWritingsListCreated.addSwitchBetweenInputsFailListener(this);
		parentDialog.getPanel()
				.addNavigableByKeyboardList(lastWritingsListCreated);
		if (japaneseWord.getWritings().isEmpty()) {
			japaneseWord.addWritingsForKana("", "");
		}
		japaneseWord.getWritings().stream().forEach(
				word -> lastWritingsListCreated.addWord(word, inputGoal));
		return lastWritingsListCreated;
	}

	@Override
	public void switchBetweenInputsFailed(JTextComponent input,
			MoveDirection direction) {
		if (direction.equals(MoveDirection.BELOW)) {
			JapaneseWord wordContainingInput = japanesePanelComponentsStore
					.getActionCreator().getWordContainingInput(input);
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

			writingsListToAddWriting.addWord(
					JapaneseWriting.getInitializer().initializeElement());
			writingsListToAddWriting.scrollToBottom();
		}
	}

	private MyList<JapaneseWriting> createJapaneseWritingsList(
			JapaneseWord japaneseWord, boolean inheritScrollBar) {
		return new MyList<>(parentDialog, applicationController,
				new RowInJapaneseWritingsList(
						japanesePanelComponentsStore.getPanelCreatingService(),
						japaneseWord, displayMode),
				Labels.WRITING_WAYS_IN_JAPANESE,
				new ListConfiguration().enableWordAdding(false)
						.displayMode(displayMode)
						.inheritScrollbar(inheritScrollBar)
						.enableWordSearching(false)
						.showButtonsLoadNextPreviousWords(false)
						.scrollBarFitsContent(!inheritScrollBar)
						.allInputsSelectionManager(listInputsSelectionManager)
						.skipTitle(true), JapaneseWriting.getInitializer());
	}

	private ListRowData<JapaneseWord> addElementsToPanel(
			MainPanel japaneseWordPanel, CommonListElements commonListElements,
			InputGoal inputGoal) {
		JPanel writingsListPanel = lastWritingsListCreated.getPanel();
		lastJapanesePanelMade = SimpleRowBuilder
				.createRowStartingFromColumn(0, FillType.BOTH,
						commonListElements.getRowNumberLabel(),
						wordMeaningLabel, wordMeaningText)
				.fillHorizontallySomeElements(wordMeaningText)
				.nextRow(partOfSpeechLabel, partOfSpeechCombobox)
				.setColumnToPutRowInto(1)
				.nextRow(writingsLabel, writingsListPanel)
				.nextRow(particlesTakenLabel, particlesTakenList.getPanel())
				.fillHorizontallySomeElements(writingsListPanel)
				.nextRow(commonListElements.getButtonDelete());
		japaneseWordPanel.addRowsOfElementsInColumn(lastJapanesePanelMade);
		ListRowDataCreator<Kanji> rowDataCreator = new ListRowDataCreator<>(
				japaneseWordPanel);
		if (inputGoal.equals(InputGoal.ADD) || inputGoal
				.equals(InputGoal.SEARCH)) {
			rowDataCreator
					.addPropertyData(ListPropertiesNames.JAPANESE_WORD_MEANING,
							lastJapanesePanelMade.getRowContainingComponent(
									wordMeaningLabel), Pair.of(wordMeaningText,
									japanesePanelComponentsStore
											.getActionCreator()
											.getWordMeaningChecker()));
			List<Pair<JTextComponent, ListElementPropertyManager<?, JapaneseWord>>> inputsWithPropertyManagersForJapaneseWritings = getWritingsInputsWithManagers();

			rowDataCreator
					.addPropertyData(ListPropertiesNames.JAPANESE_WORD_WRITINGS,
							lastJapanesePanelMade
									.getRowContainingComponent(writingsLabel),
							inputsWithPropertyManagersForJapaneseWritings
									.toArray(new Pair[] {}));
		}
		return rowDataCreator.getListRowData();
	}

	private List<Pair<JTextComponent, ListElementPropertyManager<?, JapaneseWord>>> getWritingsInputsWithManagers() {
		List<Pair<JTextComponent, ListElementPropertyManager<?, JapaneseWord>>> inputsWithPropertyManagers = new ArrayList<>();
		for (Map.Entry<JTextComponent, ListElementPropertyManager> textFieldWithPropertyManager : japanesePanelComponentsStore
				.getActionCreator().getInputManagersForInputs().entrySet()) {
			if (textFieldWithPropertyManager.getKey().equals(wordMeaningText)) {
				continue; //TODO refactor actions creator so that we dont have to exclude word meaning text
			}
			inputsWithPropertyManagers.add(Pair
					.of(textFieldWithPropertyManager.getKey(),
							textFieldWithPropertyManager.getValue()));

		}
		return inputsWithPropertyManagers;
	}

	public void focusMeaningTextfield() {
		SwingUtilities
				.invokeLater(() -> wordMeaningText.requestFocusInWindow());
	}

	public JapaneseWordPanelCreator copy() {
		return new JapaneseWordPanelCreator(applicationController, parentDialog,
				PanelDisplayMode.EDIT);
	}

	public void addValidationListeners(
			Set<InputValidationListener<JapaneseWord>> validationListeners) {
		japanesePanelComponentsStore
				.addValidationListeners(validationListeners);
	}
}

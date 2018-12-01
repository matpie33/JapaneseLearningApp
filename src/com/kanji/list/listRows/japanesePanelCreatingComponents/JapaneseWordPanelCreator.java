package com.kanji.list.listRows.japanesePanelCreatingComponents;

import com.guimaker.application.DialogWindow;
import com.guimaker.enums.InputGoal;
import com.guimaker.enums.MoveDirection;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.inputSelection.ListInputsSelectionManager;
import com.guimaker.list.ListRowData;
import com.guimaker.list.myList.ListRowDataCreator;
import com.guimaker.list.myList.MyList;
import com.guimaker.listeners.InputValidationListener;
import com.guimaker.listeners.SwitchBetweenInputsFailListener;
import com.guimaker.panels.MainPanel;
import com.guimaker.model.CommonListElements;
import com.guimaker.utilities.Pair;
import com.kanji.constants.strings.ListPropertiesNames;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.panelsAndControllers.controllers.ApplicationController;

import javax.swing.text.JTextComponent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JapaneseWordPanelCreator
		implements SwitchBetweenInputsFailListener {

	private MyList<JapaneseWriting> lastWritingsListCreated;
	private ApplicationController applicationController;
	private DialogWindow parentDialog;
	private JapanesePanelComponentsStore japanesePanelComponentsStore;
	private ListInputsSelectionManager listInputsSelectionManager;
	private PanelDisplayMode displayMode;
	private List<Pair<JapaneseWord, MyList<JapaneseWriting>>> writingsLists = new ArrayList<>();
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

	public ListRowData<JapaneseWord> createJapaneseWordPanel(
			JapaneseWord japaneseWord, InputGoal inputGoal,
			CommonListElements<JapaneseWord> commonListElements) {
		determineDisplayMode(inputGoal);
		return createRowPanel(japaneseWord, inputGoal, commonListElements);

	}

	private void determineDisplayMode(InputGoal inputGoal) {
		if (inputGoal.equals(InputGoal.NO_INPUT)) {
			displayMode = PanelDisplayMode.VIEW;
		}
		else {
			displayMode = PanelDisplayMode.EDIT;
		}
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

	private ListRowData<JapaneseWord> createRowPanel(
			JapaneseWord japaneseWord, InputGoal inputGoal,
			CommonListElements<JapaneseWord> commonListElements) {
		JapaneseWordPanel japaneseWordPanel = new JapaneseWordPanel(
				japanesePanelComponentsStore.getElementsCreator(), parentDialog,
				listInputsSelectionManager, this);
		ListRowDataCreator<JapaneseWord> rowDataCreator = createListRow(
				japaneseWord, inputGoal, commonListElements, japaneseWordPanel);

		return rowDataCreator.getListRowData();
	}

	private ListRowDataCreator<JapaneseWord> createListRow(
			JapaneseWord japaneseWord, InputGoal inputGoal,
			CommonListElements<JapaneseWord> commonListElements,
			JapaneseWordPanel japaneseWordPanel) {
		MainPanel rowPanel = japaneseWordPanel.createElements(japaneseWord,
				displayMode, inputGoal, commonListElements,
				japanesePanelComponentsStore.getPanelCreatingService(
						displayMode));
		ListRowDataCreator<JapaneseWord> rowDataCreator = new ListRowDataCreator<>(
				rowPanel);
		rowDataCreator.addPropertyData(
				ListPropertiesNames.JAPANESE_WORD_MEANING,
				japaneseWordPanel.getWordMeaningText(),
				japanesePanelComponentsStore.getActionCreator()
											.getWordMeaningChecker());

		rowDataCreator.addPropertyData(
				ListPropertiesNames.JAPANESE_WORD_WRITINGS,
				japanesePanelComponentsStore.getElementsCreator()
											.getKanaOrKanjiInputForFiltering(),
				japanesePanelComponentsStore.getActionCreator()
											.getWordCheckerForKanaOrKanjiFilter());
		return rowDataCreator;
	}

	public JapaneseWordPanelCreator copy() {
		JapaneseWordPanelCreator wordPanelCreator = new JapaneseWordPanelCreator(
				applicationController, parentDialog, PanelDisplayMode.EDIT);
		wordPanelCreator.setWordsList(
				japanesePanelComponentsStore.getWordsList());
		return wordPanelCreator;
	}

	public void setWordsList(MyList<JapaneseWord> list) {
		japanesePanelComponentsStore.setWordsList(list);
	}

	public MyList<JapaneseWriting> addWritings(
			MyList<JapaneseWriting> japaneseWritingsList,
			JapaneseWord japaneseWord, InputGoal inputGoal) {
		this.lastWritingsListCreated = japaneseWritingsList;
		writingsLists.add(new Pair<>(japaneseWord, lastWritingsListCreated));

		if (!japaneseWord.isEmpty()) {
			parentDialog.getPanel()
						.addNavigableByKeyboardList(lastWritingsListCreated);
		}

		if (japaneseWord.getWritings()
						.isEmpty()) {
			japaneseWord.addWritingsForKana("", "");
		}
		Set<JapaneseWriting> writings = japaneseWord.getWritings();
		Set<JapaneseWriting> duplicatedWritings = new HashSet<>(writings);
		duplicatedWritings
					.forEach(word -> lastWritingsListCreated.addWord(word,
							inputGoal));
		lastWritingsListCreated.addSwitchBetweenInputsFailListener(this);
		return lastWritingsListCreated;
	}
}

package com.kanji.list.listRows.japanesePanelCreatingComponents;

import com.guimaker.application.DialogWindow;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.InputGoal;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.inputSelection.ListInputsSelectionManager;
import com.guimaker.list.ListRowData;
import com.guimaker.list.myList.ListRowDataCreator;
import com.guimaker.list.myList.MyList;
import com.guimaker.model.CommonListElements;
import com.guimaker.model.PanelConfiguration;
import com.guimaker.panels.mainPanel.MainPanel;
import com.guimaker.row.ComplexRow;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.strings.ListPropertiesNames;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listElements.WordParticlesData;
import com.kanji.list.listRows.japanesePanelCreatingService.JapanesePanelCreatingService;
import com.kanji.panelsAndControllers.controllers.ApplicationController;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class JapaneseWordPanel {

	private Color defaultLabelsColor = Color.WHITE;
	private DialogWindow parentDialog;
	private ListInputsSelectionManager listInputsSelectionManager;
	private JapaneseWordPanelCreator japaneseWordPanelCreator;
	private JTextComponent wordMeaningText;
	private JapanesePanelComponentsStore componentsStore;

	public JapaneseWordPanel(DialogWindow parentDialog,
			PanelDisplayMode panelDisplayMode,
			ApplicationController applicationController) {
		this.parentDialog = parentDialog;
		this.listInputsSelectionManager = new ListInputsSelectionManager();
		componentsStore = new JapanesePanelComponentsStore(
				applicationController, parentDialog);
		this.japaneseWordPanelCreator = new JapaneseWordPanelCreator(
				applicationController.getApplicationWindow(), panelDisplayMode,
				getActionsCreator(), listInputsSelectionManager);
	}

	public JapanesePanelActionsCreator getActionsCreator() {
		return componentsStore.getActionCreator();
	}

	public ListRowData<JapaneseWord> createElements(JapaneseWord japaneseWord,
			InputGoal inputGoal,
			CommonListElements<JapaneseWord> commonListElements) {

		JLabel rowNumberLabel = commonListElements.getRowNumberLabel();
		if (rowNumberLabel != null) {
			rowNumberLabel.setForeground(
					commonListElements.getLabelsColor() != null ?
							commonListElements.getLabelsColor() :
							defaultLabelsColor);
		}
		PanelDisplayMode displayMode = determineDisplayMode(inputGoal);
		JapanesePanelElementsCreator elementsCreator = componentsStore.getElementsCreator();
		JLabel wordMeaningLabel = elementsCreator.createWordMeaningLabel(
				defaultLabelsColor);
		wordMeaningText = elementsCreator.createWordMeaningText(japaneseWord,
				displayMode, inputGoal, commonListElements);
		JLabel partOfSpeechLabel = elementsCreator.createPartOfSpeechLabel(
				defaultLabelsColor);
		MyList<WordParticlesData> particlesTakenList = elementsCreator.createParticlesDataList(
				japaneseWord, displayMode, commonListElements);
		JLabel additionalInformationLabel = elementsCreator.createAdditionalInformationLabel(
				japaneseWord, defaultLabelsColor);
		JComboBox additionalInformationCombobox = elementsCreator.createComboboxForAdditionalInformation(
				japaneseWord);
		JComboBox<String> partOfSpeechCombobox = elementsCreator.createComboboxForPartOfSpeech(
				japaneseWord.getPartOfSpeech(), additionalInformationLabel,
				additionalInformationCombobox, japaneseWord);
		boolean inheritScrollbar = !inputGoal.equals(InputGoal.ADD);
		MyList writingsList = createJapaneseWritingsList(japaneseWord,
				displayMode, inheritScrollbar,
				componentsStore.getPanelCreatingService(displayMode), inputGoal,
				commonListElements);
		JLabel writingsLabel = elementsCreator.createWritingsLabel(
				defaultLabelsColor);
		JLabel particlesTakenLabel = elementsCreator.createParticlesTakenLabel(
				defaultLabelsColor);

		MainPanel panel = new MainPanel(
				new PanelConfiguration().setPanelDisplayMode(displayMode));

		ComplexRow lastJapanesePanelMade = SimpleRowBuilder.createRowStartingFromColumn(
				0, FillType.NONE, Anchor.WEST,
				commonListElements.getRowNumberLabel(), wordMeaningLabel,
				wordMeaningText)
														   .nextRow(
																   partOfSpeechLabel,
																   partOfSpeechCombobox)
														   .setColumnToPutRowInto(
																   1)
														   .nextRow(
																   additionalInformationLabel,
																   additionalInformationCombobox)
														   .nextRow(
																   writingsLabel,
																   writingsList.getPanel())
														   .nextRow(
																   particlesTakenLabel,
																   particlesTakenList.getPanel())
														   .onlyAddIf(
																   displayMode.equals(
																		   PanelDisplayMode.EDIT)
																		   ||
																		   particlesTakenList.getNumberOfWords()
																				   > 0)
														   .nextRow(
																   commonListElements.getButtonDelete())
														   .onlyAddIf(
																   !displayMode.equals(
																		   PanelDisplayMode.VIEW))
														   .nextRow(
																   commonListElements.getButtonEdit())
														   .onlyAddIf(
																   displayMode.equals(
																		   PanelDisplayMode.VIEW))
														   .nextRow(
																   commonListElements.getFinishEditing())
														   .onlyAddIf(
																   inputGoal.equals(
																		   InputGoal.EDIT_TEMPORARILY));
		panel.addRowsOfElementsInColumn(lastJapanesePanelMade);
		return createListRow(panel);

	}

	private ListRowData<JapaneseWord> createListRow(MainPanel rowPanel) {

		ListRowDataCreator<JapaneseWord> rowDataCreator = new ListRowDataCreator<>(
				rowPanel);
		rowDataCreator.addPropertyData(
				ListPropertiesNames.JAPANESE_WORD_MEANING, wordMeaningText,
				componentsStore.getActionCreator()
							   .getWordMeaningChecker());

		rowDataCreator.addPropertyData(
				ListPropertiesNames.JAPANESE_WORD_WRITINGS,
				componentsStore.getElementsCreator()
							   .getKanaOrKanjiInputForFiltering(),
				componentsStore.getActionCreator()
							   .getWordCheckerForKanaOrKanjiFilter());
		return rowDataCreator.getListRowData();
	}

	private MyList<JapaneseWriting> createJapaneseWritingsList(
			JapaneseWord japaneseWord, PanelDisplayMode displayMode,
			boolean inheritScrollBar,
			JapanesePanelCreatingService panelCreatingService,
			InputGoal inputGoal,
			CommonListElements<JapaneseWord> commonListElements) {
		return japaneseWordPanelCreator.addWritings(
				componentsStore.getElementsCreator()
							   .createJapaneseWritingsList(japaneseWord,
									   inheritScrollBar, parentDialog,
									   displayMode, listInputsSelectionManager,
									   panelCreatingService,
									   commonListElements), japaneseWord,
				inputGoal);
	}

	public JTextComponent getWordMeaningText() {
		return wordMeaningText;
	}

	public PanelDisplayMode determineDisplayMode(InputGoal inputGoal) {
		PanelDisplayMode displayMode;
		if (inputGoal.equals(InputGoal.NO_INPUT)) {
			displayMode = PanelDisplayMode.VIEW;
		}
		else {
			displayMode = PanelDisplayMode.EDIT;
		}
		return displayMode;
	}

	public JapaneseWordPanelCreator getJapaneseWordPanelCreator() {
		return japaneseWordPanelCreator;
	}

	public void setWordsList(MyList<JapaneseWord> wordsList){
		componentsStore.getActionCreator().setWordsList(wordsList);
	}

}

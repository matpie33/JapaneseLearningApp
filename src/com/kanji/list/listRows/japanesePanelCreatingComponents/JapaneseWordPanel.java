package com.kanji.list.listRows.japanesePanelCreatingComponents;

import com.guimaker.application.DialogWindow;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.InputGoal;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.inputSelection.ListInputsSelectionManager;
import com.guimaker.list.myList.MyList;
import com.guimaker.model.PanelConfiguration;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.ComplexRow;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.model.CommonListElements;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listElements.WordParticlesData;
import com.kanji.list.listRows.japanesePanelCreatingService.JapanesePanelCreatingService;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class JapaneseWordPanel {

	private Color defaultLabelsColor = Color.WHITE;
	private JapanesePanelElementsCreator elementsCreator;
	private DialogWindow parentDialog;
	private ListInputsSelectionManager listInputsSelectionManager;
	private JapaneseWordPanelCreator japaneseWordPanelCreator;
	private JTextComponent wordMeaningText;

	public JapaneseWordPanel(JapanesePanelElementsCreator elementsCreator,
			DialogWindow parentDialog,
			ListInputsSelectionManager listInputsSelectionManager,
			JapaneseWordPanelCreator japaneseWordPanelCreator) {
		this.elementsCreator = elementsCreator;
		this.parentDialog = parentDialog;
		this.listInputsSelectionManager = listInputsSelectionManager;
		this.japaneseWordPanelCreator = japaneseWordPanelCreator;
	}

	public MainPanel createElements(JapaneseWord japaneseWord,
			PanelDisplayMode displayMode, InputGoal inputGoal,
			CommonListElements commonListElements,
			JapanesePanelCreatingService panelCreatingService) {

		JLabel rowNumberLabel = commonListElements.getRowNumberLabel();
		if (rowNumberLabel != null) {
			rowNumberLabel.setForeground(
					commonListElements.getLabelsColor() != null ?
							commonListElements.getLabelsColor() :
							defaultLabelsColor);
		}
		JLabel wordMeaningLabel = elementsCreator.createWordMeaningLabel(
				defaultLabelsColor);
		wordMeaningText = elementsCreator.createWordMeaningText(
				japaneseWord, displayMode, inputGoal);
		JLabel partOfSpeechLabel = elementsCreator.createPartOfSpeechLabel(
				defaultLabelsColor);
		MyList<WordParticlesData> particlesTakenList = elementsCreator.createParticlesDataList(
				japaneseWord, displayMode);
		JLabel additionalInformationLabel = elementsCreator.createAdditionalInformationLabel(
				japaneseWord, defaultLabelsColor);
		JComboBox additionalInformationCombobox = elementsCreator.createComboboxForAdditionalInformation(
				japaneseWord);
		JComboBox<String> partOfSpeechCombobox = elementsCreator.createComboboxForPartOfSpeech(
				japaneseWord.getPartOfSpeech(), additionalInformationLabel,
				additionalInformationCombobox, japaneseWord);
		boolean inheritScrollbar = !commonListElements.isForSingleRowOnly();
		MyList writingsList = createJapaneseWritingsList(
				japaneseWord, displayMode, inheritScrollbar,
				panelCreatingService, inputGoal);
		JLabel writingsLabel = elementsCreator.createWritingsLabel(
				defaultLabelsColor);
		JLabel particlesTakenLabel = elementsCreator.createParticlesTakenLabel(
				defaultLabelsColor);

		MainPanel panel = new MainPanel(
				new PanelConfiguration().setPanelDisplayMode(displayMode));

		ComplexRow lastJapanesePanelMade = SimpleRowBuilder.createRowStartingFromColumn(
				0, FillType.NONE, Anchor.NORTH,
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
		return panel;

	}

	private MyList<JapaneseWriting> createJapaneseWritingsList(
			JapaneseWord japaneseWord, PanelDisplayMode displayMode,
			boolean inheritScrollBar,
			JapanesePanelCreatingService panelCreatingService,
			InputGoal inputGoal) {
		return japaneseWordPanelCreator.addWritings(
				elementsCreator.createJapaneseWritingsList(japaneseWord,
						inheritScrollBar, parentDialog, displayMode,
						listInputsSelectionManager, panelCreatingService),
				japaneseWord, inputGoal);
	}

	public JTextComponent getWordMeaningText() {
		return wordMeaningText;
	}
}

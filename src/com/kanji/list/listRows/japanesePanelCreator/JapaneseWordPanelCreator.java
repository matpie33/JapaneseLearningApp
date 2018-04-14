package com.kanji.list.listRows.japanesePanelCreator;

import com.guimaker.enums.FillType;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.JapanesePanelDisplayMode;
import com.kanji.constants.strings.Labels;
import com.kanji.list.listElementPropertyManagers.JapaneseWordMeaningChecker;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listRows.RowInJapaneseWritingsList;
import com.kanji.list.listRows.japanesePanelActionsCreator.JapanesePanelEditOrAddModeAction;
import com.kanji.list.myList.ListConfiguration;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.utilities.CommonGuiElementsMaker;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class JapaneseWordPanelCreator {

	private JComboBox partOfSpeechCombobox;
	private JTextComponent wordMeaningText;
	private JLabel wordMeaningLabel;
	private JLabel partOfSpeechLabel;
	private JLabel writingsLabel;
	private JLabel rowNumberLabel;
	private MyList<JapaneseWriting> writingsList;
	private ApplicationController applicationController;
	private Color labelsColor = Color.WHITE;
	private DialogWindow parentDialog;
	private JapanesePanelServiceStore japanesePanelServiceStore;

	public JapaneseWordPanelCreator(ApplicationController applicationController,
			DialogWindow parentDialog, JapanesePanelDisplayMode displayMode) {
		japanesePanelServiceStore = new JapanesePanelServiceStore(
				applicationController, parentDialog, displayMode);
		this.applicationController = applicationController;
		this.parentDialog = parentDialog;
	}

	public void setRowNumberLabel(JLabel label) {
		rowNumberLabel = label;
	}

	public void setLabelsColor(Color color) {
		labelsColor = color;
	}

	public void addJapanesePanelToExistingPanel(MainPanel existingPanel,
			JapaneseWordInformation japaneseWordInformation) {
		japanesePanelServiceStore.getPanelCreatingService()
				.setWord(japaneseWordInformation);
		createElements(japaneseWordInformation);
		addActions(japaneseWordInformation);
		addElementsToPanel(existingPanel);
	}

	private void createElements(
			JapaneseWordInformation japaneseWordInformation) {
		if (rowNumberLabel != null) {
			rowNumberLabel.setForeground(labelsColor);
		}
		wordMeaningLabel = GuiMaker.createLabel(
				new ComponentOptions().text(Labels.WORD_MEANING)
						.foregroundColor(labelsColor));
		wordMeaningText = CommonGuiElementsMaker
				.createShortInput(japaneseWordInformation.getWordMeaning());
		partOfSpeechLabel = GuiMaker.createLabel(
				new ComponentOptions().text(Labels.PART_OF_SPEECH)
						.foregroundColor(labelsColor));
		partOfSpeechCombobox = japanesePanelServiceStore.getElementsMaker()
				.createComboboxForPartOfSpeech(
						japaneseWordInformation.getPartOfSpeech());
		writingsList = createWritingsList(japaneseWordInformation);
		writingsLabel = GuiMaker.createLabel(
				new ComponentOptions().text(Labels.WRITING_WAYS_IN_JAPANESE)
						.foregroundColor(labelsColor));
	}

	private void addActions(JapaneseWordInformation japaneseWordInformation) {
		JapanesePanelEditOrAddModeAction actionCreatingService = japanesePanelServiceStore
				.getActionMaker();
		//TODO do it only if its called for search or add panel
		JapaneseWordMeaningChecker meaningChecker = actionCreatingService
				.addWordMeaningTextFieldListeners(wordMeaningText,
						japaneseWordInformation);
		actionCreatingService.addPartOfSpeechListener(partOfSpeechCombobox,
				japaneseWordInformation);
	}

	public MyList<JapaneseWriting> createWritingsList(
			JapaneseWordInformation japaneseWordInformation) {
		writingsList = createJapaneseWritingsList();
		japaneseWordInformation.getJapaneseWritings().stream()
				.forEach(writingsList::addWord);
		return writingsList;
	}

	private MyList<JapaneseWriting> createJapaneseWritingsList() {
		return new MyList<>(parentDialog, applicationController,
				new RowInJapaneseWritingsList(
						japanesePanelServiceStore.getPanelCreatingService()),
				Labels.WRITING_WAYS_IN_JAPANESE,
				new ListConfiguration().enableWordAdding(false)
						.inheritScrollbar(true).enableWordSearching(false)
						.showButtonsLoadNextPreviousWords(false)
						.skipTitle(true), JapaneseWriting.getInitializer());
	}

	private void addElementsToPanel(MainPanel japaneseWordPanel) {
		JPanel writingsListPanel = writingsList.getPanel();
		japaneseWordPanel.addRowsOfElementsInColumnStartingFromColumn(
				SimpleRowBuilder
						.createRowStartingFromColumn(0, FillType.HORIZONTAL,
								rowNumberLabel, wordMeaningLabel,
								wordMeaningText)
						.fillHorizontallySomeElements(wordMeaningText)
						.nextRow(partOfSpeechLabel, partOfSpeechCombobox)
						.setColumnToPutRowInto(1)
						.fillHorizontallySomeElements(partOfSpeechCombobox)
						.nextRow(writingsLabel, writingsListPanel)
						.fillHorizontallySomeElements(writingsListPanel));
	}

	public TextFieldSelectionHandler getSelectionHandler() {
		return japanesePanelServiceStore.getSelectionHandler();
	}

	public void focusMeaningTextfield() {
		SwingUtilities
				.invokeLater(() -> wordMeaningText.requestFocusInWindow());
	}

}

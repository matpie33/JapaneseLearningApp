package com.kanji.list.listRows.japanesePanelCreator;

import com.guimaker.enums.FillType;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.NextRow;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.JapanesePanelDisplayMode;
import com.kanji.constants.strings.Labels;
import com.kanji.constants.strings.ListPropertiesNames;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.listRows.RowInJapaneseWritingsList;
import com.kanji.list.listRows.japanesePanelActionsCreator.JapanesePanelEditOrAddModeAction;
import com.kanji.list.myList.ListConfiguration;
import com.kanji.list.myList.ListPropertyInformation;
import com.kanji.list.myList.ListRowData;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.utilities.CommonGuiElementsMaker;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

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

	public ListRowData addJapanesePanelToExistingPanel(MainPanel existingPanel,
			JapaneseWord japaneseWord, boolean forSearchPanel) {
		if (forSearchPanel) {
			//TODO can I do it better?
			setIsForSearchDialog(true);
		}
		ListRowData listRowData = addPanel(existingPanel, japaneseWord,
				forSearchPanel);
		if (forSearchPanel) {
			setIsForSearchDialog(false);
		}
		return listRowData;
	}

	public ListRowData addPanel(MainPanel existingPanel,
			JapaneseWord japaneseWord, boolean forSearchPanel) {
		createElements(japaneseWord);
		addActions(japaneseWord);
		return addElementsToPanel(existingPanel, forSearchPanel);
	}

	private void setIsForSearchDialog(boolean isForSearchDialog) {
		japanesePanelServiceStore.getActionCreator()
				.setIsForSearchDialog(isForSearchDialog);
		japanesePanelServiceStore.getActionMaker().setIsForSearchDialog(isForSearchDialog);
	}

	private void createElements(JapaneseWord japaneseWord) {
		if (rowNumberLabel != null) {
			rowNumberLabel.setForeground(labelsColor);
		}
		wordMeaningLabel = GuiMaker.createLabel(
				new ComponentOptions().text(Labels.WORD_MEANING)
						.foregroundColor(labelsColor));
		wordMeaningText = CommonGuiElementsMaker
				.createShortInput(japaneseWord.getMeaning());
		partOfSpeechLabel = GuiMaker.createLabel(
				new ComponentOptions().text(Labels.PART_OF_SPEECH)
						.foregroundColor(labelsColor));
		partOfSpeechCombobox = japanesePanelServiceStore.getElementsMaker()
				.createComboboxForPartOfSpeech(japaneseWord.getPartOfSpeech());
		writingsList = createWritingsList(japaneseWord);
		writingsLabel = GuiMaker.createLabel(
				new ComponentOptions().text(Labels.WRITING_WAYS_IN_JAPANESE)
						.foregroundColor(labelsColor));
	}

	private void addActions(JapaneseWord japaneseWord) {
		JapanesePanelEditOrAddModeAction actionCreatingService = japanesePanelServiceStore
				.getActionMaker();
		actionCreatingService.addWordMeaningTextFieldListeners(wordMeaningText,
				japaneseWord);
		actionCreatingService
				.addPartOfSpeechListener(partOfSpeechCombobox, japaneseWord);
	}

	public MyList<JapaneseWriting> createWritingsList(
			JapaneseWord japaneseWord) {
		japanesePanelServiceStore.getPanelCreatingService()
				.setWord(japaneseWord);
		writingsList = createJapaneseWritingsList();
		japaneseWord.getWritings().stream().forEach(writingsList::addWord);
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

	private ListRowData addElementsToPanel(MainPanel japaneseWordPanel,
			boolean forSearchPanel) {
		JPanel writingsListPanel = writingsList.getPanel();
		NextRow japaneseWordsPanel = SimpleRowBuilder
				.createRowStartingFromColumn(0, FillType.HORIZONTAL,
						rowNumberLabel, wordMeaningLabel, wordMeaningText)
				.fillHorizontallySomeElements(wordMeaningText)
				.nextRow(partOfSpeechLabel, partOfSpeechCombobox)
				.setColumnToPutRowInto(1)
				.fillHorizontallySomeElements(partOfSpeechCombobox)
				.nextRow(writingsLabel, writingsListPanel)
				.fillHorizontallySomeElements(writingsListPanel);
		japaneseWordPanel.addRowsOfElementsInColumnStartingFromColumn(
				japaneseWordsPanel);
		ListRowData rowData = new ListRowData(japaneseWordPanel);
		if (forSearchPanel) {
			createPropertiesData(japaneseWordsPanel, rowData);
		}
		return rowData;
	}

	private void createPropertiesData(NextRow japaneseWordsPanel,
			ListRowData rowData) {

		Map<String, ListPropertyInformation> propertiesData = new HashMap<>();

		Map<JTextComponent, ListElementPropertyManager> allTextFieldsWithPropertyManagers = japanesePanelServiceStore
				.getActionCreator().getTextFieldsWithPropertyManagers();

		Map<JTextComponent, ListElementPropertyManager<?, Kanji>> meaningInputWithPropertyManager = new HashMap<>();
		meaningInputWithPropertyManager.put(wordMeaningText,
				allTextFieldsWithPropertyManagers.get(wordMeaningText));
		propertiesData.put(ListPropertiesNames.JAPANESE_WORD_MEANING,
				new ListPropertyInformation(
						japaneseWordsPanel.getAllRows().get(0),
						meaningInputWithPropertyManager));

		Map<JTextComponent, ListElementPropertyManager<?, Kanji>> japaneseWritingsInputWithPropertyManagers = new HashMap<>();
		for (Map.Entry<JTextComponent, ListElementPropertyManager> textFieldWithPropertyManager : allTextFieldsWithPropertyManagers
				.entrySet()) {
			if (textFieldWithPropertyManager.getKey().equals(wordMeaningText)) {
				continue;
			}
			japaneseWritingsInputWithPropertyManagers
					.put(textFieldWithPropertyManager.getKey(),
							textFieldWithPropertyManager.getValue());

		}

		propertiesData.put(ListPropertiesNames.JAPANESE_WORD_WRITINGS,
				new ListPropertyInformation(
						japaneseWordsPanel.getAllRows().get(2),
						japaneseWritingsInputWithPropertyManagers));

		rowData.setRowPropertiesData(propertiesData);
	}

	public TextFieldSelectionHandler getSelectionHandler() {
		return japanesePanelServiceStore.getSelectionHandler();
	}

	public void focusMeaningTextfield() {
		SwingUtilities
				.invokeLater(() -> wordMeaningText.requestFocusInWindow());
	}

}

package com.kanji.list.listRows.japanesePanelCreatingComponents;

import com.guimaker.enums.FillType;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.ComplexRow;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.JapanesePanelDisplayMode;
import com.kanji.constants.enums.WordSearchOptions;
import com.kanji.constants.strings.Labels;
import com.kanji.constants.strings.ListPropertiesNames;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listRows.RowInJapaneseWritingsList;
import com.kanji.list.myList.ListConfiguration;
import com.kanji.list.myList.ListPropertyInformation;
import com.kanji.list.myList.ListRowData;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.utilities.CommonGuiElementsCreator;
import com.kanji.utilities.CommonListElements;
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
	private JapanesePanelComponentsStore japanesePanelComponentsStore;
	private ComplexRow lastJapanesePanelMade;

	public JapaneseWordPanelCreator(ApplicationController applicationController,
			DialogWindow parentDialog, JapanesePanelDisplayMode displayMode) {
		japanesePanelComponentsStore = new JapanesePanelComponentsStore(
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
			JapaneseWord japaneseWord, boolean forSearchPanel,
			CommonListElements commonListElements, boolean inheritScrollBar) {
		createElements(japaneseWord, forSearchPanel, inheritScrollBar);
		addActions(japaneseWord, forSearchPanel);
		addElementsToPanel(existingPanel, commonListElements);
	}

	private void createElements(JapaneseWord japaneseWord,
			boolean forSearchPanel, boolean inheritScrollBar) {
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
		partOfSpeechCombobox = japanesePanelComponentsStore.getElementsMaker()
				.createComboboxForPartOfSpeech(japaneseWord.getPartOfSpeech());
		writingsList = createWritingsList(japaneseWord, forSearchPanel,
				inheritScrollBar);
		writingsLabel = GuiElementsCreator.createLabel(
				new ComponentOptions().text(Labels.WRITING_WAYS_IN_JAPANESE)
						.foregroundColor(labelsColor));
	}

	private void addActions(JapaneseWord japaneseWord,
			boolean forSearchDialog) {
		JapanesePanelActionsCreator actionCreatingService = japanesePanelComponentsStore
				.getActionCreator();
		actionCreatingService
				.addWordMeaningPropertyChangeListener(wordMeaningText,
						japaneseWord, forSearchDialog ?
								WordSearchOptions.BY_WORD_FRAGMENT :
								WordSearchOptions.BY_FULL_EXPRESSION,
						!forSearchDialog);
		actionCreatingService.addSavingOnSelectionListener(partOfSpeechCombobox,
				japaneseWord);
	}

	public MyList<JapaneseWriting> createWritingsList(JapaneseWord japaneseWord,
			boolean forSearchPanel, boolean inheritScrollBar) {
		writingsList = createJapaneseWritingsList(japaneseWord,
				inheritScrollBar);
		japaneseWord.getWritings().stream()
				.forEach(word -> writingsList.addWord(word, forSearchPanel));
		return writingsList;
	}

	private MyList<JapaneseWriting> createJapaneseWritingsList(
			JapaneseWord japaneseWord, boolean inheritScrollBar) {
		return new MyList<>(parentDialog, applicationController,
				new RowInJapaneseWritingsList(
						japanesePanelComponentsStore.getPanelCreatingService(),
						japaneseWord), Labels.WRITING_WAYS_IN_JAPANESE,
				new ListConfiguration().enableWordAdding(false)
						.inheritScrollbar(inheritScrollBar)
						.enableWordSearching(false)
						.showButtonsLoadNextPreviousWords(false)
						.scrollBarFitsContent(!inheritScrollBar)
						.skipTitle(true), JapaneseWriting.getInitializer());
	}

	private void addElementsToPanel(MainPanel japaneseWordPanel,
			CommonListElements commonListElements) {
		JPanel writingsListPanel = writingsList.getPanel();
		lastJapanesePanelMade = SimpleRowBuilder
				.createRowStartingFromColumn(0, FillType.BOTH,
						commonListElements.getRowNumberLabel(),
						wordMeaningLabel, wordMeaningText)
				.fillHorizontallySomeElements(wordMeaningText)
				.nextRow(partOfSpeechLabel, partOfSpeechCombobox)
				.setColumnToPutRowInto(1)
				.fillHorizontallySomeElements(partOfSpeechCombobox)
				.nextRow(writingsLabel, writingsListPanel)
				.fillHorizontallySomeElements(writingsListPanel)
				.nextRow(commonListElements.getButtonDelete());
		japaneseWordPanel.addRowsOfElementsInColumnStartingFromColumn(
				lastJapanesePanelMade);
	}

	public ListRowData getRowData() {

		ListRowData rowData = new ListRowData();
		Map<String, ListPropertyInformation> propertiesData = new HashMap<>();

		Map<JTextComponent, ListElementPropertyManager> allTextFieldsWithPropertyManagers = japanesePanelComponentsStore
				.getActionCreator().getInputManagersForInputs();

		Map<JTextComponent, ListElementPropertyManager<?, JapaneseWord>> meaningInputWithPropertyManager = new HashMap<>();
		meaningInputWithPropertyManager.put(wordMeaningText,
				japanesePanelComponentsStore.getActionCreator()
						.getWordMeaningChecker());
		propertiesData.put(ListPropertiesNames.JAPANESE_WORD_MEANING,
				new ListPropertyInformation(
						lastJapanesePanelMade.getAllRows().get(0),
						meaningInputWithPropertyManager));

		Map<JTextComponent, ListElementPropertyManager<?, JapaneseWord>> japaneseWritingsInputWithPropertyManagers = new HashMap<>();
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
						lastJapanesePanelMade.getAllRows().get(2),
						japaneseWritingsInputWithPropertyManagers));

		rowData.setRowPropertiesData(propertiesData);
		return rowData;
	}

	public TextFieldSelectionHandler getSelectionHandler() {
		return japanesePanelComponentsStore.getSelectionHandler();
	}

	public void focusMeaningTextfield() {
		SwingUtilities
				.invokeLater(() -> wordMeaningText.requestFocusInWindow());
	}

}

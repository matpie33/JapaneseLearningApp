package com.kanji.list.listRows.japanesePanelCreator;

import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.kanji.constants.strings.Labels;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listRows.RowInJapaneseWritingsList;
import com.kanji.list.listRows.japanesePanelActionsCreator.JapanesePanelActionCreatingService;
import com.kanji.list.myList.ListConfiguration;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.utilities.CommonGuiElementsMaker;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.ArrayList;

public class JapaneseWordPanelCreator {

	private JComboBox partOfSpeechCombobox;
	private JTextComponent wordMeaningText;
	private JLabel wordMeaningLabel;
	private JLabel partOfSpeechLabel;
	private JLabel writingsLabel;
	private JLabel rowLabel;
	private MyList<JapaneseWriting> writingsList;
	private ApplicationController applicationController;
	private JapanesePanelActionCreatingService actionCreatingService;
	private JapanesePanelElementsMaker elementsMaker;

	public JapaneseWordPanelCreator(ApplicationController applicationController,
			JapanesePanelActionCreatingService actionCreatingService,
			JapanesePanelElementsMaker elementsMaker) {
		this.applicationController = applicationController;
		this.actionCreatingService = actionCreatingService;
		this.elementsMaker = elementsMaker;
	}

	//TODO create a map: listpanel display mode -> listpanel creating service
	// then don't pass action and elements maker, instead pick it based on the list panel mode
	public void addJapanesePanelToExistingPanel(MainPanel existingPanel,
			JapaneseWordInformation japaneseWordInformation,
			JapanesePanelRowCreatingService panelCreatingService,
			DialogWindow parentDialog) {
		createElements(japaneseWordInformation, panelCreatingService,
				parentDialog);
		addActions(japaneseWordInformation);
		addElementsToPanel(existingPanel);
	}

	public JapanesePanelElementsMaker getElementsMaker(){
		return elementsMaker;
		//TODO won't be needed after mapping display mode to service
	}

	private void createElements(JapaneseWordInformation japaneseWordInformation,
			JapanesePanelRowCreatingService panelCreatingService,
			DialogWindow parentDialog) {
		rowLabel = panelCreatingService.getRowLabel();
		wordMeaningLabel = GuiMaker.createLabel(
				new ComponentOptions().text(Labels.WORD_MEANING)
						.foregroundColor(Color.WHITE));
		wordMeaningText = CommonGuiElementsMaker
				.createShortInput(japaneseWordInformation.getWordMeaning());
		partOfSpeechLabel = GuiMaker.createLabel(
				new ComponentOptions().text(Labels.PART_OF_SPEECH)
						.foregroundColor(Color.WHITE));
		partOfSpeechCombobox = elementsMaker.createComboboxForPartOfSpeech(
				japaneseWordInformation.getPartOfSpeech());
		writingsList = createWritingsList(japaneseWordInformation,
				panelCreatingService, parentDialog);
		writingsLabel = GuiMaker.createLabel(
				new ComponentOptions().text(Labels.WRITING_WAYS_IN_JAPANESE)
						.foregroundColor(Color.WHITE));
	}

	private void addActions(JapaneseWordInformation japaneseWordInformation) {
		actionCreatingService.addWordMeaningTextFieldListeners(wordMeaningText,
				japaneseWordInformation);
		actionCreatingService.addPartOfSpeechListener(partOfSpeechCombobox,
				japaneseWordInformation);

	}

	private MyList<JapaneseWriting> createWritingsList(
			JapaneseWordInformation japaneseWordInformation,
			JapanesePanelRowCreatingService panelCreatingService,
			DialogWindow parentDialog) {
		writingsList = createJapaneseWritingsList(parentDialog,
				applicationController, panelCreatingService);
		japaneseWordInformation.getJapaneseWritings().stream()
				.forEach(writingsList::addWord);
		return writingsList;
	}

	public static MyList<JapaneseWriting> createJapaneseWritingsList(
			DialogWindow parentDialog,
			ApplicationController applicationController,
			JapanesePanelRowCreatingService panelCreatingService) {
		return new MyList<>(parentDialog, applicationController,
				new RowInJapaneseWritingsList(panelCreatingService),
				Labels.WRITING_WAYS_IN_JAPANESE,
				new ListConfiguration().enableWordAdding(false)
						.inheritScrollbar(true).enableWordSearching(false)
						.showButtonsLoadNextPreviousWords(false)
						.skipTitle(true), new ArrayList<>(),
				JapaneseWriting.getInitializer());
	}

	private void addElementsToPanel(MainPanel japaneseWordPanel) {

		japaneseWordPanel
				.addElementsInColumnStartingFromColumn(wordMeaningText, 0,
						rowLabel, wordMeaningLabel, wordMeaningText);
		japaneseWordPanel
				.addElementsInColumnStartingFromColumn(partOfSpeechCombobox, 1,
						partOfSpeechLabel, partOfSpeechCombobox);
		JPanel writingsListPanel = writingsList.getPanel();
		japaneseWordPanel
				.addElementsInColumnStartingFromColumn(writingsListPanel, 1,
						writingsLabel, writingsListPanel);
	}

}

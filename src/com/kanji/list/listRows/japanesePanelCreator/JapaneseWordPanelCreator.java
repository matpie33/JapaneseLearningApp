package com.kanji.list.listRows.japanesePanelCreator;

import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.kanji.constants.strings.Labels;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listElements.JapaneseWriting;
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
	private MyList<JapaneseWriting> writingsList;
	private ApplicationController applicationController;

	public JapaneseWordPanelCreator(
			ApplicationController applicationController) {
		this.applicationController = applicationController;
	}

	public MainPanel createPanel(
			JapaneseWordInformation japaneseWordInformation,
			JapanesePanelRowCreatingService panelCreatingService,
			DialogWindow parentDialog) {
		createElements(japaneseWordInformation, panelCreatingService,
				parentDialog);
		return addElementsToGui();
	}

	private void createElements(JapaneseWordInformation japaneseWordInformation,
			JapanesePanelRowCreatingService panelCreatingService,
			DialogWindow parentDialog) {
		wordMeaningLabel = GuiMaker.createLabel(
				new ComponentOptions().text(Labels.WORD_MEANING)
						.foregroundColor(Color.WHITE));
		wordMeaningText = CommonGuiElementsMaker
				.createShortInput(japaneseWordInformation.getWordMeaning());
		partOfSpeechLabel = GuiMaker.createLabel(
				new ComponentOptions().text(Labels.PART_OF_SPEECH)
						.foregroundColor(Color.WHITE));
		partOfSpeechCombobox = CommonGuiElementsMaker
				.createComboboxForPartOfSpeech();
		writingsList = createWritingsList(japaneseWordInformation,
				panelCreatingService, parentDialog);
		writingsLabel = GuiMaker.createLabel(
				new ComponentOptions().text(Labels.WRITING_WAYS_IN_JAPANESE)
						.foregroundColor(Color.WHITE));
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
						.showButtonsLoadNextPreviousWords(false).skipTitle(true),
				new ArrayList<>(), JapaneseWriting.getInitializer());
	}

	private MainPanel addElementsToGui() {

		MainPanel japaneseWordPanel = new MainPanel(null);
		japaneseWordPanel
				.addElementsInColumnStartingFromColumn(wordMeaningText, 0,
						wordMeaningLabel, wordMeaningText);
		japaneseWordPanel
				.addElementsInColumnStartingFromColumn(partOfSpeechCombobox, 0,
						partOfSpeechLabel, partOfSpeechCombobox);
		JPanel writingsListPanel = writingsList.getPanel();
		japaneseWordPanel
				.addElementsInColumnStartingFromColumn(writingsListPanel, 0,
						writingsLabel, writingsListPanel);

		return japaneseWordPanel;

	}

	public MyList<JapaneseWriting> getJapaneseWritingsList() {
		return writingsList;
	}
}

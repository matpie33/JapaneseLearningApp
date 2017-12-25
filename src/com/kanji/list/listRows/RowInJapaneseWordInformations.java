package com.kanji.list.listRows;

import com.guimaker.colors.BasicColors;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.myList.ListRowMaker;
import com.kanji.constants.strings.Labels;
import com.kanji.utilities.CommonGuiElementsMaker;
import com.kanji.utilities.CommonListElements;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class RowInJapaneseWordInformations implements ListRowMaker<JapaneseWordInformation> {
	private ApplicationWindow applicationWindow;

	public RowInJapaneseWordInformations(ApplicationWindow applicationWindow) {
		this.applicationWindow = applicationWindow;
	}

	@Override
	public MainPanel createListRow(JapaneseWordInformation japaneseWord, CommonListElements commonListElements) {
		MainPanel panel = new MainPanel(null);
		JLabel wordInKanaLabel = GuiMaker.createLabel(new ComponentOptions()
				.text(Labels.WORD_IN_KANA).foregroundColor(Color.WHITE));
		String[] wordInKana = japaneseWord.getWritingsInKana();
		JTextComponent wordInKanaText = CommonGuiElementsMaker.createTextField(
				wordInKana[0]);

		JLabel meaningLabel = GuiMaker.createLabel(
				new ComponentOptions().text(Labels.WORD_MEANING).foregroundColor(BasicColors.OCEAN_BLUE));
		String meaning = japaneseWord.getWordMeaning();
		JTextComponent meaningText = CommonGuiElementsMaker.createTextField(meaning);
		JLabel partOfSpeechLabel = GuiMaker.createLabel(new ComponentOptions()
				.text(Labels.PART_OF_SPEECH).foregroundColor(Color.WHITE));

		JLabel partOfSpeech = GuiMaker.createLabel(new ComponentOptions()
				.text(japaneseWord.getPartOfSpeech().getPolishMeaning()).foregroundColor(Color.WHITE));


		panel.addElementsInColumnStartingFromColumn(0,
				commonListElements.getRowNumberLabel(),	meaningLabel, meaningText);
		panel.addElementsInColumnStartingFromColumn(1, partOfSpeechLabel, partOfSpeech);
		panel.addElementsInColumnStartingFromColumn(1,	wordInKanaLabel, wordInKanaText);

		if (japaneseWord.hasKanjiWriting()){
			String[] wordInKanji = japaneseWord.getWritingsInKanji();
			JLabel wordInKanjiLabel = GuiMaker.createLabel(
					new ComponentOptions().text(Labels.WORD_IN_KANJI).foregroundColor(Color.WHITE));
			JTextComponent wordInKanjiText = CommonGuiElementsMaker
					.createTextField(wordInKanji[0]);

			panel.addElementsInColumnStartingFromColumn(1,	wordInKanjiLabel, wordInKanjiText);
		}

		JButton remove = commonListElements.getButtonDelete();
		panel.addElementsInColumnStartingFromColumn(1, remove);

		//TODO save on focus lost, detect duplicates
//		wordInKanaText.addFocusListener(new ListPropertyChangeHandler<>(
//				applicationWindow.getApplicationController().getKanjiList(), applicationWindow,
//				new KanjiKeywordChecker(), ExceptionsMessages.WORD_ALREADY_DEFINED_EXCEPTION));

		return panel;

	}

}

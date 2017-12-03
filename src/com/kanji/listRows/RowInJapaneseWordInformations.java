package com.kanji.listRows;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.FillType;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.listElements.JapaneseWordInformation;
import com.kanji.listSearching.KanjiIdChecker;
import com.kanji.listSearching.KanjiKeywordChecker;
import com.kanji.myList.ListPropertyChangeHandler;
import com.kanji.myList.ListRowMaker;
import com.kanji.strings.ExceptionsMessages;
import com.kanji.strings.Labels;
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
	public MainPanel createListRow(JapaneseWordInformation kanji, CommonListElements commonListElements) {
		MainPanel panel = new MainPanel(null);
		JLabel wordInKanaLabel = GuiMaker.createLabel(new ComponentOptions()
				.text(Labels.WORD_IN_KANA).foregroundColor(Color.WHITE));
		String wordInKana = kanji.getWordInKana();
		JTextComponent wordInKanaText = CommonGuiElementsMaker.createTextField(wordInKana);

		JLabel meaningLabel = GuiMaker.createLabel(
				new ComponentOptions().text(Labels.WORD_MEANING).foregroundColor(BasicColors.OCEAN_BLUE));
		String meaning = kanji.getWordMeaning();
		JTextComponent meaningText = CommonGuiElementsMaker.createTextField(meaning);

		panel.addElementsInColumnStartingFromColumn(0,
				commonListElements.getRowNumberLabel(),	meaningLabel, meaningText);
		panel.addElementsInColumnStartingFromColumn(1,	wordInKanaLabel, wordInKanaText);

		if (kanji.hasKanjiWriting()){
			String wordInKanji = kanji.getWordInKanji();
			JLabel wordInKanjiLabel = GuiMaker.createLabel(
					new ComponentOptions().text(Labels.WORD_IN_KANJI).foregroundColor(Color.WHITE));
			JTextComponent wordInKanjiText = CommonGuiElementsMaker.createTextField(wordInKanji);

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

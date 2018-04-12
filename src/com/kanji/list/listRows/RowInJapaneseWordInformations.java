package com.kanji.list.listRows;

import com.guimaker.panels.MainPanel;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listRows.japanesePanelCreator.JapaneseWordPanelCreator;
import com.kanji.list.myList.ListRowMaker;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;

public class RowInJapaneseWordInformations
		implements ListRowMaker<JapaneseWordInformation> {
	private JapaneseWordPanelCreator japaneseWordPanelCreator;

	public RowInJapaneseWordInformations(
			JapaneseWordPanelCreator japaneseWordPanelCreator) {
		this.japaneseWordPanelCreator = japaneseWordPanelCreator;
	}

	@Override
	public MainPanel createListRow(JapaneseWordInformation japaneseWord,
			CommonListElements commonListElements) {
		MainPanel panel = new MainPanel(null);
		JLabel rowNumberLabel = commonListElements.getRowNumberLabel();
		japaneseWordPanelCreator.setRowNumberLabel(rowNumberLabel);
		japaneseWordPanelCreator.setLabelsColor(commonListElements.getLabelsColor());
		japaneseWordPanelCreator
				.addJapanesePanelToExistingPanel(panel, japaneseWord);
		japaneseWordPanelCreator.focusMeaningTextfield();
		return panel;
	}
}

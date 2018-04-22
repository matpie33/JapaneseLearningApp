package com.kanji.list.listRows;

import com.guimaker.panels.MainPanel;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listRows.japanesePanelCreatingComponents.JapaneseWordPanelCreator;
import com.kanji.list.myList.ListPropertyInformation;
import com.kanji.list.myList.ListRowData;
import com.kanji.list.myList.ListRowCreator;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;
import java.util.Map;

public class RowInJapaneseWordInformations
		implements ListRowCreator<JapaneseWord> {
	private JapaneseWordPanelCreator japaneseWordPanelCreator;
	private Map<String, ListPropertyInformation> propertiesInformation;

	public RowInJapaneseWordInformations(
			JapaneseWordPanelCreator japaneseWordPanelCreator) {
		this.japaneseWordPanelCreator = japaneseWordPanelCreator;
	}

	@Override
	public MainPanel createListRow(JapaneseWord japaneseWord,
			CommonListElements commonListElements, boolean forSearchPanel) {
		MainPanel panel = new MainPanel(null);
		JLabel rowNumberLabel = commonListElements.getRowNumberLabel();
		japaneseWordPanelCreator.setRowNumberLabel(rowNumberLabel);
		japaneseWordPanelCreator
				.setLabelsColor(commonListElements.getLabelsColor());
		japaneseWordPanelCreator
				.addJapanesePanelToExistingPanel(panel, japaneseWord,
						forSearchPanel, commonListElements);
		japaneseWordPanelCreator.focusMeaningTextfield();

		return panel;
	}

	@Override
	public ListRowData getRowData() {
		return japaneseWordPanelCreator.getRowData();
	}
}

package com.kanji.list.listRows;

import com.guimaker.panels.MainPanel;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listRows.japanesePanelCreator.JapaneseWordPanelCreator;
import com.kanji.list.myList.ListPropertyInformation;
import com.kanji.list.myList.ListRowData;
import com.kanji.list.myList.ListRowMaker;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;
import java.util.Map;

public class RowInJapaneseWordInformations
		implements ListRowMaker<JapaneseWordInformation> {
	private JapaneseWordPanelCreator japaneseWordPanelCreator;
	private Map<String, ListPropertyInformation> propertiesInformation;

	public RowInJapaneseWordInformations(
			JapaneseWordPanelCreator japaneseWordPanelCreator) {
		this.japaneseWordPanelCreator = japaneseWordPanelCreator;
	}

	@Override
	public ListRowData createListRow(JapaneseWordInformation japaneseWord,
			CommonListElements commonListElements, boolean forSearchPanel) {
		MainPanel panel = new MainPanel(null);
		JLabel rowNumberLabel = commonListElements.getRowNumberLabel();
		japaneseWordPanelCreator.setRowNumberLabel(rowNumberLabel);
		japaneseWordPanelCreator.setLabelsColor(commonListElements.getLabelsColor());
		japaneseWordPanelCreator
				.addJapanesePanelToExistingPanel(panel, japaneseWord);
		japaneseWordPanelCreator.focusMeaningTextfield();

		ListRowData rowData = new ListRowData(panel);

		return rowData;
	}
}

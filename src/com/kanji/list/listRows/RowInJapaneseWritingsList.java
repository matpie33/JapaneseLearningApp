package com.kanji.list.listRows;

import com.guimaker.enums.FillType;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.listRows.japanesePanelCreatingService.JapanesePanelCreatingService;
import com.kanji.list.myList.ListRowData;
import com.kanji.list.myList.ListRowCreator;
import com.kanji.list.myList.ListRowDataCreator;
import com.kanji.utilities.CommonListElements;

public class RowInJapaneseWritingsList
		implements ListRowCreator<JapaneseWriting> {

	private JapanesePanelCreatingService japanesePanelCreatingService;
	private JapaneseWord wordContainingWritings;

	public RowInJapaneseWritingsList(
			JapanesePanelCreatingService japanesePanelCreatingService,
			JapaneseWord wordContainingWritings) {
		this.japanesePanelCreatingService = japanesePanelCreatingService;
		this.wordContainingWritings = wordContainingWritings;

	}

	@Override
	public ListRowData createListRow(JapaneseWriting japaneseWriting,
			CommonListElements commonListElements, boolean forSearchPanel) {
		MainPanel rowPanel = new MainPanel(null);
		rowPanel.addRow(SimpleRowBuilder.createRow(FillType.NONE,
				japanesePanelCreatingService
						.addWritingsRow(japaneseWriting, commonListElements,
								wordContainingWritings, forSearchPanel,
								rowPanel)));
		ListRowDataCreator<Kanji> rowDataCreator = new ListRowDataCreator<>(
				rowPanel);
		return rowDataCreator.getListRowData();
	}

}

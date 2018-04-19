package com.kanji.list.listRows;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.FillType;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listRows.japanesePanelCreator.JapanesePanelCreatingService;
import com.kanji.list.myList.ListRowData;
import com.kanji.list.myList.ListRowMaker;
import com.kanji.utilities.CommonListElements;

public class RowInJapaneseWritingsList
		implements ListRowMaker<JapaneseWriting> {

	private MainPanel rowPanel;
	private JapanesePanelCreatingService japanesePanelCreatingService;

	public RowInJapaneseWritingsList(
			JapanesePanelCreatingService japanesePanelCreatingService) {
		this.japanesePanelCreatingService = japanesePanelCreatingService;
	}

	@Override
	public MainPanel createListRow(JapaneseWriting japaneseWriting,
			CommonListElements commonListElements, boolean forSearchPanel) {
		rowPanel = new MainPanel(BasicColors.OCEAN_BLUE);
		rowPanel.addRow(SimpleRowBuilder.createRow(FillType.NONE,
				japanesePanelCreatingService
						.addWritingsRow(japaneseWriting, commonListElements,
								rowPanel, forSearchPanel)));
		return rowPanel;
	}

	@Override
	public ListRowData getRowData() {
		return null;
	}
}

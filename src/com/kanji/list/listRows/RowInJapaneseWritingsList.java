package com.kanji.list.listRows;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.FillType;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listRows.japanesePanelActionsCreator.JapanesePanelActionCreatingService;
import com.kanji.list.listRows.japanesePanelCreator.JapanesePanelRowCreatingService;
import com.kanji.list.myList.ListRowMaker;
import com.kanji.utilities.CommonListElements;

public class RowInJapaneseWritingsList
		implements ListRowMaker<JapaneseWriting> {

	private MainPanel rowPanel;
	private JapanesePanelRowCreatingService japanesePanelRowCreatingService;

	public RowInJapaneseWritingsList(
			JapanesePanelRowCreatingService japanesePanelRowCreatingService) {
		this.japanesePanelRowCreatingService = japanesePanelRowCreatingService;
	}

	@Override
	public MainPanel createListRow(JapaneseWriting data,
			CommonListElements commonListElements) {
		rowPanel = new MainPanel(BasicColors.OCEAN_BLUE);
		rowPanel.addRow(SimpleRowBuilder.createRow(FillType.NONE,
				japanesePanelRowCreatingService
						.addWritingsRow(data, commonListElements,
								rowPanel)));
		return rowPanel;
	}

}

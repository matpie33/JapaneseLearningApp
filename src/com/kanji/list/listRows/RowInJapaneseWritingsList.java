package com.kanji.list.listRows;

import com.guimaker.enums.FillType;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.model.PanelConfiguration;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.InputGoal;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.listRows.japanesePanelCreatingService.JapanesePanelCreatingService;
import com.kanji.list.listeners.InputValidationListener;
import com.kanji.list.myList.ListRowCreator;
import com.kanji.list.myList.ListRowData;
import com.kanji.list.myList.ListRowDataCreator;
import com.kanji.utilities.CommonListElements;

public class RowInJapaneseWritingsList
		implements ListRowCreator<JapaneseWriting> {

	private JapanesePanelCreatingService japanesePanelCreatingService;
	private JapaneseWord wordContainingWritings;
	private PanelDisplayMode displayMode;

	public RowInJapaneseWritingsList(
			JapanesePanelCreatingService japanesePanelCreatingService,
			JapaneseWord wordContainingWritings, PanelDisplayMode displayMode) {
		this.japanesePanelCreatingService = japanesePanelCreatingService;
		this.wordContainingWritings = wordContainingWritings;
		this.displayMode = displayMode;
	}

	@Override
	public ListRowData createListRow(JapaneseWriting japaneseWriting,
			CommonListElements commonListElements, InputGoal inputGoal) {
		MainPanel rowPanel = new MainPanel(null, false, true,
				new PanelConfiguration(displayMode));
		rowPanel.addRow(SimpleRowBuilder.createRow(FillType.NONE,
				japanesePanelCreatingService
						.addWritingsRow(japaneseWriting, commonListElements,
								wordContainingWritings, inputGoal, rowPanel)));
		ListRowDataCreator<Kanji> rowDataCreator = new ListRowDataCreator<>(
				rowPanel);
		return rowDataCreator.getListRowData();
	}

	@Override
	public void addValidationListener(
			InputValidationListener<JapaneseWriting> inputValidationListener) {

	}
}

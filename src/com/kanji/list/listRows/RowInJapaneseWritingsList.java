package com.kanji.list.listRows;

import com.guimaker.enums.FillType;
import com.guimaker.enums.InputGoal;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.list.ListRowData;
import com.guimaker.list.myList.ListRowCreator;
import com.guimaker.list.myList.ListRowDataCreator;
import com.guimaker.model.CommonListElements;
import com.guimaker.model.PanelConfiguration;
import com.guimaker.panels.mainPanel.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listRows.japanesePanelCreatingService.JapanesePanelCreatingService;

import javax.swing.*;

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
	public ListRowData<JapaneseWriting> createListRow(
			JapaneseWriting japaneseWriting,
			CommonListElements<JapaneseWriting> commonListElements,
			InputGoal inputGoal) {
		MainPanel rowPanel = new MainPanel(
				new PanelConfiguration().setPanelDisplayMode(displayMode));
		if (inputGoal.equals(InputGoal.NO_INPUT)){
			rowPanel.setGapsBetweenRowsTo0();
			rowPanel.setPadding(0);
		}
		JComponent[] components = japanesePanelCreatingService.addWritingsRow(
				japaneseWriting, commonListElements, wordContainingWritings,
				inputGoal, rowPanel);
		rowPanel.addRow(
				SimpleRowBuilder.createRow(FillType.HORIZONTAL, components));
		ListRowDataCreator<JapaneseWriting> rowDataCreator = new ListRowDataCreator<>(
				rowPanel);
		return rowDataCreator.getListRowData();
	}

}

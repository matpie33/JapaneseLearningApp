package com.kanji.list.listRows;

import com.guimaker.enums.FillType;
import com.guimaker.enums.InputGoal;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.list.ListRowData;
import com.guimaker.list.myList.ListRowCreator;
import com.guimaker.list.myList.ListRowDataCreator;
import com.guimaker.listeners.InputValidationListener;
import com.guimaker.model.PanelConfiguration;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.model.CommonListElements;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listElements.Kanji;
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
	public ListRowData createListRow(JapaneseWriting japaneseWriting,
			CommonListElements commonListElements, InputGoal inputGoal) {
		MainPanel rowPanel = new MainPanel(
				new PanelConfiguration().setPanelDisplayMode(displayMode));
		JComponent[] components = japanesePanelCreatingService.addWritingsRow(
				japaneseWriting, commonListElements, wordContainingWritings,
				inputGoal, rowPanel);
		rowPanel.addRow(
				SimpleRowBuilder.createRow(FillType.HORIZONTAL, components));
		ListRowDataCreator<Kanji> rowDataCreator = new ListRowDataCreator<>(
				rowPanel);
		return rowDataCreator.getListRowData();
	}

	@Override
	public void addValidationListener(
			InputValidationListener<JapaneseWriting> inputValidationListener) {

	}
}

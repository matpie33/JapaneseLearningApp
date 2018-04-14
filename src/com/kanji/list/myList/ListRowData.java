package com.kanji.list.myList;

import com.guimaker.panels.MainPanel;

import java.util.Map;
import java.util.Optional;

public class ListRowData {

	private MainPanel row;
	private Optional<Map<String, ListPropertyInformation>> rowPropertiesData;

	public ListRowData(MainPanel row) {
		this.row = row;
	}

	public void setRowPropertiesData(
			Map<String, ListPropertyInformation> rowPropertiesData) {
		this.rowPropertiesData = Optional.of(rowPropertiesData);
	}

	public MainPanel getRowPanel() {
		return row;
	}

	public Optional<Map<String, ListPropertyInformation>> getRowPropertiesData() {
		return rowPropertiesData;
	}
}

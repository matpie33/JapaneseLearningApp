package com.kanji.list.myList;

import com.guimaker.panels.MainPanel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ListRowData {

	private MainPanel row;
	private Map<String, ListPropertyInformation> rowPropertiesData;

	public ListRowData(MainPanel row) {
		this.row = row;
	}

	public void setRowPropertiesData(
			Map<String, ListPropertyInformation> rowPropertiesData) {
		this.rowPropertiesData = rowPropertiesData;
	}

	public MainPanel getRowPanel() {
		return row;
	}

	public Map<String, ListPropertyInformation> getRowPropertiesData() {
		return rowPropertiesData;
	}
}

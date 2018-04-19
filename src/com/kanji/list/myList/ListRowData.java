package com.kanji.list.myList;

import com.guimaker.panels.MainPanel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ListRowData {

	private Map<String, ListPropertyInformation> rowPropertiesData;

	public void setRowPropertiesData(
			Map<String, ListPropertyInformation> rowPropertiesData) {
		this.rowPropertiesData = rowPropertiesData;
	}

	public Map<String, ListPropertyInformation> getRowPropertiesData() {
		return rowPropertiesData;
	}
}

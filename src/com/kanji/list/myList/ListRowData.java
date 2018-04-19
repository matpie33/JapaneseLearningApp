package com.kanji.list.myList;

import java.util.Map;

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

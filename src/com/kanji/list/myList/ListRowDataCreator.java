package com.kanji.list.myList;

import com.guimaker.panels.MainPanel;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.guimaker.list.listElements.ListElement;

import javax.swing.text.JTextComponent;

public class ListRowDataCreator<Word extends ListElement> {

	private ListRowData listRowData;

	public ListRowDataCreator(MainPanel rowPanel) {
		this.listRowData = new ListRowData(rowPanel);
	}

	public void addPropertyData(String propertyName,
			JTextComponent filteringInput,
			ListElementPropertyManager<?, Word> filteringHandler) {

		ListPropertyInformation<Word> listPropertyInformation = new ListPropertyInformation<>(
				filteringInput, filteringHandler);
		listRowData
				.addPropertyInformation(propertyName, listPropertyInformation);

	}

	public ListRowData getListRowData() {
		return listRowData;
	}

}

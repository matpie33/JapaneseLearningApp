package com.kanji.list.myList;

import com.guimaker.panels.MainPanel;
import com.guimaker.row.AbstractSimpleRow;
import com.guimaker.row.ComplexRow;
import com.guimaker.row.SimpleRow;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElements.ListElement;
import com.kanji.utilities.Pair;

import javax.swing.text.JTextComponent;

public class ListRowDataCreator<Word extends ListElement> {

	private ListRowData listRowData;


	public ListRowDataCreator(MainPanel rowPanel) {
		this.listRowData =   new ListRowData(rowPanel);
	}

	public void addPropertyData(String propertyName, AbstractSimpleRow rowForProperty,
			Pair<JTextComponent, ListElementPropertyManager<?, Word>>... inputsWithManagers) {
		ListPropertyInformation<Word> listPropertyInformation = new ListPropertyInformation<>(
				rowForProperty);
		for (Pair<JTextComponent, ListElementPropertyManager<?, Word>> pair : inputsWithManagers) {
			listPropertyInformation
					.addInputWithManager(pair.getLeft(), pair.getRight());
		}
		listRowData
				.addPropertyInformation(propertyName, listPropertyInformation);

	}

	public ListRowData getListRowData() {
		return listRowData;
	}

}

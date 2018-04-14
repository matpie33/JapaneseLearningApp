package com.kanji.list.myList;

import com.guimaker.row.AbstractSimpleRow;
import com.guimaker.row.SimpleRow;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElements.ListElement;

import javax.swing.text.JTextComponent;
import java.util.Map;

public class ListPropertyInformation<Word extends ListElement> {

	private AbstractSimpleRow rowForProperty;
	private Map<JTextComponent, ListElementPropertyManager<?, Word>> textFieldsWithPropertyManagers;

	public ListPropertyInformation(AbstractSimpleRow rowForProperty,
			Map<JTextComponent, ListElementPropertyManager<?, Word>> textFieldsWithPropertyManagers) {
		this.rowForProperty = rowForProperty;
		this.textFieldsWithPropertyManagers = textFieldsWithPropertyManagers;
	}

	public AbstractSimpleRow getRowForProperty() {
		return rowForProperty;
	}

	public Map<JTextComponent, ListElementPropertyManager<?, Word>> getTextFieldsWithPropertyManagers() {
		return textFieldsWithPropertyManagers;
	}
}

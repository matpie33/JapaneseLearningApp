package com.kanji.list.myList;

import com.guimaker.row.AbstractSimpleRow;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElements.ListElement;

import javax.swing.text.JTextComponent;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ListPropertyInformation<Word extends ListElement> {

	private AbstractSimpleRow rowForProperty;
	private Map<JTextComponent, ListElementPropertyManager<?, Word>> textFieldsWithPropertyManagers;

	public ListPropertyInformation(AbstractSimpleRow rowForProperty) {
		textFieldsWithPropertyManagers = new LinkedHashMap<>();
		this.rowForProperty = rowForProperty;
	}

	public AbstractSimpleRow getRowForProperty() {
		return rowForProperty;
	}

	public Map<JTextComponent, ListElementPropertyManager<?, Word>> getTextFieldsWithPropertyManagers() {
		return textFieldsWithPropertyManagers;
	}

	public void addInputWithManager(JTextComponent input,
			ListElementPropertyManager<?, Word> propertyManager) {
		textFieldsWithPropertyManagers.put(input, propertyManager);
	}
}

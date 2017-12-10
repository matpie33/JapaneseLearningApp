package com.kanji.listElements;

import com.kanji.enums.ListElementType;
import com.kanji.listSearching.PropertyManager;

public class ListElementData<ListElement> {

	private String elementLabel;
	private PropertyManager<?, ListElement> propertyManager;
	private ListElementType listElementType;
	private String comboboxLabel;

	public ListElementData (String elementLabel, PropertyManager<?, ListElement> propertyManager,
			ListElementType listElementType, String comboboxLabel){
		this.elementLabel = elementLabel;
		this.propertyManager = propertyManager;
		this.listElementType = listElementType;
		this.comboboxLabel = comboboxLabel;
	}

	public String getElementLabel() {
		return elementLabel;
	}

	public PropertyManager getPropertyManager() {
		return propertyManager;
	}

	public ListElementType getListElementType() {
		return listElementType;
	}

	public String getComboboxLabel() {
		return comboboxLabel;
	}
}

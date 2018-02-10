package com.kanji.list.listElements;

import com.kanji.constants.enums.ListElementPropertyType;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;

public class ListElementData<Element extends ListElement> {

	private String elementLabel;
	private ListElementPropertyManager<?, Element> listElementPropertyManager;
	private ListElementPropertyType listElementPropertyType;
	private String comboboxLabel;

	public ListElementData(String elementLabel,
			ListElementPropertyManager<?, Element> listElementPropertyManager,
			ListElementPropertyType listElementPropertyType,
			String comboboxLabel) {
		this.elementLabel = elementLabel;
		this.listElementPropertyManager = listElementPropertyManager;
		this.listElementPropertyType = listElementPropertyType;
		this.comboboxLabel = comboboxLabel;
	}

	public String getElementLabel() {
		return elementLabel;
	}

	public ListElementPropertyManager getListElementPropertyManager() {
		return listElementPropertyManager;
	}

	public ListElementPropertyType getListElementPropertyType() {
		return listElementPropertyType;
	}

	public String getComboboxLabel() {
		return comboboxLabel;
	}
}

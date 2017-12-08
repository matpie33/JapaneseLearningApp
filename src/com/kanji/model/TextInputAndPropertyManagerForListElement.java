package com.kanji.model;

import com.kanji.listSearching.PropertyManager;

import javax.swing.text.JTextComponent;

public class TextInputAndPropertyManagerForListElement {

	private String comboboxLabel;
	private JTextComponent textComponent;
	private PropertyManager propertyManager;

	public TextInputAndPropertyManagerForListElement(String comboboxLabel, JTextComponent textComponent,
			PropertyManager propertyManager){
		this.comboboxLabel = comboboxLabel;
		this.textComponent = textComponent;
		this.propertyManager = propertyManager;
	}

	public String getComboboxLabel() {
		return comboboxLabel;
	}

	public JTextComponent getTextComponent() {
		return textComponent;
	}

	public PropertyManager getPropertyManager() {
		return propertyManager;
	}
}

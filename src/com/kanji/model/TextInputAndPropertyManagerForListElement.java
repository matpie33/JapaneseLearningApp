package com.kanji.model;

import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;

import javax.swing.text.JTextComponent;

public class TextInputAndPropertyManagerForListElement {

	private String comboboxLabel;
	private JTextComponent textComponent;
	private ListElementPropertyManager listElementPropertyManager;

	public TextInputAndPropertyManagerForListElement(String comboboxLabel,
			JTextComponent textComponent,
			ListElementPropertyManager listElementPropertyManager) {
		this.comboboxLabel = comboboxLabel;
		this.textComponent = textComponent;
		this.listElementPropertyManager = listElementPropertyManager;
	}

	public String getComboboxLabel() {
		return comboboxLabel;
	}

	public JTextComponent getTextComponent() {
		return textComponent;
	}

	public ListElementPropertyManager getListElementPropertyManager() {
		return listElementPropertyManager;
	}
}

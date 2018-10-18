package com.kanji.model;

import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.guimaker.list.listElements.ListElement;

public class PropertyPostValidationData<WordProperty, Word extends ListElement> {
	private Word validatedWord;
	private WordProperty recentlyValidatedProperty;
	private ListElementPropertyManager<WordProperty, Word> propertyManager;
	private boolean isValid;

	public PropertyPostValidationData(Word validatedWord,
			WordProperty recentlyValidatedProperty,
			ListElementPropertyManager<WordProperty, Word> propertyManager,
			boolean isValid) {
		this.validatedWord = validatedWord;
		this.recentlyValidatedProperty = recentlyValidatedProperty;
		this.propertyManager = propertyManager;
		this.isValid = isValid;
	}

	public Word getValidatedWord() {
		return validatedWord;
	}

	public WordProperty getRecentlyValidatedProperty() {
		return recentlyValidatedProperty;
	}

	public ListElementPropertyManager<WordProperty, Word> getPropertyManager() {
		return propertyManager;
	}

	public boolean isValid() {
		return isValid;
	}
}

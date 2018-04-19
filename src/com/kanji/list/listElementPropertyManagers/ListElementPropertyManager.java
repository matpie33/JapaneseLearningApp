package com.kanji.list.listElementPropertyManagers;

import com.kanji.list.listElements.ListElement;

import javax.swing.text.JTextComponent;

public interface ListElementPropertyManager<PropertyType, PropertyHolder extends ListElement> {

	public String getInvalidPropertyReason();

	public boolean isPropertyFound(PropertyType property,
			PropertyHolder propertyHolder);

	public PropertyType validateInputAndConvertToProperty(
			JTextComponent textInput);

	public void setProperty(PropertyHolder propertyHolder,
			PropertyType propertyValue);

	public default boolean tryToReplacePropertyWithValueFromTextInput(
			JTextComponent input, PropertyHolder propertyHolder) {
		PropertyType propertyValue = validateInputAndConvertToProperty(input);
		if (propertyValue != null) {
			setProperty(propertyHolder, propertyValue);
		}
		return propertyValue != null;
	}

	public String getPropertyDefinedException(PropertyType property);

}

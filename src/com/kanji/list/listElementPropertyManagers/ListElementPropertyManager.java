package com.kanji.list.listElementPropertyManagers;

import com.kanji.list.listElements.ListElement;

import javax.swing.text.JTextComponent;

public interface ListElementPropertyManager<PropertyType, PropertyHolder extends ListElement> {

	public boolean isPropertyFound(PropertyType property, PropertyHolder propertyHolder);

	public void replaceValueOfProperty(PropertyType newValue, PropertyHolder propertyHolder);

	public PropertyType convertStringToProperty(String valueToConvert);

	public void setPropertyValue (PropertyHolder propertyHolder, PropertyType propertyValue);

	public default boolean tryToReplacePropertyWithValueFromTextInput (String input,
			PropertyHolder propertyHolder){
		PropertyType propertyValue = convertStringToProperty(input);
		if (propertyValue != null){
			setPropertyValue(propertyHolder, propertyValue);
		}
		return propertyValue != null;
	}

}

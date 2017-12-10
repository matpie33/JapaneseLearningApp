package com.kanji.list.listElementPropertyManagers;

import com.kanji.list.listElements.ListElement;

import javax.swing.text.JTextComponent;

public interface ListElementPropertyManager<PropertyType, PropertyHolder extends ListElement> {

	boolean isPropertyFound(PropertyType property, PropertyHolder propertyHolder);

	void replaceValueOfProperty(PropertyType newValue, PropertyHolder propertyHolder);

	PropertyType convertStringToProperty(String valueToConvert);

	boolean tryToReplacePropertyWithValueFromInput (JTextComponent input, PropertyHolder propertyHolder);

}

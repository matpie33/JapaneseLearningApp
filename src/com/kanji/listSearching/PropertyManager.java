package com.kanji.listSearching;

import javax.swing.text.JTextComponent;

public interface PropertyManager<PropertyType, PropertyHolder> {

	boolean isPropertyFound(PropertyType property, PropertyHolder propertyHolder);

	void replaceValueOfProperty(PropertyType newValue, PropertyHolder propertyHolder);

	PropertyType convertStringToProperty(String valueToConvert);

	boolean tryToReplacePropertyWithValueFromInput (JTextComponent input, PropertyHolder propertyHolder);

}

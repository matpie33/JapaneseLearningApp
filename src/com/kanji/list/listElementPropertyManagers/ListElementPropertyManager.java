package com.kanji.list.listElementPropertyManagers;

import com.guimaker.list.listElements.ListElement;

import javax.swing.text.JTextComponent;

public interface ListElementPropertyManager<PropertyType, PropertyHolder extends ListElement> {

	public String getInvalidPropertyReason();

	public boolean isPropertyFound(PropertyType property,
			PropertyHolder propertyHolder);

	public String getPropertyValue(PropertyHolder propertyHolder);

	public PropertyType validateInputAndConvertToProperty(
			JTextComponent textInput);

	public void setProperty(PropertyHolder propertyHolder,
			PropertyType propertyValue);

	public String getPropertyDefinedException(PropertyType property);

}

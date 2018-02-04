package com.kanji.list.listElementPropertyManagers;

import com.kanji.list.listElements.ListElement;

import javax.swing.text.JTextComponent;

public interface ListElementPropertyManager<PropertyType, PropertyHolder extends ListElement> {

	public String getInvalidPropertyReason ();

	public boolean isPropertyFound(PropertyType property, PropertyHolder propertyHolder);

	public PropertyType convertTextInputToProperty(JTextComponent textInput);

	public void setProperty(PropertyHolder propertyHolder, PropertyType propertyValue);

	public default boolean tryToReplacePropertyWithValueFromTextInput (JTextComponent input,
			PropertyHolder propertyHolder){
		PropertyType propertyValue = convertTextInputToProperty(input);
		if (propertyValue != null){
			setProperty(propertyHolder, propertyValue);
		}
		return propertyValue != null;
	}

	public default void replaceProperty (PropertyHolder propertyHolder,
			PropertyType oldValue, PropertyType newValue){
		setProperty(propertyHolder, newValue);
	}

}

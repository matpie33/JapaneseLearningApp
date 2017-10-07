package com.kanji.listSearching;

public interface PropertyManager<PropertyType, PropertyHolder> {

	boolean isPropertyFound(PropertyType property, PropertyHolder propertyHolder);

	void replaceValueOfProperty(PropertyType newValue, PropertyHolder propertyHolder);

	PropertyType convertStringToProperty(String valueToConvert);

}

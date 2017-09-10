package com.kanji.listSearching;

public interface PropertyManager<PropertyType, PropertyHolder> {

	public boolean isPropertyFound(PropertyType property, PropertyHolder propertyHolder);

	public void replaceValueOfProperty(PropertyType newValue, PropertyHolder propertyHolder);

	public PropertyType convertStringToProperty(String valueToConvert);

}

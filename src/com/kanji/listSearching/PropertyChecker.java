package com.kanji.listSearching;

public interface PropertyChecker<PropertyType, PropertyHolder> {

	public boolean isPropertyFound(PropertyType property, PropertyHolder propertyHolder);

}

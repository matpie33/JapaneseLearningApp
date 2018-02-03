package com.kanji.list.myList;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.text.JTextComponent;

import com.kanji.list.listElements.ListElement;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.windows.ApplicationWindow;

public class ListPropertyChangeHandler<Property, PropertyHolder extends ListElement> implements FocusListener {

	private MyList<PropertyHolder> list;
	private ApplicationWindow applicationWindow;
	private Property propertyBeingModified;
	private ListElementPropertyManager<Property, PropertyHolder> listElementPropertyManager;
	private String propertyDefinedExceptionMessage;
	private PropertyHolder propertyHolder;

	public ListPropertyChangeHandler(PropertyHolder propertyHolder,
			MyList<PropertyHolder> list,
			ApplicationWindow applicationWindow,
			ListElementPropertyManager<Property, PropertyHolder> listElementPropertyManager,
			String propertyDefinedExceptionMessage) {
		this.list = list;
		this.applicationWindow = applicationWindow;
		this.listElementPropertyManager = listElementPropertyManager;
		this.propertyDefinedExceptionMessage = propertyDefinedExceptionMessage;
		this.propertyHolder = propertyHolder;
	}

	public void focusGained(FocusEvent e) {
		JTextComponent textElement = (JTextComponent) e.getSource();
		propertyBeingModified = listElementPropertyManager.convertTextInputToProperty(textElement);
	}

	public void focusLost(FocusEvent e) {
		JTextComponent elem = (JTextComponent) e.getSource();
		Property propertyNewValue = listElementPropertyManager.convertTextInputToProperty(elem);

		if (propertyBeingModified.equals(propertyNewValue)) {
			return;
		}
		listElementPropertyManager.setProperty(propertyHolder,
				propertyNewValue);
		//TODO add method revert in property manager - and keep previously changed value
		//from method replace value of property - use it if word is already defined
		// could be default -> it will be same for all
		if (list.isWordDefined(propertyHolder)){
			applicationWindow.showMessageDialog(
					String.format(propertyDefinedExceptionMessage, propertyNewValue));
			elem.setText(propertyBeingModified.toString());
			elem.requestFocusInWindow();
			elem.selectAll();
			return;
		}
		propertyBeingModified = null;
		list.save();
	}

}

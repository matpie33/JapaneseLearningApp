package com.kanji.myList;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.text.JTextComponent;

import com.kanji.listSearching.PropertyManager;
import com.kanji.windows.ApplicationWindow;

public class ListPropertyChangeHandler<Property, PropertyHolder> implements FocusListener {

	private MyList<PropertyHolder> list;
	private ApplicationWindow applicationWindow;
	private Property propertyBeingModified;
	private PropertyManager<Property, PropertyHolder> propertyManager;
	private String propertyDefinedExceptionMessage;

	public ListPropertyChangeHandler(MyList<PropertyHolder> list,
			ApplicationWindow applicationWindow,
			PropertyManager<Property, PropertyHolder> propertyManager,
			String propertyDefinedExceptionMessage) {
		this.list = list;
		this.applicationWindow = applicationWindow;
		this.propertyManager = propertyManager;
		this.propertyDefinedExceptionMessage = propertyDefinedExceptionMessage;
	}

	public void focusGained(FocusEvent e) {
		JTextComponent textElement = (JTextComponent) e.getSource();
		propertyBeingModified = propertyManager.convertStringToProperty(textElement.getText());
	}

	public void focusLost(FocusEvent e) {
		JTextComponent elem = (JTextComponent) e.getSource();

		Property propertyNewValue = propertyManager.convertStringToProperty(elem.getText());

		if (propertyBeingModified.equals(propertyNewValue)) {
			return;
		}
		if (list.isPropertyDefined(propertyManager, propertyNewValue)) {
			applicationWindow.showMessageDialog(
					String.format(propertyDefinedExceptionMessage, propertyNewValue));
			elem.setText(propertyBeingModified.toString());
			elem.requestFocusInWindow();
			elem.selectAll();
			return;
		}
		list.replaceProperty(propertyManager, propertyBeingModified,
				propertyNewValue);
		propertyBeingModified = null;
	}

}

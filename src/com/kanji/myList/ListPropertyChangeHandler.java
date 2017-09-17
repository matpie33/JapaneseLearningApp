package com.kanji.myList;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.text.JTextComponent;

import com.kanji.constants.ExceptionsMessages;
import com.kanji.listSearching.PropertyManager;
import com.kanji.windows.ApplicationWindow;

public class ListPropertyChangeHandler<Property, PropertyHolder> implements FocusListener {

	private MyList<PropertyHolder> list;
	private ApplicationWindow applicationWindow;
	private Property propertyBeingModified;
	private PropertyManager<Property, PropertyHolder> propertyManager;

	public ListPropertyChangeHandler(MyList<PropertyHolder> list,
			ApplicationWindow applicationWindow,
			PropertyManager<Property, PropertyHolder> propertyManager) {
		this.list = list;
		this.applicationWindow = applicationWindow;
		this.propertyManager = propertyManager;
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
			applicationWindow.showMessageDialog(String
					.format(ExceptionsMessages.ID_ALREADY_DEFINED_EXCEPTION, propertyNewValue));
			// TODO ouch its not generic and throws exception for strings.
			elem.setText(propertyBeingModified.toString());
			elem.requestFocusInWindow();
			elem.selectAll();
			return;
		}
		list.replaceProperty(propertyManager, propertyBeingModified, list.getParent(),
				propertyNewValue);
		propertyBeingModified = null;
	}

}

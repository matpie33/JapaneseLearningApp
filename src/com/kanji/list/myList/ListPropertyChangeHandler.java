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

	public ListPropertyChangeHandler(MyList<PropertyHolder> list,
			ApplicationWindow applicationWindow,
			ListElementPropertyManager<Property, PropertyHolder> listElementPropertyManager,
			String propertyDefinedExceptionMessage) {
		this.list = list;
		this.applicationWindow = applicationWindow;
		this.listElementPropertyManager = listElementPropertyManager;
		this.propertyDefinedExceptionMessage = propertyDefinedExceptionMessage;
	}

	public void focusGained(FocusEvent e) {
		JTextComponent textElement = (JTextComponent) e.getSource();
		propertyBeingModified = listElementPropertyManager.convertStringToProperty(textElement.getText());
	}

	public void focusLost(FocusEvent e) {
		JTextComponent elem = (JTextComponent) e.getSource();

		Property propertyNewValue = listElementPropertyManager.convertStringToProperty(elem.getText());

		if (propertyBeingModified.equals(propertyNewValue)) {
			return;
		}
		if (list.isPropertyDefined(listElementPropertyManager, propertyNewValue)) {
			applicationWindow.showMessageDialog(
					String.format(propertyDefinedExceptionMessage, propertyNewValue));
			elem.setText(propertyBeingModified.toString());
			elem.requestFocusInWindow();
			elem.selectAll();
			return;
		}
		list.replaceProperty(listElementPropertyManager, propertyBeingModified,
				propertyNewValue);
		propertyBeingModified = null;
	}

}

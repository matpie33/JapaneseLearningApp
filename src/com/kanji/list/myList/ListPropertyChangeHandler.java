package com.kanji.list.myList;

import java.awt.*;
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
		textElement.setForeground(Color.BLACK);
		propertyBeingModified = listElementPropertyManager.convertTextInputToProperty(textElement);
	}

	public void focusLost(FocusEvent e) {
		JTextComponent elem = (JTextComponent) e.getSource();
		Property propertyNewValue = listElementPropertyManager.convertTextInputToProperty(elem);
		if (propertyNewValue == null){
			String modifiedProperty = propertyBeingModified.toString();
			elem.setForeground(Color.RED);
			applicationWindow.showMessageDialog(
					listElementPropertyManager.getInvalidPropertyReason());
			elem.setText(modifiedProperty);
			elem.selectAll();
			elem.requestFocusInWindow();
			return;
		}

		if (propertyBeingModified.equals(propertyNewValue)) {
			return;
		}
		if (list.doesWordWithPropertyExist(propertyNewValue,
				listElementPropertyManager)){
			elem.requestFocusInWindow();
			elem.setText(propertyBeingModified.toString());
			elem.selectAll();
			applicationWindow.showMessageDialog(
					String.format(propertyDefinedExceptionMessage, propertyNewValue));

			return;
		}
		else{
			listElementPropertyManager.replaceProperty(propertyHolder,
					propertyBeingModified, propertyNewValue);
			propertyBeingModified = null;
			list.save();
		}

	}

}

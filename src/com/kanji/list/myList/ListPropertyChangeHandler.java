package com.kanji.list.myList;

import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElements.ListElement;
import com.kanji.model.WordInMyListExistence;
import com.kanji.windows.ApplicationWindow;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ListPropertyChangeHandler<Property, PropertyHolder extends ListElement>
		implements FocusListener {

	private MyList<PropertyHolder> list;
	private ApplicationWindow applicationWindow;
	private Property propertyBeingModified;
	private ListElementPropertyManager<Property, PropertyHolder> listElementPropertyManager;
	private String propertyDefinedExceptionMessage;
	private PropertyHolder propertyHolder;
	private String defaultValue = "";

	public ListPropertyChangeHandler(PropertyHolder propertyHolder, MyList<PropertyHolder> list,
			ApplicationWindow applicationWindow,
			ListElementPropertyManager<Property, PropertyHolder> listElementPropertyManager,
			String propertyDefinedExceptionMessage) {
		this.list = list;
		this.applicationWindow = applicationWindow;
		this.listElementPropertyManager = listElementPropertyManager;
		this.propertyDefinedExceptionMessage = propertyDefinedExceptionMessage;
		this.propertyHolder = propertyHolder;
	}

	public ListPropertyChangeHandler(PropertyHolder propertyHolder, MyList<PropertyHolder> list,
			ApplicationWindow applicationWindow,
			ListElementPropertyManager<Property, PropertyHolder> listElementPropertyManager,
			String propertyDefinedExceptionMessage, String defaultValue) {
		this(propertyHolder, list, applicationWindow, listElementPropertyManager,
				propertyDefinedExceptionMessage);
		this.defaultValue = defaultValue;
	}

	public void focusGained(FocusEvent e) {
		JTextComponent textElement = (JTextComponent) e.getSource();

		textElement.setForeground(Color.BLACK);
		propertyBeingModified = listElementPropertyManager
				.validateInputAndConvertToProperty(textElement);
	}

	public void focusLost(FocusEvent e) {
		JTextComponent elem = (JTextComponent) e.getSource();
		Property propertyNewValue = listElementPropertyManager
				.validateInputAndConvertToProperty(elem);
		if (propertyNewValue == null && !elem.getText().equals(defaultValue)) {
			String modifiedProperty = propertyBeingModified.toString();
			elem.setForeground(Color.RED);
			applicationWindow
					.showMessageDialog(listElementPropertyManager.getInvalidPropertyReason());
			elem.setText(modifiedProperty);
			elem.selectAll();
			elem.requestFocusInWindow();
			return;
		}

		if (propertyNewValue == null || propertyBeingModified.equals(propertyNewValue)) {
			return;
		}
		WordInMyListExistence<PropertyHolder> wordInMyListExistence = list
				.doesWordWithPropertyExist(propertyNewValue, listElementPropertyManager,
						propertyHolder);
		if (wordInMyListExistence.exists()) {
			elem.requestFocusInWindow();
			elem.setText(propertyBeingModified.toString());
			elem.selectAll();
			applicationWindow.showMessageDialog(
					String.format(propertyDefinedExceptionMessage, propertyNewValue,
							list.get1BasedRowNumberOfWord(wordInMyListExistence.getWord())));

			return;
		}
		else {
			listElementPropertyManager
					.replaceProperty(propertyHolder, propertyBeingModified, propertyNewValue);
			propertyBeingModified = null;
			list.save();
		}

	}

}

package com.kanji.list.myList;

import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElements.ListElement;
import com.kanji.model.WordInMyListExistence;
import com.kanji.windows.DialogWindow;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ListPropertyChangeHandler<Property, PropertyHolder extends ListElement>
		implements FocusListener {

	private MyList<PropertyHolder> list;
	private DialogWindow dialogWindow;
	private Property propertyBeingModified;
	private ListElementPropertyManager<Property, PropertyHolder> listElementPropertyManager;
	private String propertyDefinedExceptionMessage;
	private PropertyHolder propertyHolder;
	private String defaultValue = "";
	private boolean isRequiredField;

	public ListPropertyChangeHandler(PropertyHolder propertyHolder,
			MyList<PropertyHolder> list, DialogWindow dialogWindow,
			ListElementPropertyManager<Property, PropertyHolder> listElementPropertyManager,
			String propertyDefinedExceptionMessage, boolean isRequiredField) {
		this.list = list;
		this.dialogWindow = dialogWindow;
		this.listElementPropertyManager = listElementPropertyManager;
		this.propertyDefinedExceptionMessage = propertyDefinedExceptionMessage;
		this.propertyHolder = propertyHolder;
		this.isRequiredField = isRequiredField;
	}

	public ListPropertyChangeHandler(PropertyHolder propertyHolder,
			MyList<PropertyHolder> list, DialogWindow dialogWindow,
			ListElementPropertyManager<Property, PropertyHolder> listElementPropertyManager,
			String propertyDefinedExceptionMessage, String defaultValue,
			boolean isRequiredField) {
		this(propertyHolder, list, dialogWindow, listElementPropertyManager,
				propertyDefinedExceptionMessage, isRequiredField);
		this.defaultValue = defaultValue;
	}

	@Override
	public void focusGained(FocusEvent e) {
		JTextComponent textElement = (JTextComponent) e.getSource();
		textElement.setForeground(Color.BLACK);
		propertyBeingModified = listElementPropertyManager
				.validateInputAndConvertToProperty(textElement);

	}

	private boolean isTextFieldEmpty(JTextComponent textComponent) {
		return textComponent.getText().isEmpty() || textComponent.getText()
				.equals(defaultValue);
	}

	@Override
	public void focusLost(FocusEvent e) {
		JTextComponent elem = (JTextComponent) e.getSource();
		if (!isRequiredField && isTextFieldEmpty(elem)) {
			return;
		}
		Property propertyNewValue = listElementPropertyManager
				.validateInputAndConvertToProperty(elem);
		if (propertyNewValue == null && !elem.getText().isEmpty()) {
			String modifiedProperty = propertyBeingModified.toString();
			elem.setForeground(Color.RED);
			dialogWindow.showMessageDialog(
					listElementPropertyManager.getInvalidPropertyReason());
			elem.setText(modifiedProperty.replace("[", "").replace("]", ""));
			elem.selectAll();
			elem.requestFocusInWindow();
			return;
		}

		if (propertyNewValue == null || propertyBeingModified
				.equals(propertyNewValue)) {
			return;
		}

		listElementPropertyManager
				.replaceProperty(propertyHolder, propertyBeingModified,
						propertyNewValue);
		WordInMyListExistence<PropertyHolder> wordInMyListExistence = list
				.doesWordWithPropertyExist(propertyNewValue,
						listElementPropertyManager, propertyHolder);
		if (wordInMyListExistence.exists()) {
			listElementPropertyManager
					.replaceProperty(propertyHolder, propertyNewValue,
							propertyBeingModified);
			elem.requestFocusInWindow();
			elem.setText(propertyBeingModified.toString().replace("[", "")
					.replace("]", ""));
			elem.selectAll();
			list.highlightRow(list.get1BasedRowNumberOfWord(
					wordInMyListExistence.getWord()) - 1, true);
			dialogWindow.showMessageDialog(
					String.format(propertyDefinedExceptionMessage,
							propertyNewValue, list.get1BasedRowNumberOfWord(
									wordInMyListExistence.getWord())));

			return;
		}
		else {

			propertyBeingModified = null;
			list.save();
		}

	}

}

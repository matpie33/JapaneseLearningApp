package com.kanji.list.myList;

import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElements.ListElement;
import com.kanji.model.WordInMyListExistence;
import com.kanji.utilities.StringUtilities;
import com.kanji.windows.DialogWindow;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ListPropertyChangeHandler<Property, PropertyHolder extends ListElement>
		implements FocusListener {

	private MyList<PropertyHolder> list;
	private DialogWindow dialogWindow;
	private String previousValueOfTextInput;
	private ListElementPropertyManager<Property, PropertyHolder> listElementPropertyManager;
	private PropertyHolder propertyHolder;
	private String defaultValue = "";
	private boolean isRequiredField;
	private boolean addingWord;

	public ListPropertyChangeHandler(PropertyHolder propertyHolder,
			MyList<PropertyHolder> list, DialogWindow dialogWindow,
			ListElementPropertyManager<Property, PropertyHolder> listElementPropertyManager,
			boolean isRequiredField, boolean addingWord) {
		this.list = list;
		this.dialogWindow = dialogWindow;
		this.listElementPropertyManager = listElementPropertyManager;
		this.propertyHolder = propertyHolder;
		this.isRequiredField = isRequiredField;
		this.addingWord = addingWord;
	}

	public ListPropertyChangeHandler(PropertyHolder propertyHolder,
			MyList<PropertyHolder> list, DialogWindow dialogWindow,
			ListElementPropertyManager<Property, PropertyHolder> listElementPropertyManager,
			String defaultValue, boolean isRequiredField, boolean addingWord) {
		this(propertyHolder, list, dialogWindow, listElementPropertyManager,
				isRequiredField, addingWord);
		this.defaultValue = defaultValue;
	}

	@Override
	public void focusGained(FocusEvent e) {
		JTextComponent textElement = (JTextComponent) e.getSource();
		textElement.setForeground(Color.BLACK);
		previousValueOfTextInput = textElement.getText();

	}

	private boolean isTextFieldEmpty(JTextComponent textComponent) {
		return textComponent.getText().isEmpty() || textComponent.getText()
				.equals(defaultValue);
	}

	@Override
	public void focusLost(FocusEvent e) {
		JTextComponent elem = (JTextComponent) e.getSource();
		Property propertyNewValue = validateAndConvertToProperty(elem);
		if (propertyNewValue != null && addingWord){
			add(elem, propertyNewValue);
		}


		

	}

	private void add(JTextComponent elem, Property propertyNewValue) {
		listElementPropertyManager
				.setProperty(propertyHolder, propertyNewValue);
		WordInMyListExistence<PropertyHolder> wordInMyListExistence = list
				.doesWordWithPropertyExist(propertyNewValue,
						listElementPropertyManager, propertyHolder);
		if (wordInMyListExistence.exists()) {
			elem.setText(previousValueOfTextInput);
			elem.requestFocusInWindow();
			elem.selectAll();
			list.highlightRow(list.get1BasedRowNumberOfWord(
					wordInMyListExistence.getWord()) - 1, true);
			dialogWindow.showMessageDialog(listElementPropertyManager
					.getPropertyDefinedException(propertyNewValue)
					+ StringUtilities.putInNewLine(String.format(
					ExceptionsMessages.ROW_FOR_DUPLICATED_PROPERTY,
					list.get1BasedRowNumberOfWord(
							wordInMyListExistence.getWord()))));

			return;
		}
		else {
			previousValueOfTextInput = null;
			list.save();
		}
	}

	private Property validateAndConvertToProperty(JTextComponent elem) {
		if (!isRequiredField && isTextFieldEmpty(elem) || elem.getText()
				.equals(previousValueOfTextInput)) {
			return null;
		}
		Property propertyNewValue = listElementPropertyManager
				.validateInputAndConvertToProperty(elem);
		if (propertyNewValue == null && !elem.getText().isEmpty()) {
			elem.setForeground(Color.RED);
			dialogWindow.showMessageDialog(
					listElementPropertyManager.getInvalidPropertyReason());
			elem.setText(previousValueOfTextInput);
			elem.selectAll();
			elem.requestFocusInWindow();
			return null;
		}
		return propertyNewValue;

	}

}

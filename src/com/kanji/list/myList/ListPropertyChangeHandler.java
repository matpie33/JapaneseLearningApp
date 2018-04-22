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
		JTextComponent textInput = (JTextComponent) e.getSource();
		textInput.setForeground(Color.WHITE);
		previousValueOfTextInput = textInput.getText();

	}

	private boolean isTextFieldEmpty(JTextComponent textComponent) {
		return textComponent.getText().isEmpty() || textComponent.getText()
				.equals(defaultValue);
	}

	@Override
	public void focusLost(FocusEvent e) {
		JTextComponent input = (JTextComponent) e.getSource();
		Property propertyNewValue = validateAndConvertToProperty(input);
		if (propertyNewValue != null && addingWord) {
			add(input, propertyNewValue);
		}

	}

	private void add(JTextComponent input, Property propertyNewValue) {
		listElementPropertyManager
				.setProperty(propertyHolder, propertyNewValue);
		WordInMyListExistence<PropertyHolder> wordInMyListExistence = list
				.doesWordWithPropertyExist(propertyNewValue,
						listElementPropertyManager, propertyHolder);
		if (wordInMyListExistence.exists()) {
			String exceptionMessage = getExceptionForDuplicate(propertyNewValue,
					wordInMyListExistence.getWord());
			setTextInputToPreviousValue(input);
			setWordToPreviousValue(input);
			list.highlightRow(list.get1BasedRowNumberOfWord(
					wordInMyListExistence.getWord()) - 1, true);
			dialogWindow.showMessageDialog(exceptionMessage);
			return;
		}
		else {
			previousValueOfTextInput = null;
			list.save();
		}
	}

	private void setWordToPreviousValue(JTextComponent input) {
		listElementPropertyManager.setProperty(propertyHolder,
				listElementPropertyManager
						.validateInputAndConvertToProperty(input));
	}

	private void setTextInputToPreviousValue(JTextComponent input) {
		input.setText(previousValueOfTextInput);
		input.requestFocusInWindow();
		input.selectAll();
	}

	private String getExceptionForDuplicate(Property propertyNewValue,
			PropertyHolder duplicatedWord) {
		String propertyDefinedMessage = listElementPropertyManager
				.getPropertyDefinedException(propertyNewValue);
		int duplicateRowNumber = list.get1BasedRowNumberOfWord(duplicatedWord);
		String duplicatedRowMessage = StringUtilities.putInNewLine(
				String.format(ExceptionsMessages.ROW_FOR_DUPLICATED_PROPERTY,
						duplicateRowNumber));
		return propertyDefinedMessage + duplicatedRowMessage;
	}

	private Property validateAndConvertToProperty(JTextComponent input) {
		if (!isRequiredField && isTextFieldEmpty(input) || input.getText()
				.equals(previousValueOfTextInput)) {
			return null;
		}
		Property propertyNewValue = listElementPropertyManager
				.validateInputAndConvertToProperty(input);
		if (propertyNewValue == null && !input.getText().isEmpty()) {
			setTextInputToPreviousValue(input);
			input.setForeground(Color.RED);
			dialogWindow.showMessageDialog(
					listElementPropertyManager.getInvalidPropertyReason());
			return null;
		}
		return propertyNewValue;

	}


}

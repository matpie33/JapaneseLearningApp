package com.kanji.list.myList;

import com.kanji.constants.enums.InputGoal;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.listeners.InputValidationListener;
import com.kanji.model.PropertyPostValidationData;
import com.kanji.model.WordInMyListExistence;
import com.kanji.utilities.StringUtilities;
import com.kanji.utilities.ThreadUtilities;
import com.kanji.windows.DialogWindow;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashSet;
import java.util.Set;

public class ListPropertyChangeHandler<Property, PropertyHolder extends ListElement>
		implements FocusListener {

	private MyList<PropertyHolder> list;
	private DialogWindow dialogWindow;
	private String previousValueOfTextInput;
	private ListElementPropertyManager<Property, PropertyHolder> listElementPropertyManager;
	private PropertyHolder propertyHolder;
	private String defaultValue = "";
	private boolean isRequiredField;
	private InputGoal inputGoal;
	private Set<InputValidationListener<PropertyHolder>> validationListeners = new HashSet<>();

	public ListPropertyChangeHandler(PropertyHolder propertyHolder,
			MyList<PropertyHolder> list, DialogWindow dialogWindow,
			ListElementPropertyManager<Property, PropertyHolder> listElementPropertyManager,
			boolean isRequiredField, InputGoal inputGoal) {
		this.list = list;
		this.dialogWindow = dialogWindow;
		this.listElementPropertyManager = listElementPropertyManager;
		this.propertyHolder = propertyHolder;
		this.isRequiredField = isRequiredField;
		this.inputGoal = inputGoal;
	}

	public ListPropertyChangeHandler(PropertyHolder propertyHolder,
			MyList<PropertyHolder> list, DialogWindow dialogWindow,
			ListElementPropertyManager<Property, PropertyHolder> listElementPropertyManager,
			String defaultValue, boolean isRequiredField, InputGoal inputGoal) {
		this(propertyHolder, list, dialogWindow, listElementPropertyManager,
				isRequiredField, inputGoal);
		this.defaultValue = defaultValue;
	}

	public void addValidationListener(
			InputValidationListener<PropertyHolder> validationListener) {
		validationListeners.add(validationListener);
	}

	@Override
	public void focusGained(FocusEvent e) {
		JTextComponent textInput = (JTextComponent) e.getSource();
		textInput.setForeground(Color.WHITE);
		previousValueOfTextInput = textInput.getText().isEmpty() ?
				defaultValue :
				textInput.getText();

	}

	private boolean isTextFieldEmpty(JTextComponent textComponent) {
		return textComponent.getText().isEmpty() || textComponent.getText()
				.equals(defaultValue);
	}

	@Override
	public void focusLost(FocusEvent e) {
		JTextComponent input = (JTextComponent) e.getSource();
		if (isTextFieldEmpty(input) || (
				input.getText().equals(previousValueOfTextInput) && !inputGoal
						.equals(InputGoal.SEARCH))) {
			return;
		}
		Property propertyNewValue = validateAndConvertToProperty(input);

		ThreadUtilities.callOnOtherThread(() -> {
			boolean inputValid = propertyNewValue != null;
			boolean addedWord = false;
			if (inputValid && !inputGoal.equals(InputGoal.SEARCH)) {
				addedWord = addWordToList(input, propertyNewValue);
			}
			notifyValidationListeners(inputValid && (addedWord || inputGoal
					.equals(InputGoal.SEARCH)), propertyNewValue);
		});
	}

	private void notifyValidationListeners(boolean inputValid,
			Property propertyNewValue) {
		PropertyPostValidationData<Property, PropertyHolder> postValidationData = new PropertyPostValidationData<>(
				propertyHolder, propertyNewValue, listElementPropertyManager,
				inputValid);
		validationListeners.forEach(
				listener -> listener.inputValidated(postValidationData));
	}

	private boolean addWordToList(JTextComponent input,
			Property propertyNewValue) {
		listElementPropertyManager
				.setProperty(propertyHolder, propertyNewValue);
		WordInMyListExistence<PropertyHolder> wordInMyListExistence = list
				.doesWordWithPropertyExist(propertyNewValue,
						listElementPropertyManager, propertyHolder);
		if (wordInMyListExistence.exists()) {
			setTextInputToPreviousValue(input);
			setWordToPreviousValue(input);
			int duplicateRowNumber = wordInMyListExistence
					.getOneBasedRowNumber();
			String exceptionMessage = getExceptionForDuplicate(propertyNewValue,
					duplicateRowNumber);
			dialogWindow.showMessageDialog(exceptionMessage, false);
			list.highlightRow(duplicateRowNumber - 1, true);

			//TODO performance of displaying just 200 words is terrible low, which
			//forces me to show message dialog before highlighting row (which loads words
			// if necessary)
			//TODO also think of a way how to display modal dialog while painting the
			//loaded words

			return false;
		}
		else {
			previousValueOfTextInput = null;
			list.save();
			return true;
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
			int duplicateRowNumber) {
		String propertyDefinedMessage = listElementPropertyManager
				.getPropertyDefinedException(propertyNewValue);
		String duplicatedRowMessage = StringUtilities.putInNewLine(
				String.format(ExceptionsMessages.ROW_FOR_DUPLICATED_PROPERTY,
						duplicateRowNumber));
		return propertyDefinedMessage + duplicatedRowMessage;
	}

	private Property validateAndConvertToProperty(JTextComponent input) {
		if ((!isRequiredField && isTextFieldEmpty(input))) {
			return null;
		}
		Property propertyNewValue = listElementPropertyManager
				.validateInputAndConvertToProperty(input);
		if (propertyNewValue == null && !input.getText().isEmpty()) {
			input.setForeground(Color.RED);
			dialogWindow.showMessageDialog(
					listElementPropertyManager.getInvalidPropertyReason());
			setTextInputToPreviousValue(input);
			return null;
		}
		return propertyNewValue;

	}

}

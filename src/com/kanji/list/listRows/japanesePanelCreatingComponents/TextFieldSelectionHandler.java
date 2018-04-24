package com.kanji.list.listRows.japanesePanelCreatingComponents;

import com.guimaker.colors.BasicColors;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class TextFieldSelectionHandler {

	private JTextComponent currentlySelectedTextfield = emptyTextField;
	public static final Color NOT_SELECTED_COLOR = Color.GRAY;
	public static final Color SELECTED_COLOR = BasicColors.DARK_BLUE;
	private static final JTextComponent emptyTextField = new JTextField();

	public void toggleSelection(JTextComponent clickedTextField) {
		if (clickedTextField.equals(currentlySelectedTextfield)) {
			currentlySelectedTextfield = emptyTextField;
			unselectTextInput(clickedTextField);
		}
		else {
			unselectTextInput(currentlySelectedTextfield);	
			currentlySelectedTextfield = clickedTextField;
			currentlySelectedTextfield.setBackground(SELECTED_COLOR);
			currentlySelectedTextfield.setOpaque(true);
		}
	}

	private void unselectTextInput (JTextComponent textInput){
		textInput.setBackground(NOT_SELECTED_COLOR);
		textInput.setOpaque(false);
	}

	public String getCurrentlySelectedWord() {
		return currentlySelectedTextfield.getText();
	}
}

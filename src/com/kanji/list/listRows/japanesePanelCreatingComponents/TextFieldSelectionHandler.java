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
			clickedTextField.setBackground(NOT_SELECTED_COLOR);
		}
		else {
			currentlySelectedTextfield.setBackground(NOT_SELECTED_COLOR);
			currentlySelectedTextfield = clickedTextField;
			currentlySelectedTextfield.setBackground(SELECTED_COLOR);
		}
	}

	public String getCurrentlySelectedWord() {
		return currentlySelectedTextfield.getText();
	}
}

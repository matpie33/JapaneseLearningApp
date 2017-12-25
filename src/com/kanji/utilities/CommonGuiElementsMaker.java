package com.kanji.utilities;

import java.awt.Color;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import com.guimaker.options.ComponentOptions;
import com.guimaker.options.TextAreaOptions;
import com.guimaker.options.TextComponentOptions;
import com.guimaker.panels.GuiMaker;
import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.enums.SplitPaneOrientation;

public class CommonGuiElementsMaker {

	public static JTextComponent createKanjiWordInput(String defaultContent) {
		return GuiMaker.createTextArea(
				new TextAreaOptions().text(defaultContent).rowsAndColumns(3, 15)
						.moveToNextComponentWhenTabbed(true));
	}

	public static JTextComponent createTextField(String defaultContent) {
		return GuiMaker.createTextField(
				new TextComponentOptions().text(defaultContent).rowsAndColumns(1, 15));
	}

	public static JTextComponent createKanjiIdInput() {
		return GuiMaker
				.createTextField(new TextComponentOptions().maximumCharacters(5).digitsOnly(true).rowsAndColumns(1,5));
	}

	public static JLabel createErrorLabel(String message) {
		return GuiMaker
				.createLabel(new ComponentOptions().text(message).foregroundColor(Color.RED));
	}

	public static JSplitPane createSplitPane(SplitPaneOrientation splitPaneOrientation) {
		JSplitPane splitPane = new JSplitPane(splitPaneOrientation.getValue());
		splitPane.setContinuousLayout(true);
		return splitPane;
	}

	public static JComboBox<String> createComboboxForPartOfSpeech() {
		JComboBox<String> comboBox = new JComboBox<>();
		for (PartOfSpeech partOfSpeech: PartOfSpeech.values()){
			comboBox.addItem(partOfSpeech.getPolishMeaning());
		}
		return comboBox;
	}

}

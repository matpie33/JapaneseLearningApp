package com.kanji.utilities;

import com.guimaker.options.ComponentOptions;
import com.guimaker.options.TextAreaOptions;
import com.guimaker.options.TextComponentOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.kanji.constants.enums.SplitPaneOrientation;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class CommonGuiElementsCreator {

	public static JTextComponent createKanjiWordInput(String defaultContent) {
		return GuiElementsCreator.createTextArea(
				new TextAreaOptions().text(defaultContent).rowsAndColumns(2, 5)
						.moveToNextComponentWhenTabbed(true));
	}

	public static JTextComponent createShortInput(String defaultContent) {
		return GuiElementsCreator.createTextField(
				new TextComponentOptions().text(defaultContent).rowsAndColumns(1, 35));
	}

	public static JTextComponent createTextField(String defaultContent) {
		return GuiElementsCreator.createTextField(
				new TextComponentOptions().text(defaultContent)
						.rowsAndColumns(1, 15));
	}

	public static JTextComponent createKanjiIdInput() {
		return GuiElementsCreator.createTextField(
				new TextComponentOptions().maximumCharacters(5).digitsOnly(true)
						.rowsAndColumns(1, 5));
	}

	public static JLabel createErrorLabel(String message) {
		return GuiElementsCreator.createLabel(
				new ComponentOptions().text(message)
						.foregroundColor(Color.RED));
	}

	public static JSplitPane createSplitPane(
			SplitPaneOrientation splitPaneOrientation,
			JComponent leftOrUpperComponent, JComponent rightOrDownComponent,
			double splittingWeight) {
		JSplitPane splitPane = new JSplitPane(splitPaneOrientation.getValue());
		splitPane.setContinuousLayout(true);
		splitPane.setLeftComponent(leftOrUpperComponent);
		splitPane.setRightComponent(rightOrDownComponent);
		splitPane.setResizeWeight(splittingWeight);
		splitPane.setBorder(null);
		return splitPane;
	}

}

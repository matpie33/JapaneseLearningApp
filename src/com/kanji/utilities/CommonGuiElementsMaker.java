package com.kanji.utilities;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JTextArea;

import com.guimaker.options.ComponentOptions;
import com.guimaker.options.TextComponentOptions;
import com.guimaker.panels.GuiMaker;

public class CommonGuiElementsMaker {

	public static JTextArea createKanjiWordInput(String defaultContent) {
		return GuiMaker.createTextArea(
				new TextComponentOptions().text(defaultContent).rowsAndColumns(3, 15));
	}

	public static JTextArea createKanjiIdInput() {
		return GuiMaker.createTextArea(new TextComponentOptions().maximumCharacters(5));
	}

	public static JLabel createErrorLabel(String message) {
		return GuiMaker
				.createLabel(new ComponentOptions().text(message).foregroundColor(Color.RED));
	}

}

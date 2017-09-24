package com.kanji.utilities;

import javax.swing.JTextArea;

import com.guimaker.panels.GuiMaker;

public class CommonGuiElementsMaker {

	public static JTextArea createKanjiWordInput(String defaultContent) {
		return GuiMaker.createTextArea(defaultContent);
	}

	public static JTextArea createKanjiIdInput() {
		return GuiMaker.createTextArea(5);
	}

}

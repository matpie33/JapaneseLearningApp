package com.kanji.list.listRows.japanesePanelCreator;

import com.guimaker.enums.ComponentType;
import com.guimaker.options.TextComponentOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.Prompts;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;

public class JapanesePanelElementsMaker {

	public static JTextComponent createEmptyKanaTextField() {
		return createKanaOrKanjiTextField(Prompts.KANA_TEXT);
	}

	public static JTextComponent createEmptyKanjiTextField() {
		return createKanaOrKanjiTextField(Prompts.KANJI_TEXT);
	}

	public static JTextComponent createKanaOrKanjiTextField(String prompt) {
		return GuiMaker.createTextField(
				new TextComponentOptions().text(prompt).editable(true)
						.focusable(true).fontSize(30f));
	}

	private static AbstractButton createButton(String buttonLabel,
			AbstractAction actionOnClick) {
		return GuiMaker
				.createButtonlikeComponent(ComponentType.BUTTON, buttonLabel,
						null);

	}

	public static AbstractButton createButtonAddKanjiWriting(MainPanel rowPanel) {
		return createButton(ButtonsNames.ADD_KANJI_WRITING,
				new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						addKanjiTextFieldToRow(rowPanel);
					}
				});
	}

	public static void addKanjiTextFieldToRow(MainPanel rowPanel) {
		//TODO implement this method in guimaker
		rowPanel.insertElementInRowAndColumn(1, createEmptyKanaTextField());
	}

}

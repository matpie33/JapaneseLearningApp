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

	public static JTextComponent createKanaTextField(String text) {
		return createKanaOrKanjiTextField(text, Prompts.KANA_TEXT);
	}

	public static JTextComponent createKanjiTextField(String text) {
		return createKanaOrKanjiTextField(text, Prompts.KANJI_TEXT);
	}

	private static JTextComponent createKanaOrKanjiTextField(
			String initialValue, String prompt) {
		return GuiMaker.createTextField(
				new TextComponentOptions().text(initialValue).editable(true)
						.focusable(true).fontSize(30f).promptWhenEmpty(prompt));
	}

	private static AbstractButton createButton(String buttonLabel,
			AbstractAction actionOnClick) {
		return GuiMaker
				.createButtonlikeComponent(ComponentType.BUTTON, buttonLabel,
						actionOnClick);

	}

	public static AbstractButton createButtonAddKanjiWriting(
			MainPanel rowPanel) {
		AbstractButton button = createButton(ButtonsNames.ADD_KANJI_WRITING,
				null);
		button.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rowPanel.insertElementInPlaceOfElement(
						createKanjiTextField(""), button);
			}
		});
		return button;
	}

}

package com.kanji.kanjiListRow;

import com.guimaker.enums.ButtonType;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.options.ButtonOptions;
import com.guimaker.options.ComponentOptions;
import com.guimaker.options.TextAreaOptions;
import com.guimaker.options.TextComponentOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.kanji.constants.strings.JapaneseApplicationButtonsNames;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class KanjiElementsCreator {

	private Color labelsColor;

	public void setLabelsColor(Color labelsColor) {
		this.labelsColor = labelsColor;
	}

	public JTextComponent createKanjiKeywordInput(String text,
			boolean enabled) {
		return GuiElementsCreator.createTextArea(
				new TextAreaOptions().text(text).rowsAndColumns(2, 5)
						.moveToNextComponentWhenTabbed(true)
						.setEnabled(enabled));
	}

	private boolean isEditable(PanelDisplayMode displayMode) {
		return displayMode.equals(PanelDisplayMode.EDIT);
	}

	public JTextComponent createKanjiIdInput(int id, boolean enabled) {
		String inputText;
		if (id > 0) {
			inputText = Integer.toString(id);
		}
		else {
			inputText = "";
		}
		return GuiElementsCreator.createTextField(
				new TextComponentOptions().text(inputText).maximumCharacters(5)
						.digitsOnly(true).setEnabled(enabled)
						.rowsAndColumns(1, 5));
	}

	public JLabel createLabel(String text) {
		return GuiElementsCreator.createLabel(
				new ComponentOptions().text(text).foregroundColor(labelsColor));
	}

	public AbstractButton createButtonShowKanjiStories() {
		return GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON)
						.text(JapaneseApplicationButtonsNames.SHOW_KANJI_STORIES), null);
	}
}

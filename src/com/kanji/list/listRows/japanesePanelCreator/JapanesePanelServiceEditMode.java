package com.kanji.list.listRows.japanesePanelCreator;

import com.guimaker.panels.MainPanel;
import com.kanji.list.listElementPropertyManagers.japaneseWordWritings.JapaneseWordWritingsInputManager;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.util.ArrayList;
import java.util.List;

public class JapanesePanelServiceEditMode
		implements JapanesePanelCreatingService {

	private JapanesePanelElementsMaker elementsMaker;
	private JapaneseWord wordContainingWriting;

	public JapanesePanelServiceEditMode(
			JapanesePanelElementsMaker elementsMaker) {
		this.elementsMaker = elementsMaker;
	}

	@Override
	public void setWord(JapaneseWord wordInformation) {
		wordContainingWriting = wordInformation;
	}

	@Override
	public JComponent[] addWritingsRow(JapaneseWriting japaneseWriting,
			CommonListElements commonListElements, MainPanel rowPanel) {
		List<JComponent> rowElements = new ArrayList<>();
		JapaneseWordWritingsInputManager japaneseWritingsTextFields = elementsMaker
				.createJapaneseWritingsTextFields(japaneseWriting,
						wordContainingWriting, true);
		rowElements.add(japaneseWritingsTextFields.getKanaInput());
		//TODO try to use the approach in whole application:
		//GuiElement e = actionMaker.withAction(elementsMaker.createElement)
		for (JTextComponent kanjiInput : japaneseWritingsTextFields
				.getKanjiInputs()) {
			rowElements.add(kanjiInput);
		}
		rowElements.add(elementsMaker
				.createButtonAddKanjiWriting(rowPanel, japaneseWriting,
						wordContainingWriting));
		rowElements.add(commonListElements.getButtonAddRow());
		rowElements.add(commonListElements.getButtonDelete());

		return rowElements.toArray(new JComponent[] {});
	}

}

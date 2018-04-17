package com.kanji.list.listRows.japanesePanelCreator;

import com.guimaker.panels.MainPanel;
import com.kanji.list.listElementPropertyManagers.japaneseWordWritings.JapaneseWordWritingsInputManager;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listRows.japanesePanelActionsCreator.JapanesePanelActions;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.util.ArrayList;
import java.util.List;

public class JapanesePanelServiceViewMode
		implements JapanesePanelCreatingService {

	private JapanesePanelElementsMaker elementsMaker;
	private JapaneseWord wordContainingWriting;
	private TextFieldSelectionHandler textFieldSelectionHandler;
	private JapanesePanelActions actionsCreator;

	public JapanesePanelServiceViewMode(
			JapanesePanelElementsMaker elementsMaker,
			TextFieldSelectionHandler textFieldSelectionHandler,
			JapanesePanelActions actionsCreator) {
		this.elementsMaker = elementsMaker;
		this.textFieldSelectionHandler = textFieldSelectionHandler;
		this.actionsCreator = actionsCreator;
	}

	@Override
	public void setWord(JapaneseWord wordContainingWriting) {
		this.wordContainingWriting = wordContainingWriting;
	}

	@Override
	public JComponent[] addWritingsRow(JapaneseWriting japaneseWriting,
			CommonListElements commonListElements, MainPanel rowPanel) {
		List<JComponent> rowElements = new ArrayList<>();
		JapaneseWordWritingsInputManager japaneseWritingsTextFields = elementsMaker
				.createJapaneseWritingsTextFields(japaneseWriting,
						wordContainingWriting, true);
		rowElements.add(actionsCreator
				.selectableTextfield(japaneseWritingsTextFields.getKanaInput(),
						textFieldSelectionHandler));
		for (JTextComponent kanjiInput : japaneseWritingsTextFields.getKanjiInputs()) {
			rowElements.add(actionsCreator.selectableTextfield(kanjiInput,
					textFieldSelectionHandler));
		}

		return rowElements.toArray(new JComponent[] {});
	}

	public TextFieldSelectionHandler getSelectionHandler() {
		return textFieldSelectionHandler;
	}
}

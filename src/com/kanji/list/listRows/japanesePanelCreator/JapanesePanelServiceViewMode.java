package com.kanji.list.listRows.japanesePanelCreator;

import com.guimaker.panels.MainPanel;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listRows.japanesePanelActionsCreator.JapanesePanelActions;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class JapanesePanelServiceViewMode
		implements JapanesePanelCreatingService {

	private JapanesePanelElementsMaker elementsMaker;
	private JapaneseWordInformation wordContainingWriting;
	private TextFieldSelectionHandler textFieldSelectionHandler;

	public JapanesePanelServiceViewMode(
			JapanesePanelElementsMaker elementsMaker,
			TextFieldSelectionHandler textFieldSelectionHandler) {
		this.elementsMaker = elementsMaker;
		this.textFieldSelectionHandler = textFieldSelectionHandler;
	}

	@Override
	public void setWord(JapaneseWordInformation wordContainingWriting) {
		this.wordContainingWriting = wordContainingWriting;
	}

	@Override
	public JComponent[] addWritingsRow(JapaneseWriting japaneseWriting,
			CommonListElements commonListElements, MainPanel rowPanel) {
		List<JComponent> rowElements = new ArrayList<>();
		rowElements.add(JapanesePanelActions.selectableTextfield(elementsMaker
						.createKanaTextField(japaneseWriting.getKanaWriting(),
								japaneseWriting, wordContainingWriting, false),
				textFieldSelectionHandler));
		for (String kanjiWriting : japaneseWriting.getKanjiWritings()) {
			rowElements.add(JapanesePanelActions.selectableTextfield(
					elementsMaker
							.createKanjiTextField(kanjiWriting, japaneseWriting,
									wordContainingWriting, false),
					textFieldSelectionHandler));
		}

		return rowElements.toArray(new JComponent[] {});
	}

	public TextFieldSelectionHandler getSelectionHandler() {
		return textFieldSelectionHandler;
	}
}

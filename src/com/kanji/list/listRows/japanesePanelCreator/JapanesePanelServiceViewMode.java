package com.kanji.list.listRows.japanesePanelCreator;

import com.guimaker.panels.MainPanel;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listRows.japanesePanelActionsCreator.JapanesePanelActions;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class JapanesePanelServiceViewMode
		implements JapanesePanelCreatingService {

	private JapanesePanelElementsMaker elementsMaker;
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
	public JComponent[] addWritingsRow(JapaneseWriting japaneseWriting,
			CommonListElements commonListElements, JapaneseWord japaneseWord,
			boolean forSearchPanel, MainPanel rowPanel) {
		List<JComponent> rowElements = new ArrayList<>();
		rowElements.add(actionsCreator.selectableTextfield(elementsMaker
						.createKanaInputWithValidation(japaneseWriting,
								japaneseWord, false, forSearchPanel),
				textFieldSelectionHandler));
		for (String kanjiWriting : japaneseWriting.getKanjiWritings()) {
			rowElements.add(actionsCreator.selectableTextfield(elementsMaker
					.createKanjiInputWithValidation(kanjiWriting,
							japaneseWriting, japaneseWord,
							forSearchPanel), textFieldSelectionHandler));
		}

		return rowElements.toArray(new JComponent[] {});
	}

	public TextFieldSelectionHandler getSelectionHandler() {
		return textFieldSelectionHandler;
	}
}

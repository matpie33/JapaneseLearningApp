package com.kanji.list.listRows.japanesePanelCreatingService;

import com.guimaker.panels.MainPanel;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listRows.japanesePanelCreatingComponents.JapanesePanelActionsCreator;
import com.kanji.list.listRows.japanesePanelCreatingComponents.JapanesePanelElementsCreator;
import com.kanji.list.listRows.japanesePanelCreatingComponents.TextFieldSelectionHandler;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class JapanesePanelInViewModeCreator
		implements JapanesePanelCreatingService {

	private JapanesePanelElementsCreator elementsMaker;
	private TextFieldSelectionHandler textFieldSelectionHandler;
	private JapanesePanelActionsCreator actionsCreator;

	public JapanesePanelInViewModeCreator(
			JapanesePanelElementsCreator elementsMaker,
			TextFieldSelectionHandler textFieldSelectionHandler,
			JapanesePanelActionsCreator actionsCreator) {
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
								japaneseWord, false, forSearchPanel, true),
				textFieldSelectionHandler));
		for (String kanjiWriting : japaneseWriting.getKanjiWritings()) {
			rowElements.add(actionsCreator.selectableTextfield(elementsMaker
					.createKanjiInputWithValidation(kanjiWriting,
							japaneseWriting, japaneseWord,
							forSearchPanel, false, true), textFieldSelectionHandler));
		}

		return rowElements.toArray(new JComponent[] {});
	}

	public TextFieldSelectionHandler getSelectionHandler() {
		return textFieldSelectionHandler;
	}
}

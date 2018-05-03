package com.kanji.list.listRows.japanesePanelCreatingService;

import com.guimaker.panels.MainPanel;
import com.kanji.constants.enums.InputGoal;
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

	public JapanesePanelInViewModeCreator(
			JapanesePanelElementsCreator elementsMaker,
			JapanesePanelActionsCreator actionsCreator) {
		this.elementsMaker = elementsMaker;
	}

	@Override
	public JComponent[] addWritingsRow(JapaneseWriting japaneseWriting,
			CommonListElements commonListElements, JapaneseWord japaneseWord,
			InputGoal inputGoal, MainPanel rowPanel) {
		List<JComponent> rowElements = new ArrayList<>();
		rowElements.add(elementsMaker
				.createKanaInputWithValidation(japaneseWriting, japaneseWord,
						false, inputGoal, true));
		for (String kanjiWriting : japaneseWriting.getKanjiWritings()) {
			rowElements.add(elementsMaker
					.createKanjiInputWithValidation(kanjiWriting,
							japaneseWriting, japaneseWord, inputGoal, false,
							true));
		}

		return rowElements.toArray(new JComponent[] {});
	}

}

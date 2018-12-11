package com.kanji.list.listRows.japanesePanelCreatingService;

import com.guimaker.enums.InputGoal;
import com.guimaker.model.CommonListElements;
import com.guimaker.panels.MainPanel;
import com.kanji.constants.enums.TypeOfJapaneseWriting;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listRows.japanesePanelCreatingComponents.JapanesePanelActionsCreator;
import com.kanji.list.listRows.japanesePanelCreatingComponents.JapanesePanelElementsCreator;
import com.kanji.utilities.JapaneseWritingUtilities;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class JapanesePanelInViewModeCreator
		implements JapanesePanelCreatingService {

	private JapanesePanelElementsCreator elementsMaker;
	private JapanesePanelActionsCreator actionsCreator;

	public JapanesePanelInViewModeCreator(
			JapanesePanelElementsCreator elementsMaker,
			JapanesePanelActionsCreator actionsCreator) {
		this.elementsMaker = elementsMaker;
		this.actionsCreator = actionsCreator;
	}

	@Override
	public JComponent[] addWritingsRow(JapaneseWriting japaneseWriting,
			CommonListElements<JapaneseWriting> commonListElements,
			JapaneseWord japaneseWord, InputGoal inputGoal,
			MainPanel rowPanel) {
		List<JComponent> rowElements = new ArrayList<>();
		rowElements.add(actionsCreator.switchToHandCursorOnMouseEnter(
				elementsMaker.createKanaInputWithValidation(japaneseWriting,
						japaneseWord, false, inputGoal, true,
						commonListElements)));
		for (String kanjiWriting : japaneseWriting.getKanjiWritings()) {
			if (JapaneseWritingUtilities.isInputEmpty(kanjiWriting,
					TypeOfJapaneseWriting.KANJI)) {
				continue;
			}
			rowElements.add(actionsCreator.switchToHandCursorOnMouseEnter(
					elementsMaker.createKanjiInputWithValidation(kanjiWriting,
							japaneseWriting, japaneseWord, inputGoal, false,
							true, commonListElements)));
		}

		return rowElements.toArray(new JComponent[] {});
	}

}

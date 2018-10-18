package com.kanji.list.listRows.japanesePanelCreatingService;

import com.guimaker.panels.MainPanel;
import com.guimaker.enums.InputGoal;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listRows.japanesePanelCreatingComponents.JapanesePanelElementsCreator;
import com.guimaker.utilities.CommonListElements;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class JapanesePanelInEditModeCreator
		implements JapanesePanelCreatingService {

	private JapanesePanelElementsCreator elementsMaker;

	public JapanesePanelInEditModeCreator(
			JapanesePanelElementsCreator elementsMaker) {
		this.elementsMaker = elementsMaker;
	}

	@Override
	public JComponent[] addWritingsRow(JapaneseWriting japaneseWriting,
			CommonListElements commonListElements, JapaneseWord japaneseWord,
			InputGoal inputGoal, MainPanel rowPanel) {
		List<JComponent> rowElements = new ArrayList<>();
		if (inputGoal.equals(InputGoal.SEARCH)) {
			elementsMaker.createKanaOrKanjiInputForFiltering(japaneseWriting,
					japaneseWord, true, inputGoal, false);
		}
		else {
			rowElements.add(elementsMaker
					.createKanaInputWithValidation(japaneseWriting,
							japaneseWord, true, inputGoal, false));
			//TODO try to use the approach in whole application:
			//GuiElement e = actionMaker.withAction(elementsMaker.createElement)
			for (String kanjiWriting : japaneseWriting.getKanjiWritings()) {
				rowElements.add(elementsMaker
						.createKanjiInputWithValidation(kanjiWriting,
								japaneseWriting, japaneseWord, inputGoal, true,
								false));
			}
		}

		rowElements.add(elementsMaker
				.createButtonAddKanjiWriting(rowPanel, japaneseWriting,
						japaneseWord, inputGoal, true, false));
		rowElements.add(commonListElements.getButtonAddRow());
		rowElements.add(elementsMaker
				.createButonDelete(commonListElements.getButtonDelete(),
						japaneseWord, japaneseWriting, inputGoal));

		return rowElements.toArray(new JComponent[] {});
	}

}

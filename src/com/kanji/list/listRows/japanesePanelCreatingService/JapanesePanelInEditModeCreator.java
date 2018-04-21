package com.kanji.list.listRows.japanesePanelCreatingService;

import com.guimaker.panels.MainPanel;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listRows.japanesePanelCreatingComponents.JapanesePanelElementsCreator;
import com.kanji.utilities.CommonListElements;

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
			boolean forSearchPanel, MainPanel rowPanel) {
		List<JComponent> rowElements = new ArrayList<>();
		rowElements.add(elementsMaker
				.createKanaInputWithValidation(japaneseWriting, japaneseWord,
						true, forSearchPanel));
		//TODO try to use the approach in whole application:
		//GuiElement e = actionMaker.withAction(elementsMaker.createElement)
		for (String kanjiWriting : japaneseWriting.getKanjiWritings()) {
			rowElements.add(elementsMaker
					.createKanjiInputWithValidation(kanjiWriting,
							japaneseWriting, japaneseWord, forSearchPanel));
		}
		rowElements.add(elementsMaker
				.createButtonAddKanjiWriting(rowPanel, japaneseWriting,
						japaneseWord, forSearchPanel));
		rowElements.add(commonListElements.getButtonAddRow());
		rowElements.add(elementsMaker.updateWritingsInWordCheckerWhenDeleteWriting(
				commonListElements.getButtonDelete(), japaneseWord,
				japaneseWriting, forSearchPanel));

		return rowElements.toArray(new JComponent[] {});
	}

}

package com.kanji.list.listRows.japanesePanelCreator;

import com.guimaker.panels.MainPanel;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;
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
			CommonListElements commonListElements, MainPanel rowPanel,
			boolean forSearchPanel) {
		List<JComponent> rowElements = new ArrayList<>();
		rowElements.add(elementsMaker
				.createKanaInputWithValidation(japaneseWriting,
						wordContainingWriting, true, forSearchPanel));
		//TODO try to use the approach in whole application:
		//GuiElement e = actionMaker.withAction(elementsMaker.createElement)
		for (String kanjiWriting : japaneseWriting.getKanjiWritings()) {
			rowElements.add(elementsMaker
					.createKanjiInputWithValidation(kanjiWriting,
							japaneseWriting, wordContainingWriting,
							forSearchPanel));
		}
		rowElements.add(elementsMaker
				.createButtonAddKanjiWriting(rowPanel, japaneseWriting,
						wordContainingWriting, forSearchPanel));
		rowElements.add(commonListElements.getButtonAddRow());
		rowElements.add(commonListElements.getButtonDelete());

		return rowElements.toArray(new JComponent[] {});
	}

}

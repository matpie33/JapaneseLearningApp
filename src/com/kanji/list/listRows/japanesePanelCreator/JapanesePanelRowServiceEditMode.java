package com.kanji.list.listRows.japanesePanelCreator;

import com.guimaker.panels.MainPanel;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class JapanesePanelRowServiceEditMode
		implements JapanesePanelRowCreatingService {

	private JapanesePanelElementsMaker elementsMaker;
	private JapaneseWordInformation wordContainingWriting;

	public JapanesePanelRowServiceEditMode(
			JapanesePanelElementsMaker elementsMaker,
			JapaneseWordInformation wordContainingWriting) {
		this.elementsMaker = elementsMaker;
		this.wordContainingWriting = wordContainingWriting;
	}

	@Override
	public JComponent[] addWritingsRow(JapaneseWriting japaneseWriting,
			CommonListElements commonListElements, MainPanel rowPanel) {
		List<JComponent> rowElements = new ArrayList<>();
		rowElements.add(elementsMaker
				.createKanaTextField(japaneseWriting.getKanaWriting(),
						japaneseWriting, wordContainingWriting));
		//TODO try to use the approach in whole application:
		//GuiElement e = actionMaker.withAction(elementsMaker.createElement)
		for (String kanjiWriting : japaneseWriting.getKanjiWritings()) {
			rowElements.add(elementsMaker
					.createKanjiTextField(kanjiWriting, japaneseWriting,
							wordContainingWriting));
		}
		rowElements.add(elementsMaker
				.createButtonAddKanjiWriting(rowPanel, japaneseWriting,
						wordContainingWriting));
		rowElements.add(commonListElements.getButtonAddRow());
		rowElements.add(commonListElements.getButtonDelete());

		return rowElements.toArray(new JComponent[] {});
	}

}

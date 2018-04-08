package com.kanji.list.listRows.japanesePanelCreator;

import com.guimaker.panels.MainPanel;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class JapanesePanelServiceViewMode
		implements JapanesePanelCreatingService {

	private JapanesePanelElementsMaker elementsMaker;
	private JapaneseWordInformation wordContainingWriting;

	public JapanesePanelServiceViewMode(
			JapanesePanelElementsMaker elementsMaker) {
		this.elementsMaker = elementsMaker;
	}

	@Override
	public void setWord(
			JapaneseWordInformation wordContainingWriting) {
		this.wordContainingWriting = wordContainingWriting;
	}

	@Override
	public JComponent[] addWritingsRow(JapaneseWriting japaneseWriting,
			CommonListElements commonListElements, MainPanel rowPanel) {
		List<JComponent> rowElements = new ArrayList<>();
		rowElements.add(elementsMaker
				.createKanaTextField(japaneseWriting.getKanaWriting(),
						japaneseWriting, wordContainingWriting));
		for (String kanjiWriting : japaneseWriting.getKanjiWritings()) {
			rowElements.add(elementsMaker
					.createKanjiTextField(kanjiWriting, japaneseWriting,
							wordContainingWriting));
		}

		return rowElements.toArray(new JComponent[] {});
	}

}

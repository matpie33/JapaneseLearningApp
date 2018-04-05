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

	private JapaneseWordInformation japaneseWordInformation;

	public JapanesePanelRowServiceEditMode(
			JapaneseWordInformation japaneseWordInformation) {
		this.japaneseWordInformation = japaneseWordInformation;
	}

	@Override
	public JComponent[] addWritingsRow(JapaneseWriting japaneseWriting,
			CommonListElements commonListElements, MainPanel rowPanel) {
		List<JComponent> rowElements = new ArrayList<>();
		rowElements.add(JapanesePanelElementsMaker
				.createKanaOrKanjiTextField(
						japaneseWriting.getKanaWriting()));
		for (String kanjiWriting : japaneseWriting.getKanjiWritings()) {
			rowElements.add(JapanesePanelElementsMaker
					.createKanaOrKanjiTextField(kanjiWriting));
		}
		rowElements.add(JapanesePanelElementsMaker
				.createButtonAddKanjiWriting(rowPanel));
		rowElements.add(commonListElements.getButtonAddRow());
		rowElements.add(commonListElements.getButtonDelete());

		return rowElements.toArray(new JComponent[] {});
	}
}

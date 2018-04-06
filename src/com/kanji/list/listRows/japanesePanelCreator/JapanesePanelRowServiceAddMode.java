package com.kanji.list.listRows.japanesePanelCreator;

import com.guimaker.panels.MainPanel;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class JapanesePanelRowServiceAddMode
		implements JapanesePanelRowCreatingService {

	@Override
	public JComponent[] addWritingsRow(JapaneseWriting japaneseWriting,
			CommonListElements commonListElements, MainPanel rowPanel) {
		List<JComponent> rowElements = new ArrayList<>();
		rowElements.add(JapanesePanelElementsMaker.createKanaTextField(""));
		rowElements.add(JapanesePanelElementsMaker.createKanjiTextField(""));
		rowElements.add(JapanesePanelElementsMaker
				.createButtonAddKanjiWriting(rowPanel));
		rowElements.add(commonListElements.getButtonAddRow());
		rowElements.add(commonListElements.getButtonDelete());
		return rowElements.toArray(new JComponent[] {});
	}
}

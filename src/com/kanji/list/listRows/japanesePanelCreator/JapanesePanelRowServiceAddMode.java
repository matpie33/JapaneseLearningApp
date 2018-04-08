package com.kanji.list.listRows.japanesePanelCreator;

import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class JapanesePanelRowServiceAddMode
		implements JapanesePanelRowCreatingService {

	private JapanesePanelElementsMaker elementsMaker;
	private JapaneseWordInformation wordContainingWriting;

	public JapanesePanelRowServiceAddMode(
			JapanesePanelElementsMaker elementsMaker,
			JapaneseWordInformation wordContainingWriting) {
		this.wordContainingWriting = wordContainingWriting;
		this.elementsMaker = elementsMaker;
	}

	@Override
	public JComponent[] addWritingsRow(JapaneseWriting japaneseWriting,
			CommonListElements commonListElements, MainPanel rowPanel) {
		List<JComponent> rowElements = new ArrayList<>();
		rowElements.add(elementsMaker.createKanaTextField("", japaneseWriting,
				wordContainingWriting));
		//TODO try to use the approach in whole application:
		//GuiElement e = actionMaker.withAction(elementsMaker.createElement)
		rowElements.add(elementsMaker.createKanjiTextField("", japaneseWriting,
				wordContainingWriting));
		rowElements.add(elementsMaker
				.createButtonAddKanjiWriting(rowPanel, japaneseWriting,
						wordContainingWriting));
		rowElements.add(commonListElements.getButtonAddRow());
		rowElements.add(commonListElements.getButtonDelete());
		return rowElements.toArray(new JComponent[] {});
	}

	@Override
	public JLabel getRowLabel() {
		return GuiMaker.createLabel(
				new ComponentOptions().foregroundColor(Color.WHITE).text("+"));
	}
}

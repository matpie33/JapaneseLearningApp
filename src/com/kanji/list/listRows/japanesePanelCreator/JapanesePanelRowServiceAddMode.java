package com.kanji.list.listRows.japanesePanelCreator;

import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listRows.japanesePanelActionsCreator.JapanesePanelEditOrAddModeAction;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class JapanesePanelRowServiceAddMode
		implements JapanesePanelRowCreatingService {

	private JapanesePanelEditOrAddModeAction actionMaker;
	private JapaneseWordInformation wordContainingWriting;

	public JapanesePanelRowServiceAddMode(
			JapanesePanelEditOrAddModeAction actionMaker,
			JapaneseWordInformation wordContainingWriting) {
		this.actionMaker = actionMaker;
		this.wordContainingWriting = wordContainingWriting;
	}

	@Override
	public JComponent[] addWritingsRow(JapaneseWriting japaneseWriting,
			CommonListElements commonListElements, MainPanel rowPanel) {
		List<JComponent> rowElements = new ArrayList<>();
		rowElements.add(actionMaker.withKanaValidation(
				JapanesePanelElementsMaker.createKanaTextField(""),
				japaneseWriting, wordContainingWriting));
		//TODO try to use the approach in whole application:
		//GuiElement e = actionMaker.withAction(elementsMaker.createElement)
		rowElements.add(actionMaker.withKanjiValidation(
				JapanesePanelElementsMaker.createKanjiTextField(""),
				japaneseWriting, wordContainingWriting));
		rowElements.add(JapanesePanelElementsMaker
				.createButtonAddKanjiWriting(rowPanel));
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

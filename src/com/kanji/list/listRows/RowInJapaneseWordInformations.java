package com.kanji.list.listRows;

import com.guimaker.panels.MainPanel;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listRows.panelCreators.JapaneseWordPanelCreator;
import com.kanji.list.myList.ListRowMaker;
import com.kanji.utilities.CommonListElements;
import com.kanji.windows.ApplicationWindow;

import javax.swing.text.JTextComponent;
import java.util.List;
import java.util.Map;

public class RowInJapaneseWordInformations implements ListRowMaker<JapaneseWordInformation> {
	private JapaneseWordPanelCreator japaneseWordPanelCreator;
	private ApplicationWindow applicationWindow;

	public RowInJapaneseWordInformations(ApplicationWindow applicationWindow) {
		japaneseWordPanelCreator = new JapaneseWordPanelCreator(applicationWindow);
	}

	@Override
	public MainPanel createListRow(JapaneseWordInformation japaneseWord, CommonListElements commonListElements) {
		MainPanel panel = japaneseWordPanelCreator.createPanelInViewMode(
				japaneseWord, commonListElements, this);
		return panel;
	}

	public JapaneseWordPanelCreator getJapaneseWordPanelCreator() {
		 return japaneseWordPanelCreator;
	}
}

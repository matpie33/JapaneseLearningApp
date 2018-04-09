package com.kanji.list.listRows;

import com.guimaker.panels.MainPanel;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listRows.japanesePanelCreator.JapaneseWordPanelCreator;
import com.kanji.list.myList.ListRowMaker;
import com.kanji.problematicWords.ProblematicJapaneseWordsDisplayer;
import com.kanji.utilities.CommonListElements;

public class RowInJapaneseWordsReviewingList
		implements ListRowMaker<JapaneseWordInformation> {

	private ProblematicJapaneseWordsDisplayer problematicJapaneseWordsDisplayer;
	private JapaneseWordPanelCreator japanesePanelCreator;

	public RowInJapaneseWordsReviewingList(
			ProblematicJapaneseWordsDisplayer problematicJapaneseWordsDisplayer,
			JapaneseWordPanelCreator japanesePanelCreator) {
		this.problematicJapaneseWordsDisplayer = problematicJapaneseWordsDisplayer;
		this.japanesePanelCreator = japanesePanelCreator;
	}

	@Override
	public MainPanel createListRow(JapaneseWordInformation japaneseWord,
			CommonListElements commonListElements) {
		MainPanel panel = new MainPanel(null);
		japanesePanelCreator
				.setRowNumberLabel(commonListElements.getRowNumberLabel());
		japanesePanelCreator
				.addJapanesePanelToExistingPanel(panel, japaneseWord);

		return panel;
	}

}

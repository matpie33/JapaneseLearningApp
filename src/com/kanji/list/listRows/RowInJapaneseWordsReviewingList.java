package com.kanji.list.listRows;

import com.guimaker.enums.ComponentType;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listRows.japanesePanelCreator.JapanesePanelRowServiceViewMode;
import com.kanji.list.listRows.japanesePanelCreator.JapaneseWordPanelCreator;
import com.kanji.list.myList.ListRowMaker;
import com.kanji.problematicWords.ProblematicJapaneseWordsDisplayer;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;
import java.awt.event.ActionEvent;

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
				.addJapanesePanelToExistingPanel(panel, japaneseWord,
						new JapanesePanelRowServiceViewMode(
								japanesePanelCreator.getElementsMaker(),
								japaneseWord), null);

		return panel;
	}

	private AbstractButton createButtonSearchWord() {
		return GuiMaker.createButtonlikeComponent(ComponentType.BUTTON,
				ButtonsNames.SEARCH_IN_DICTIONARY, new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						problematicJapaneseWordsDisplayer
								.searchCurrentWordInDictionary();
					}
				});
	}

}

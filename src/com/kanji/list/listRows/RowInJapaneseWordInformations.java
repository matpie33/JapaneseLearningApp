package com.kanji.list.listRows;

import com.guimaker.application.DialogWindow;
import com.guimaker.enums.InputGoal;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.list.ListRowData;
import com.guimaker.list.myList.ListRowCreator;
import com.guimaker.model.CommonListElements;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listRows.japanesePanelCreatingComponents.JapaneseWordPanel;
import com.kanji.list.listRows.japanesePanelCreatingComponents.JapaneseWordPanelCreator;
import com.kanji.panelsAndControllers.controllers.ApplicationController;

public class RowInJapaneseWordInformations
		implements ListRowCreator<JapaneseWord> {
	private JapaneseWordPanel japaneseWordPanel;

	public RowInJapaneseWordInformations(
			ApplicationController applicationController, DialogWindow dialog,
			PanelDisplayMode panelDisplayMode) {
		japaneseWordPanel = new JapaneseWordPanel(dialog, panelDisplayMode,
				applicationController);
	}

	public JapaneseWordPanel getJapaneseWordPanel() {
		return japaneseWordPanel;
	}

	public JapaneseWordPanelCreator getJapaneseWordsPanelCreator() {
		return japaneseWordPanel.getJapaneseWordPanelCreator();
	}

	@Override
	public ListRowData<JapaneseWord> createListRow(JapaneseWord japaneseWord,
			CommonListElements<JapaneseWord> commonListElements,
			InputGoal inputGoal) {

		return japaneseWordPanel.createElements(japaneseWord, inputGoal,
				commonListElements);
	}

}

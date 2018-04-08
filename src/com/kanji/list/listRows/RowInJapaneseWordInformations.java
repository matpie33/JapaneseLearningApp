package com.kanji.list.listRows;

import com.guimaker.panels.MainPanel;
import com.kanji.constants.enums.ListPanelDisplayMode;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listRows.japanesePanelActionsCreator.JapanesePanelEditOrAddModeAction;
import com.kanji.list.listRows.japanesePanelCreator.JapanesePanelElementsMaker;
import com.kanji.list.listRows.japanesePanelCreator.JapanesePanelRowServiceEditMode;
import com.kanji.list.listRows.japanesePanelCreator.JapaneseWordPanelCreator;
import com.kanji.list.myList.ListRowMaker;
import com.kanji.utilities.CommonListElements;
import com.kanji.windows.ApplicationWindow;

public class RowInJapaneseWordInformations
		implements ListRowMaker<JapaneseWordInformation> {
	private JapaneseWordPanelCreator japaneseWordPanelCreator;
	private ApplicationWindow applicationWindow;
	private JapanesePanelElementsMaker elementsMaker;

	public RowInJapaneseWordInformations(ApplicationWindow applicationWindow) {
		JapanesePanelEditOrAddModeAction actionMaker = new JapanesePanelEditOrAddModeAction(
				applicationWindow.getApplicationController(), applicationWindow,
				applicationWindow.getApplicationController().getJapaneseWords(),
				ListPanelDisplayMode.VIEW_AND_EDIT);
		elementsMaker = new JapanesePanelElementsMaker(actionMaker);
		japaneseWordPanelCreator = new JapaneseWordPanelCreator(
				applicationWindow.getApplicationController(), actionMaker,
				elementsMaker);
		this.applicationWindow = applicationWindow;
	}

	@Override
	public MainPanel createListRow(JapaneseWordInformation japaneseWord,
			CommonListElements commonListElements) {
		MainPanel panel = new MainPanel(null);
		japaneseWordPanelCreator
				.setRowNumberLabel(commonListElements.getRowNumberLabel());
		japaneseWordPanelCreator
				.addJapanesePanelToExistingPanel(panel, japaneseWord,
						new JapanesePanelRowServiceEditMode(elementsMaker,
								japaneseWord), applicationWindow);
		return panel;
	}
}

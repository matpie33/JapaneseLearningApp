package com.kanji.list.listRows;

import com.guimaker.panels.MainPanel;
import com.kanji.constants.enums.ListPanelDisplayMode;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listRows.japanesePanelActionsCreator.JapanesePanelEditOrAddModeAction;
import com.kanji.list.listRows.japanesePanelCreator.JapanesePanelRowServiceEditMode;
import com.kanji.list.listRows.japanesePanelCreator.JapaneseWordPanelCreator;
import com.kanji.list.myList.ListRowMaker;
import com.kanji.list.myList.MyList;
import com.kanji.utilities.CommonListElements;
import com.kanji.windows.ApplicationWindow;

public class RowInJapaneseWordInformations
		implements ListRowMaker<JapaneseWordInformation> {
	private JapaneseWordPanelCreator japaneseWordPanelCreator;
	private ApplicationWindow applicationWindow;

	public RowInJapaneseWordInformations(ApplicationWindow applicationWindow) {
		japaneseWordPanelCreator = new JapaneseWordPanelCreator(
				applicationWindow.getApplicationController(),
				new JapanesePanelEditOrAddModeAction(applicationWindow,
						applicationWindow.getApplicationController()
								.getJapaneseWords(),
						ListPanelDisplayMode.VIEW_AND_EDIT));
		this.applicationWindow = applicationWindow;
	}

	@Override
	public MainPanel createListRow(JapaneseWordInformation japaneseWord,
			CommonListElements commonListElements) {
		MainPanel panel = japaneseWordPanelCreator.createPanel(japaneseWord,
				new JapanesePanelRowServiceEditMode(
						new JapanesePanelEditOrAddModeAction(applicationWindow,
								applicationWindow.getApplicationController()
										.getJapaneseWords(),
								ListPanelDisplayMode.VIEW_AND_EDIT),
						japaneseWord), applicationWindow);
		return panel;
	}

	public JapaneseWordPanelCreator getJapaneseWordPanelCreator() {
		return japaneseWordPanelCreator;
	}
}

package com.kanji.list.listRows.japanesePanelCreator;

import com.kanji.constants.enums.JapanesePanelDisplayMode;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listRows.japanesePanelActionsCreator.JapanesePanelEditOrAddModeAction;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.windows.DialogWindow;

public class JapanesePanelServiceStore {
	private JapanesePanelCreatingService panelCreatingService;
	private JapanesePanelEditOrAddModeAction actionMaker;
	private JapanesePanelElementsMaker elementsMaker;

	public JapanesePanelServiceStore(ApplicationController applicationController,
			DialogWindow parentDialog,
			MyList<JapaneseWordInformation> wordsList,
			JapanesePanelDisplayMode japanesePanelDisplayMode) {
		initializeActionMaker(applicationController, parentDialog, wordsList,
				japanesePanelDisplayMode);
		elementsMaker = new JapanesePanelElementsMaker(actionMaker);
		getPanelRowService(japanesePanelDisplayMode);
	}

	private void getPanelRowService(JapanesePanelDisplayMode japanesePanelDisplayMode) {
		switch (japanesePanelDisplayMode) {
		case EDIT:
			panelCreatingService = new JapanesePanelServiceEditMode(
					elementsMaker);
			break;
		case VIEW:
			panelCreatingService = new JapanesePanelServiceViewMode(
					elementsMaker);
			break;
		}
	}

	private void initializeActionMaker(
			ApplicationController applicationController,
			DialogWindow parentDialog,
			MyList<JapaneseWordInformation> wordsList,
			JapanesePanelDisplayMode japanesePanelDisplayMode) {
		actionMaker = new JapanesePanelEditOrAddModeAction(
				applicationController, parentDialog, wordsList,
				japanesePanelDisplayMode);
	}

	public JapanesePanelCreatingService getPanelCreatingService() {
		return panelCreatingService;
	}

	public JapanesePanelEditOrAddModeAction getActionMaker() {
		return actionMaker;
	}

	public JapanesePanelElementsMaker getElementsMaker() {
		return elementsMaker;
	}
}

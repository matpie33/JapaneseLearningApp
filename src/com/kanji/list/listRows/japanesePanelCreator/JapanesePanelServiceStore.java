package com.kanji.list.listRows.japanesePanelCreator;

import com.kanji.constants.enums.JapanesePanelDisplayMode;
import com.kanji.list.listRows.japanesePanelActionsCreator.JapanesePanelEditOrAddModeAction;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.windows.DialogWindow;

public class JapanesePanelServiceStore {
	private JapanesePanelCreatingService panelCreatingService;
	private JapanesePanelEditOrAddModeAction actionMaker;
	private JapanesePanelElementsMaker elementsMaker;

	public JapanesePanelServiceStore(
			ApplicationController applicationController,
			DialogWindow parentDialog,
			JapanesePanelDisplayMode japanesePanelDisplayMode) {
		initializeActionMaker(applicationController, parentDialog,
				japanesePanelDisplayMode);
		elementsMaker = new JapanesePanelElementsMaker(actionMaker);
		getPanelRowService(japanesePanelDisplayMode);
	}

	private void getPanelRowService(
			JapanesePanelDisplayMode japanesePanelDisplayMode) {
		switch (japanesePanelDisplayMode) {
		case EDIT:
			panelCreatingService = new JapanesePanelServiceEditMode(
					elementsMaker);
			break;
		case VIEW:
			panelCreatingService = new JapanesePanelServiceViewMode(
					elementsMaker, new TextFieldSelectionHandler());
			break;
		}
	}

	private void initializeActionMaker(
			ApplicationController applicationController,
			DialogWindow parentDialog,
			JapanesePanelDisplayMode japanesePanelDisplayMode) {
		actionMaker = new JapanesePanelEditOrAddModeAction(
				applicationController, parentDialog, japanesePanelDisplayMode);
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

	public TextFieldSelectionHandler getSelectionHandler() {
		if (panelCreatingService instanceof JapanesePanelServiceViewMode) {
			return ((JapanesePanelServiceViewMode) panelCreatingService)
					.getSelectionHandler();
		}
		else {
			throw new IllegalArgumentException(
					"Only panel in view mode contains selection hanler - and this panel is not view mode");
		}

	}
}

package com.kanji.list.listRows.japanesePanelCreator;

import com.kanji.constants.enums.JapanesePanelDisplayMode;
import com.kanji.list.listRows.japanesePanelActionsCreator.JapanesePanelActions;
import com.kanji.list.listRows.japanesePanelActionsCreator.JapanesePanelEditOrAddModeAction;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.windows.DialogWindow;

public class JapanesePanelServiceStore {
	private JapanesePanelCreatingService panelCreatingService;
	private JapanesePanelEditOrAddModeAction actionMaker;
	private JapanesePanelElementsMaker elementsMaker;
	private JapanesePanelActions actionsCreator;

	public JapanesePanelServiceStore(
			ApplicationController applicationController,
			DialogWindow parentDialog, JapanesePanelDisplayMode displayMode) {
		actionsCreator = new JapanesePanelActions();
		initializeActionMaker(applicationController, parentDialog);
		elementsMaker = new JapanesePanelElementsMaker(actionMaker,
				actionsCreator);
		getPanelRowService(displayMode);
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
					elementsMaker, new TextFieldSelectionHandler(),
					actionsCreator);
			break;
		}
	}

	private void initializeActionMaker(
			ApplicationController applicationController,
			DialogWindow parentDialog) {
		actionMaker = new JapanesePanelEditOrAddModeAction(
				applicationController, parentDialog, actionsCreator);
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

	public JapanesePanelActions getActionCreator() {
		return actionsCreator;
	}
}

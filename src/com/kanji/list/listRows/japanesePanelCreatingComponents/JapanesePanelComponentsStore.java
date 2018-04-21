package com.kanji.list.listRows.japanesePanelCreatingComponents;

import com.kanji.constants.enums.JapanesePanelDisplayMode;
import com.kanji.list.listRows.japanesePanelCreatingService.JapanesePanelCreatingService;
import com.kanji.list.listRows.japanesePanelCreatingService.JapanesePanelInEditModeCreator;
import com.kanji.list.listRows.japanesePanelCreatingService.JapanesePanelInViewModeCreator;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.windows.DialogWindow;

public class JapanesePanelComponentsStore {
	private JapanesePanelCreatingService panelCreatingService;
	private JapanesePanelElementsCreator elementsMaker;
	private JapanesePanelActionsCreator actionsCreator;

	public JapanesePanelComponentsStore(
			ApplicationController applicationController,
			DialogWindow parentDialog, JapanesePanelDisplayMode displayMode) {
		actionsCreator = new JapanesePanelActionsCreator(parentDialog,
				applicationController);
		elementsMaker = new JapanesePanelElementsCreator(actionsCreator);
		getPanelRowService(displayMode);
	}

	private void getPanelRowService(
			JapanesePanelDisplayMode japanesePanelDisplayMode) {
		switch (japanesePanelDisplayMode) {
		case EDIT:
			panelCreatingService = new JapanesePanelInEditModeCreator(
					elementsMaker);
			break;
		case VIEW:
			panelCreatingService = new JapanesePanelInViewModeCreator(
					elementsMaker, new TextFieldSelectionHandler(),
					actionsCreator);
			break;
		}
	}

	public JapanesePanelCreatingService getPanelCreatingService() {
		return panelCreatingService;
	}

	public JapanesePanelElementsCreator getElementsMaker() {
		return elementsMaker;
	}

	public TextFieldSelectionHandler getSelectionHandler() {
		if (panelCreatingService instanceof JapanesePanelInViewModeCreator) {
			return ((JapanesePanelInViewModeCreator) panelCreatingService)
					.getSelectionHandler();
		}
		else {
			throw new IllegalStateException(
					"Selection handler belongs only to panel in view mode.");
		}

	}

	public JapanesePanelActionsCreator getActionCreator() {
		return actionsCreator;
	}
}

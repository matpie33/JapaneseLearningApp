package com.kanji.list.listRows.japanesePanelCreatingComponents;

import com.guimaker.enums.PanelDisplayMode;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listRows.japanesePanelCreatingService.JapanesePanelCreatingService;
import com.kanji.list.listRows.japanesePanelCreatingService.JapanesePanelInEditModeCreator;
import com.kanji.list.listRows.japanesePanelCreatingService.JapanesePanelInViewModeCreator;
import com.kanji.list.listeners.InputValidationListener;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.windows.DialogWindow;

import java.util.Set;

public class JapanesePanelComponentsStore {
	private JapanesePanelCreatingService panelCreatingService;
	private JapanesePanelElementsCreator elementsMaker;
	private JapanesePanelActionsCreator actionsCreator;

	public JapanesePanelComponentsStore(
			ApplicationController applicationController,
			DialogWindow parentDialog, PanelDisplayMode displayMode) {
		actionsCreator = new JapanesePanelActionsCreator(parentDialog,
				applicationController);
		elementsMaker = new JapanesePanelElementsCreator(actionsCreator);
		getPanelRowService(displayMode);
	}

	private void getPanelRowService(PanelDisplayMode panelDisplayMode) {
		switch (panelDisplayMode) {
		case EDIT:
			panelCreatingService = new JapanesePanelInEditModeCreator(
					elementsMaker);
			break;
		case VIEW:
			panelCreatingService = new JapanesePanelInViewModeCreator(
					elementsMaker, actionsCreator);
			break;
		}
	}

	public JapanesePanelCreatingService getPanelCreatingService() {
		return panelCreatingService;
	}

	public JapanesePanelElementsCreator getElementsMaker() {
		return elementsMaker;
	}

	public JapanesePanelActionsCreator getActionCreator() {
		return actionsCreator;
	}

	public void addValidationListeners(
			Set<InputValidationListener<JapaneseWord>> validationListeners) {
		elementsMaker.addValidationListeners(validationListeners);
	}
}

package com.kanji.list.listRows.japanesePanelCreatingComponents;

import com.guimaker.enums.PanelDisplayMode;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listRows.japanesePanelCreatingService.JapanesePanelCreatingService;
import com.kanji.list.listRows.japanesePanelCreatingService.JapanesePanelInEditModeCreator;
import com.kanji.list.listRows.japanesePanelCreatingService.JapanesePanelInViewModeCreator;
import com.kanji.list.listeners.InputValidationListener;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.windows.DialogWindow;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JapanesePanelComponentsStore {
	private JapanesePanelElementsCreator elementsMaker;
	private JapanesePanelActionsCreator actionsCreator;
	private Map<PanelDisplayMode, JapanesePanelCreatingService> panelCreatingServicesForDisplayModes = new HashMap<>();

	public JapanesePanelComponentsStore(
			ApplicationController applicationController,
			DialogWindow parentDialog) {
		actionsCreator = new JapanesePanelActionsCreator(parentDialog,
				applicationController);
		elementsMaker = new JapanesePanelElementsCreator(actionsCreator,
				applicationController, parentDialog);
		initializePanelRowServices();
	}

	private void initializePanelRowServices() {
		panelCreatingServicesForDisplayModes.put(PanelDisplayMode.EDIT,
				new JapanesePanelInEditModeCreator(elementsMaker));
		panelCreatingServicesForDisplayModes.put(PanelDisplayMode.VIEW,
				new JapanesePanelInViewModeCreator(elementsMaker,
						actionsCreator));
	}

	public JapanesePanelCreatingService getPanelCreatingService(
			PanelDisplayMode panelDisplayMode) {
		return panelCreatingServicesForDisplayModes.get(panelDisplayMode);
	}

	public JapanesePanelElementsCreator getElementsCreator() {
		return elementsMaker;
	}

	public JapanesePanelActionsCreator getActionCreator() {
		return actionsCreator;
	}

	public void addValidationListeners(
			Set<InputValidationListener<JapaneseWord>> validationListeners) {
		elementsMaker.addValidationListeners(validationListeners);
	}

	public void setWordsList(MyList<JapaneseWord> list){
		actionsCreator.setWordsList(list);
	}

}

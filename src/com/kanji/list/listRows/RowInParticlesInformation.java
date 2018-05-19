package com.kanji.list.listRows;

import com.guimaker.enums.FillType;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.InputGoal;
import com.kanji.japaneseParticlesPanel.*;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listeners.InputValidationListener;
import com.kanji.list.myList.ListRowCreator;
import com.kanji.list.myList.ListRowData;
import com.kanji.model.WordParticlesData;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.utilities.CommonListElements;

public class RowInParticlesInformation
		implements ListRowCreator<WordParticlesData> {

	private JapaneseWord japaneseWord;
	private ApplicationController applicationController;
	private JapaneseParticleRowCreatingService japaneseParticleRowCreatingService;

	public RowInParticlesInformation(JapaneseWord japaneseWord,
			ApplicationController applicationController,
			PanelDisplayMode displayMode) {
		this.japaneseWord = japaneseWord;
		this.applicationController = applicationController;
		getRowCreatingService(displayMode);
	}

	private void getRowCreatingService(PanelDisplayMode displayMode) {
		switch (displayMode) {
		case VIEW:
			japaneseParticleRowCreatingService = new JapaneseParticleRowInViewMode();
			break;
		case EDIT:
			japaneseParticleRowCreatingService = new JapaneseParticleRowInEditMode(
					applicationController);
			break;
		}
	}

	@Override
	public ListRowData<WordParticlesData> createListRow(
			WordParticlesData wordParticlesData,
			CommonListElements commonListElements, InputGoal inputGoal) {

		MainPanel panel = new MainPanel(null);
		panel.addRow(SimpleRowBuilder.createRow(FillType.HORIZONTAL,
				japaneseParticleRowCreatingService
						.createRowElements(wordParticlesData, japaneseWord,
								commonListElements)));

		return new ListRowData<>(panel);
	}

	@Override
	public void addValidationListener(
			InputValidationListener<WordParticlesData> inputValidationListener) {
	}

}

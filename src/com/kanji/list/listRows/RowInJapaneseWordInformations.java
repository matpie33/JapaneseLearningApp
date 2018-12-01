package com.kanji.list.listRows;

import com.guimaker.enums.InputGoal;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.list.ListRowData;
import com.guimaker.list.myList.ListRowCreator;
import com.guimaker.listeners.InputValidationListener;
import com.guimaker.model.CommonListElements;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listRows.japanesePanelCreatingComponents.JapaneseWordPanelCreator;

import javax.swing.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class RowInJapaneseWordInformations
		implements ListRowCreator<JapaneseWord> {
	private JapaneseWordPanelCreator japaneseWordsPanelCreator;
	private Optional<JapaneseWordPanelCreator> searchOrAddDialogPanelCreator;

	public RowInJapaneseWordInformations(
			JapaneseWordPanelCreator japaneseWordsPanelCreator) {
		this.japaneseWordsPanelCreator = japaneseWordsPanelCreator;
		searchOrAddDialogPanelCreator = Optional.empty();
	}


	@Override
	public ListRowData<JapaneseWord> createListRow(JapaneseWord japaneseWord,
			CommonListElements<JapaneseWord> commonListElements, InputGoal
			inputGoal) {

		JapaneseWordPanelCreator panelCreatorToUse = getJapaneseWordPanelCreator(
				commonListElements.isForSingleRowOnly());

		return panelCreatorToUse.createJapaneseWordPanel(
				japaneseWord, inputGoal, commonListElements);
	}


	private JapaneseWordPanelCreator getJapaneseWordPanelCreator(
			boolean forSingleRowOnly) {
		JapaneseWordPanelCreator panelCreatorToUse;
		if (forSingleRowOnly) {
			panelCreatorToUse = searchOrAddDialogPanelCreator.orElse(
					japaneseWordsPanelCreator.copy());
		}
		else {
			panelCreatorToUse = japaneseWordsPanelCreator;
		}
		return panelCreatorToUse;
	}

	private PanelDisplayMode getPanelDisplayMode(
			CommonListElements commonListElements, InputGoal inputGoal) {
		PanelDisplayMode displayMode = japaneseWordsPanelCreator.getDisplayMode();
		if (displayMode.equals(PanelDisplayMode.VIEW) && (
				inputGoal.equals(InputGoal.EDIT_TEMPORARILY)
						|| commonListElements.isForSingleRowOnly())) {
			displayMode = PanelDisplayMode.EDIT;
		}
		return displayMode;
	}

}

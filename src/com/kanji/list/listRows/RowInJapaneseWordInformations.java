package com.kanji.list.listRows;

import com.guimaker.enums.InputGoal;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.list.ListRowData;
import com.guimaker.list.myList.ListRowCreator;
import com.guimaker.listeners.InputValidationListener;
import com.guimaker.utilities.CommonListElements;
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
	private Set<InputValidationListener<JapaneseWord>> validationListeners = new HashSet<>();

	public RowInJapaneseWordInformations(
			JapaneseWordPanelCreator japaneseWordsPanelCreator) {
		this.japaneseWordsPanelCreator = japaneseWordsPanelCreator;
		searchOrAddDialogPanelCreator = Optional.empty();
	}

	@Override
	public void addValidationListener(
			InputValidationListener<JapaneseWord> validationListener) {
		//TODO this method looks the same in implementors or it's not needed at all for others
		this.validationListeners.add(validationListener);
	}

	@Override
	public ListRowData<JapaneseWord> createListRow(JapaneseWord japaneseWord,
			CommonListElements commonListElements, InputGoal inputGoal) {
		PanelDisplayMode displayMode = getPanelDisplayMode(commonListElements,
				inputGoal);

		JLabel rowNumberLabel = commonListElements.getRowNumberLabel();

		JapaneseWordPanelCreator panelCreatorToUse = getJapaneseWordPanelCreator(
				commonListElements.isForSingleRowOnly());
		setPanelCreatorProperties(commonListElements, rowNumberLabel,
				panelCreatorToUse);
		ListRowData<JapaneseWord> rowData = panelCreatorToUse.createJapaneseWordPanel(
				japaneseWord, inputGoal, commonListElements);

		return rowData;
	}

	private void setPanelCreatorProperties(
			CommonListElements commonListElements, JLabel rowNumberLabel,
			JapaneseWordPanelCreator panelCreatorToUse) {
		panelCreatorToUse.addValidationListeners(validationListeners);
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

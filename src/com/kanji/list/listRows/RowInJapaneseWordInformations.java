package com.kanji.list.listRows;

import com.guimaker.panels.MainPanel;
import com.kanji.constants.enums.InputGoal;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listRows.japanesePanelCreatingComponents.JapaneseWordPanelCreator;
import com.kanji.list.myList.InputValidationListener;
import com.kanji.list.myList.ListRowCreator;
import com.kanji.list.myList.ListRowData;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class RowInJapaneseWordInformations
		implements ListRowCreator<JapaneseWord> {
	private JapaneseWordPanelCreator newWordsPanelCreator;
	private Optional<JapaneseWordPanelCreator> searchOrAddDialogPanelCreator;
	private Set<InputValidationListener<JapaneseWord>> validationListeners = new HashSet<>();

	public RowInJapaneseWordInformations(
			JapaneseWordPanelCreator newWordsPanelCreator) {
		this.newWordsPanelCreator = newWordsPanelCreator;
		searchOrAddDialogPanelCreator = Optional.empty();
	}

	@Override
	public void addValidationListener(
			InputValidationListener<JapaneseWord> validationListener) {
		//TODO this method looks the same in implementors or it's not needed at all for others
		this.validationListeners.add(validationListener);
	}

	@Override
	public ListRowData createListRow(JapaneseWord japaneseWord,
			CommonListElements commonListElements, InputGoal inputGoal) {
		MainPanel panel = new MainPanel(null);
		JLabel rowNumberLabel = commonListElements.getRowNumberLabel();
		JapaneseWordPanelCreator panelCreatorToUse;
		if (commonListElements.isForSingleRowOnly()){
			panelCreatorToUse = searchOrAddDialogPanelCreator.orElse(
					newWordsPanelCreator.copy());
		}
		else{
			panelCreatorToUse = newWordsPanelCreator;
		}
		panelCreatorToUse.addValidationListeners (validationListeners);
		panelCreatorToUse.setRowNumberLabel(rowNumberLabel);
		panelCreatorToUse
				.setLabelsColor(commonListElements.getLabelsColor());
		ListRowData<JapaneseWord> rowData = panelCreatorToUse
				.addJapanesePanelToExistingPanel(panel, japaneseWord,
						inputGoal, commonListElements,
						!commonListElements.isForSingleRowOnly());
		panelCreatorToUse.focusMeaningTextfield();

		return rowData;
	}

}

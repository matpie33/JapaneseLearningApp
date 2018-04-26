package com.kanji.list.listRows;

import com.guimaker.panels.MainPanel;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listRows.japanesePanelCreatingComponents.JapaneseWordPanelCreator;
import com.kanji.list.myList.ListRowCreator;
import com.kanji.list.myList.ListRowData;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;
import java.util.Optional;

public class RowInJapaneseWordInformations
		implements ListRowCreator<JapaneseWord> {
	private JapaneseWordPanelCreator newWordsPanelCreator;
	private Optional<JapaneseWordPanelCreator> searchDialogPanelCreator;

	public RowInJapaneseWordInformations(
			JapaneseWordPanelCreator newWordsPanelCreator) {
		this.newWordsPanelCreator = newWordsPanelCreator;
		searchDialogPanelCreator = Optional.empty();
	}

	@Override
	public ListRowData createListRow(JapaneseWord japaneseWord,
			CommonListElements commonListElements, boolean forSearchPanel) {
		MainPanel panel = new MainPanel(null);
		JLabel rowNumberLabel = commonListElements.getRowNumberLabel();
		JapaneseWordPanelCreator panelCreatorToUse;
		if (commonListElements.isForSingleRowOnly()){
			panelCreatorToUse = searchDialogPanelCreator.orElse(
					newWordsPanelCreator.copy());
		}
		else{
			panelCreatorToUse = newWordsPanelCreator;
		}
		panelCreatorToUse.setRowNumberLabel(rowNumberLabel);
		panelCreatorToUse
				.setLabelsColor(commonListElements.getLabelsColor());
		ListRowData<JapaneseWord> rowData = panelCreatorToUse
				.addJapanesePanelToExistingPanel(panel, japaneseWord,
						forSearchPanel, commonListElements,
						!commonListElements.isForSingleRowOnly());
		panelCreatorToUse.focusMeaningTextfield();

		return rowData;
	}

}

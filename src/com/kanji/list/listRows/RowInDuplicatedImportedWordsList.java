package com.kanji.list.listRows;

import com.guimaker.enums.ComponentType;
import com.guimaker.enums.FillType;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.JapanesePanelDisplayMode;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listRows.japanesePanelCreatingComponents.JapaneseWordPanelCreator;
import com.kanji.list.myList.ListRowData;
import com.kanji.list.myList.ListRowCreator;
import com.kanji.list.myList.MyList;
import com.kanji.model.DuplicatedJapaneseWordInformation;
import com.kanji.utilities.CommonGuiElementsCreator;
import com.kanji.utilities.CommonListElements;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RowInDuplicatedImportedWordsList
		implements ListRowCreator<DuplicatedJapaneseWordInformation> {

	private ApplicationWindow applicationWindow;
	private DialogWindow parentDialog;
	private MyList<JapaneseWord> newJapaneseWords;

	public RowInDuplicatedImportedWordsList(ApplicationWindow applicationWindow,
			DialogWindow parentDialog, MyList<JapaneseWord> newJapaneseWords) {
		this.applicationWindow = applicationWindow;
		this.parentDialog = parentDialog;
		this.newJapaneseWords = newJapaneseWords;
	}

	@Override
	public MainPanel createListRow(DuplicatedJapaneseWordInformation data,
			CommonListElements commonListElements, boolean forSearchPanel) {
		MainPanel panel = new MainPanel(null);
		JLabel rowNumber = GuiElementsCreator.createLabel(
				new ComponentOptions().text(Prompts.ROW_NUMBER)
						.foregroundColor(Color.WHITE));
		JTextComponent rowNumberText = CommonGuiElementsCreator
				.createTextField("" + (data.getDuplicatedWordRowNumber() + 1));
		MainPanel japaneseWordInformationPanel = new MainPanel(null);
		new JapaneseWordPanelCreator(
				applicationWindow.getApplicationController(), parentDialog,
				JapanesePanelDisplayMode.EDIT)
				.addJapanesePanelToExistingPanel(japaneseWordInformationPanel,
						data.getJapaneseWord(), forSearchPanel);
		AbstractButton buttonGoToRow = createButtonGoToRow(
				data.getDuplicatedWordRowNumber());
		panel.addRow(SimpleRowBuilder
				.createRow(FillType.NONE, rowNumber, rowNumberText,
						buttonGoToRow));
		panel.addRow(SimpleRowBuilder.createRow(FillType.BOTH,
				japaneseWordInformationPanel.getPanel()));
		return panel;
	}

	private AbstractButton createButtonGoToRow(int rowNumber) {
		return GuiElementsCreator.createButtonlikeComponent(ComponentType.BUTTON,
				ButtonsNames.GO_TO_ROW, new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						newJapaneseWords.highlightRow(rowNumber);
					}
				});
	}

	@Override
	public ListRowData getRowData() {
		return null;// TODO not needed atm
	}

}

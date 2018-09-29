package com.kanji.list.listRows;

import com.guimaker.enums.ButtonType;
import com.guimaker.enums.FillType;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.options.ButtonOptions;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.InputGoal;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.listRows.japanesePanelCreatingComponents.JapaneseWordPanelCreator;
import com.kanji.list.listeners.InputValidationListener;
import com.kanji.list.myList.ListRowCreator;
import com.kanji.list.myList.ListRowData;
import com.kanji.list.myList.ListRowDataCreator;
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
	public ListRowData createListRow(DuplicatedJapaneseWordInformation data,
			CommonListElements commonListElements, InputGoal inputGoal) {
		MainPanel panel = new MainPanel(null);
		JLabel rowNumber = GuiElementsCreator.createLabel(
				new ComponentOptions().text(Prompts.ROW_NUMBER)
						.foregroundColor(Color.WHITE));
		JTextComponent rowNumberText = CommonGuiElementsCreator
				.createTextField("" + (data.getDuplicatedWordRowNumber() + 1));
		MainPanel japaneseWordInformationPanel = new MainPanel(null);
		new JapaneseWordPanelCreator(
				applicationWindow.getApplicationController(), parentDialog,
				PanelDisplayMode.EDIT)
				.addJapanesePanelToExistingPanel(japaneseWordInformationPanel,
						data.getJapaneseWord(), inputGoal, commonListElements,
						false);
		AbstractButton buttonGoToRow = createButtonGoToRow(
				data.getDuplicatedWordRowNumber());
		panel.addRow(SimpleRowBuilder
				.createRow(FillType.NONE, rowNumber, rowNumberText,
						buttonGoToRow));
		panel.addRow(SimpleRowBuilder.createRow(FillType.BOTH,
				japaneseWordInformationPanel.getPanel()));
		ListRowDataCreator<Kanji> rowDataCreator = new ListRowDataCreator<>(
				panel);
		return rowDataCreator.getListRowData();
	}

	private AbstractButton createButtonGoToRow(int rowNumber) {
		return GuiElementsCreator
				.createButtonlikeComponent(new ButtonOptions(ButtonType.BUTTON),
						new AbstractAction() {
							@Override
							public void actionPerformed(ActionEvent e) {
								newJapaneseWords.highlightRow(rowNumber);
							}
						});
	}

	@Override
	public void addValidationListener(
			InputValidationListener<DuplicatedJapaneseWordInformation> inputValidationListener) {

	}
}

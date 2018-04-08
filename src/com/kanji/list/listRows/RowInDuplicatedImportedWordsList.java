package com.kanji.list.listRows;

import com.guimaker.enums.ComponentType;
import com.guimaker.enums.FillType;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.ListPanelDisplayMode;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listRows.japanesePanelActionsCreator.JapanesePanelEditOrAddModeAction;
import com.kanji.list.listRows.japanesePanelCreator.JapanesePanelElementsMaker;
import com.kanji.list.listRows.japanesePanelCreator.JapanesePanelRowServiceAddMode;
import com.kanji.list.listRows.japanesePanelCreator.JapaneseWordPanelCreator;
import com.kanji.list.myList.ListRowMaker;
import com.kanji.list.myList.MyList;
import com.kanji.model.DuplicatedJapaneseWordInformation;
import com.kanji.utilities.CommonGuiElementsMaker;
import com.kanji.utilities.CommonListElements;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RowInDuplicatedImportedWordsList
		implements ListRowMaker<DuplicatedJapaneseWordInformation> {

	private ApplicationWindow applicationWindow;
	private DialogWindow parentDialog;
	private MyList<JapaneseWordInformation> newJapaneseWords;

	public RowInDuplicatedImportedWordsList(ApplicationWindow applicationWindow,
			DialogWindow parentDialog,
			MyList<JapaneseWordInformation> newJapaneseWords) {
		this.applicationWindow = applicationWindow;
		this.parentDialog = parentDialog;
		this.newJapaneseWords = newJapaneseWords;
	}

	@Override
	public MainPanel createListRow(DuplicatedJapaneseWordInformation data,
			CommonListElements commonListElements) {
		MainPanel panel = new MainPanel(null);
		JLabel rowNumber = GuiMaker.createLabel(
				new ComponentOptions().text(Prompts.ROW_NUMBER)
						.foregroundColor(Color.WHITE));
		JTextComponent rowNumberText = CommonGuiElementsMaker
				.createTextField("" + (data.getDuplicatedWordRowNumber() + 1));
		JapanesePanelEditOrAddModeAction actionMaker = new JapanesePanelEditOrAddModeAction(
				applicationWindow.getApplicationController(), parentDialog,
				applicationWindow.getApplicationController().getJapaneseWords(),
				ListPanelDisplayMode.VIEW_AND_EDIT);
		JapanesePanelElementsMaker elementsMaker = new JapanesePanelElementsMaker(
				actionMaker);
		MainPanel japaneseWordInformationPanel = new MainPanel(null);
		new JapaneseWordPanelCreator(
				applicationWindow.getApplicationController(), actionMaker,
				elementsMaker)
				.addJapanesePanelToExistingPanel(japaneseWordInformationPanel,
						data.getJapaneseWordInformation(),
						new JapanesePanelRowServiceAddMode(elementsMaker,
								data.getJapaneseWordInformation()),
						parentDialog);
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
		return GuiMaker.createButtonlikeComponent(ComponentType.BUTTON,
				ButtonsNames.GO_TO_ROW, new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						newJapaneseWords.highlightRow(rowNumber);
					}
				});
	}

}

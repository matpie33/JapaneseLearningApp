package com.kanji.panelsAndControllers.panels;

import com.guimaker.enums.FillType;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.ListPanelViewMode;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listRows.RowInJapaneseWordInformations;
import com.kanji.list.listRows.panelCreators.JapaneseWordPanelCreator;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.InsertJapaneseWordController;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class InsertJapaneseWordPanel extends AbstractPanelWithHotkeysInfo {

	private InsertJapaneseWordController controller;
	private JapaneseWordPanelCreator japaneseWordPanelCreator;
	private ApplicationWindow applicationWindow;

	public InsertJapaneseWordPanel(RowInJapaneseWordInformations row,
			MyList<JapaneseWordInformation> list,
			ApplicationWindow applicationWindow) {
		controller = new InsertJapaneseWordController(row, list,
				applicationWindow.getApplicationController());
		this.applicationWindow = applicationWindow;

	}

	@Override
	public void createElements() {

		japaneseWordPanelCreator = new JapaneseWordPanelCreator(
				applicationWindow, true, parentDialog);
		controller.setParentDialog(parentDialog);

		MainPanel addWordPanel = japaneseWordPanelCreator
				.createPanelInGivenMode(JapaneseWordInformation.getInitializer()
						.initializeElement(), ListPanelViewMode.ADD, null);

		AbstractButton cancel = createButtonClose();
		AbstractButton approve = createButtonValidate(ButtonsNames.ADD_WORD);

		mainPanel.addRows(SimpleRowBuilder
				.createRow(FillType.BOTH, addWordPanel.getPanel())
				.useAllExtraVerticalSpace());
		setNavigationButtons(cancel, approve);

	}

	private AbstractButton createButtonValidate(String text) {
		return createButtonWithHotkey(KeyEvent.VK_ENTER, controller
						.createActionValidateAndAddWord(japaneseWordPanelCreator
										.getKanaToKanjiWritingsTextComponents(),
								japaneseWordPanelCreator
										.getPropertyManagersOfTextFields(),
								japaneseWordPanelCreator.getPartOfSpeechCombobox()),
				text, HotkeysDescriptions.ADD_WORD);
	}

}

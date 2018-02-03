package com.kanji.panelsAndControllers.panels;

import com.guimaker.enums.FillType;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.ListPanelViewMode;
import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.list.listElementPropertyManagers.*;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.listRows.RowInJapaneseWordInformations;
import com.kanji.list.listRows.panelCreators.JapaneseWordPanelCreator;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.panelsAndControllers.controllers.InsertJapaneseWordController;
import com.kanji.panelsAndControllers.controllers.InsertWordController;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class InsertJapaneseWordPanel extends AbstractPanelWithHotkeysInfo {

	private InsertJapaneseWordController controller;
	private JapaneseWordPanelCreator japaneseWordPanelCreator;

	public InsertJapaneseWordPanel(RowInJapaneseWordInformations row,
			MyList<JapaneseWordInformation> list, ApplicationController applicationController) {
		controller = new InsertJapaneseWordController(row, list, applicationController);
		japaneseWordPanelCreator = new JapaneseWordPanelCreator();
	}

	@Override
	public void createElements() {

		controller.setParentDialog(parentDialog);

		MainPanel addWordPanel = japaneseWordPanelCreator.createPanelForEditing(
				"", new HashMap<>(), PartOfSpeech.NOUN, ListPanelViewMode.EDIT,
				null
		);

		AbstractButton cancel = createButtonClose();
		AbstractButton approve = createButtonValidate(ButtonsNames.ADD_WORD);

		mainPanel.addRows(
				SimpleRowBuilder.createRow(FillType.BOTH, addWordPanel.getPanel()).useAllExtraVerticalSpace());
		setNavigationButtons(cancel, approve);

	}

	private AbstractButton createButtonValidate(String text) {
		return createButtonWithHotkey(KeyEvent.VK_ENTER,
				controller.createActionValidateAndAddWord(
						japaneseWordPanelCreator.getKanaToKanjiWritingsTextComponents(),
						japaneseWordPanelCreator.getPropertyManagersOfTextFields(),
						japaneseWordPanelCreator.getPartOfSpeechCombobox()),
				text, HotkeysDescriptions.ADD_WORD);
	}




}

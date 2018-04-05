package com.kanji.panelsAndControllers.panels;

import com.guimaker.enums.FillType;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listRows.RowInJapaneseWordInformations;
import com.kanji.list.listRows.japanesePanelCreator.JapanesePanelServiceAddMode;
import com.kanji.list.listRows.japanesePanelCreator.JapaneseWordPanelCreator;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.InsertJapaneseWordController;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;

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
				applicationWindow.getApplicationController());
		controller.setParentDialog(parentDialog);

		MainPanel addWordPanel = japaneseWordPanelCreator.createPanel(
				JapaneseWordInformation.getInitializer().initializeElement(),
				new JapanesePanelServiceAddMode(), parentDialog);

		AbstractButton cancel = createButtonClose();
		AbstractButton approve = createButtonValidate(ButtonsNames.ADD_WORD);

		mainPanel.addRows(SimpleRowBuilder
				.createRow(FillType.BOTH, addWordPanel.getPanel())
				.useAllExtraVerticalSpace());
		setNavigationButtons(cancel, approve);

	}

	private AbstractButton createButtonValidate(String text) {
		//		return createButtonWithHotkey(KeyEvent.VK_ENTER, controller
		//						.createActionValidateAndAddWord(HotkeysDescriptions.ADD_WORD);
		return new JButton("hi");
	}

}

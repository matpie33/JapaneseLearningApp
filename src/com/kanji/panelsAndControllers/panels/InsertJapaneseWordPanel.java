package com.kanji.panelsAndControllers.panels;

import com.guimaker.enums.FillType;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.JapanesePanelDisplayMode;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listRows.RowInJapaneseWordInformations;
import com.kanji.list.listRows.japanesePanelActionsCreator.JapanesePanelEditOrAddModeAction;
import com.kanji.list.listRows.japanesePanelCreator.JapaneseWordPanelCreator;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.InsertJapaneseWordController;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import java.awt.*;

public class InsertJapaneseWordPanel extends AbstractPanelWithHotkeysInfo {

	private InsertJapaneseWordController controller;
	private JapaneseWordPanelCreator japaneseWordPanelCreator;
	private ApplicationWindow applicationWindow;
	private JapaneseWordInformation japaneseWordInformation;

	public InsertJapaneseWordPanel(RowInJapaneseWordInformations row,
			MyList<JapaneseWordInformation> list,
			ApplicationWindow applicationWindow) {
		japaneseWordInformation = JapaneseWordInformation.getInitializer()
				.initializeElement();
		controller = new InsertJapaneseWordController(list,
				applicationWindow.getApplicationController(),
				japaneseWordInformation);
		this.applicationWindow = applicationWindow;

	}

	@Override
	public void createElements() {

		JapanesePanelEditOrAddModeAction actionMaker = new JapanesePanelEditOrAddModeAction(
				applicationWindow.getApplicationController(), parentDialog,
				applicationWindow.getApplicationController().getJapaneseWords(),
				JapanesePanelDisplayMode.EDIT);
		japaneseWordPanelCreator = new JapaneseWordPanelCreator(
				applicationWindow.getApplicationController(),parentDialog,
				JapanesePanelDisplayMode.EDIT);
		japaneseWordPanelCreator.setLabelsColor(Color.BLACK);
		controller.setParentDialog(parentDialog);

		MainPanel addWordPanel = new MainPanel(null);
		japaneseWordPanelCreator.addJapanesePanelToExistingPanel(addWordPanel,
				japaneseWordInformation);

		AbstractButton cancel = createButtonClose();
		AbstractButton approve = createButtonValidate(ButtonsNames.ADD_WORD);

		mainPanel.addRows(SimpleRowBuilder
				.createRow(FillType.BOTH, addWordPanel.getPanel())
				.useAllExtraVerticalSpace());
		setNavigationButtons(cancel, approve);

	}

	private AbstractButton createButtonValidate(String text) {
		//				return createButtonWithHotkey(KeyEvent.VK_ENTER,
		//						controller.createActionValidateAndAddWord(
		//								HotkeysDescriptions.ADD_WORD));
		return new JButton("hi");
	}

}

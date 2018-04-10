package com.kanji.panelsAndControllers.panels;

import com.guimaker.enums.FillType;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.JapanesePanelDisplayMode;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listRows.RowInJapaneseWordInformations;
import com.kanji.list.listRows.japanesePanelCreator.JapaneseWordPanelCreator;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.InsertJapaneseWordController;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class InsertJapaneseWordPanel extends AbstractPanelWithHotkeysInfo {

	private InsertJapaneseWordController controller;
	private JapaneseWordPanelCreator japaneseWordPanelCreator;
	private ApplicationWindow applicationWindow;
	private JapaneseWordInformation japaneseWordInformation;

	//TODO now the insert word panel/controller are ready to be used with generics -
	// for both kanji and japanese word inserting

	public InsertJapaneseWordPanel(RowInJapaneseWordInformations row,
			MyList<JapaneseWordInformation> list,
			ApplicationWindow applicationWindow) {
		controller = new InsertJapaneseWordController(list,
				applicationWindow.getApplicationController(), this);
		this.applicationWindow = applicationWindow;

	}

	@Override
	public void setParentDialog(DialogWindow parentDialog) {
		super.setParentDialog(parentDialog);
		controller.setParentDialog(parentDialog);
		initializeGuiOneTimeOnlyElements();
	}

	private void initializeGuiOneTimeOnlyElements() {
		japaneseWordPanelCreator = new JapaneseWordPanelCreator(
				applicationWindow.getApplicationController(), parentDialog,
				JapanesePanelDisplayMode.EDIT);
		japaneseWordPanelCreator.setLabelsColor(Color.BLACK);
		AbstractButton cancel = createButtonClose();
		AbstractButton approve = createButtonValidate();
		setNavigationButtons(cancel, approve);
	}

	@Override
	public void createElements() {
		initializeWord();
		initializeJapaneseWordPanel();
	}

	private void initializeWord() {
		japaneseWordInformation = JapaneseWordInformation.getInitializer()
				.initializeElement();
	}

	private void initializeJapaneseWordPanel() {
		MainPanel addWordPanel = new MainPanel(null);
		japaneseWordPanelCreator.addJapanesePanelToExistingPanel(addWordPanel,
				japaneseWordInformation);
		japaneseWordPanelCreator.focusMeaningTextfield();
		mainPanel.addRow(SimpleRowBuilder
				.createRow(FillType.BOTH, addWordPanel.getPanel())
				.useAllExtraVerticalSpace());
	}

	public void reinitializePanel() {
		mainPanel.clear();
		createPanel();
		mainPanel.updateView();
	}

	private AbstractButton createButtonValidate() {
		return createButtonWithHotkey(KeyEvent.VK_ENTER,
				controller.createActionValidateAndAddWord(), ButtonsNames.ADD,
				HotkeysDescriptions.ADD_WORD);
	}

	public JapaneseWordInformation getWord() {
		return japaneseWordInformation;
	}
}

package com.kanji.panelsAndControllers.panels;

import com.guimaker.enums.FillType;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.InsertWordController;
import com.kanji.utilities.CommonListElements;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class InsertWordPanel<Word extends ListElement>
		extends AbstractPanelWithHotkeysInfo {

	private InsertWordController<Word> controller;
	private Word word;
	private MyList<Word> wordsList;
	private Color labelsColor;

	public InsertWordPanel(MyList<Word> list,
			ApplicationWindow applicationWindow) {
		wordsList = list;
		controller = new InsertWordController(list,
				applicationWindow.getApplicationController(), this);

	}

	@Override
	public void setParentDialog(DialogWindow parentDialog) {
		super.setParentDialog(parentDialog);
		controller.setParentDialog(parentDialog);
		initializeGuiOneTimeOnlyElements();
	}

	private void initializeGuiOneTimeOnlyElements() {
		AbstractButton cancel = createButtonClose();
		AbstractButton approve = createButtonValidate();
		setNavigationButtons(cancel, approve);
	}

	@Override
	public void createElements() {
		initializeWord();
		initializeAddWordPanel();
	}

	private void initializeWord() {
		word = wordsList.getWordInitializer().initializeElement();
	}

	private void initializeAddWordPanel() {
		labelsColor = Color.WHITE;
		MainPanel addWordPanel = wordsList.getListRowCreator()
				.createListRow(word,
						CommonListElements.forSingleRowOnly(labelsColor), false)
				.getRowPanel();
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

	public Word getWord() {
		return word;
	}
}

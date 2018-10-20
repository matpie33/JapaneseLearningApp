package com.kanji.panelsAndControllers.panels;

import com.guimaker.enums.FillType;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.strings.ButtonsNames;
import com.guimaker.strings.HotkeysDescriptions;
import com.kanji.application.ApplicationChangesManager;
import com.guimaker.list.ListElement;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.InsertWordController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class InsertWordPanel<Word extends ListElement>
		extends AbstractPanelWithHotkeysInfo {

	private static final String UNIQUE_NAME = "Insert word panel";
	private InsertWordController<Word> controller;
	private Color labelsColor = Color.WHITE;

	public InsertWordPanel(MyList<Word> list,
			ApplicationChangesManager applicationChangesManager) {
		controller = new InsertWordController<>(list, applicationChangesManager,
				this);
		initializeOneTimeOnlyElements();
	}

	public Color getLabelsColor() {
		return labelsColor;
	}

	private void initializeOneTimeOnlyElements() {
		AbstractButton cancel = createButtonClose();
		AbstractButton approve = createButtonValidateAndAddWord();
		setNavigationButtons(cancel, approve);
	}

	@Override
	public void createElements() {
		mainPanel.addRow(SimpleRowBuilder.createRow(FillType.BOTH,
				controller.createListRowPanel().getPanel()).fillAllVertically()
				.fillHorizontallyEqually());
	}

	@Override
	public String getUniqueName() {
		return UNIQUE_NAME;
	}

	public void reinitializePanel() {
		clear();
		createPanel();
		mainPanel.updateView();
	}

	private AbstractButton createButtonValidateAndAddWord() {
		//if combobox is selected, the enter key will not work, because it has
		//another function there - choose the currently selected item
		return createButtonWithHotkey(KeyEvent.VK_ENTER,
				controller.createActionValidateAndAddFocusedElement(),
				ButtonsNames.ADD, HotkeysDescriptions.ADD_WORD);
	}

}

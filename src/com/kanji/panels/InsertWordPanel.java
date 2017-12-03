package com.kanji.panels;

import com.guimaker.enums.FillType;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.listElements.KanjiInformation;
import com.kanji.strings.ButtonsNames;
import com.kanji.strings.HotkeysDescriptions;
import com.kanji.strings.Prompts;
import com.kanji.controllers.ApplicationController;
import com.kanji.controllers.InsertWordController;
import com.kanji.myList.MyList;
import com.kanji.utilities.CommonGuiElementsMaker;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.KeyEvent;

public class InsertWordPanel extends AbstractPanelWithHotkeysInfo {

	private JTextComponent insertWordTextComponent;
	private JTextComponent insertNumberTextComponent;
	private InsertWordController controller;

	public InsertWordPanel(MyList<KanjiInformation> list,
			ApplicationController applicationController) {
		controller = new InsertWordController(list, applicationController);
	}

	@Override
	public void createElements() {

		controller.setParentDialog(parentDialog);
		JLabel addWordPrompt = new JLabel(Prompts.ADD_DIALOG);
		insertWordTextComponent = CommonGuiElementsMaker.createKanjiWordInput("");

		JLabel addNumberPrompt = new JLabel(Prompts.ADD_NUMBER);
		insertNumberTextComponent = CommonGuiElementsMaker.createKanjiIdInput();

		AbstractButton cancel = createButtonClose();
		AbstractButton approve = createButtonValidate(ButtonsNames.ADD_WORD);

		MainPanel addWordPanel = new MainPanel(null);

		addWordPanel.addRows(
				SimpleRowBuilder.createRow(FillType.BOTH, addWordPrompt, insertWordTextComponent)
				.fillHorizontallySomeElements(insertWordTextComponent)
				.fillVertically(insertWordTextComponent)
				.nextRow(FillType.NONE, addNumberPrompt, insertNumberTextComponent));

		mainPanel.addRows(
				SimpleRowBuilder.createRow(FillType.BOTH, addWordPanel.getPanel()).useAllExtraVerticalSpace());
		setNavigationButtons(cancel, approve);

	}

	private AbstractButton createButtonValidate(String text) {
		return createButtonWithHotkey(KeyEvent.VK_ENTER,
				controller.createActionValidateAndAddWord(insertNumberTextComponent,
								insertWordTextComponent), text, HotkeysDescriptions.ADD_WORD);
	}

}

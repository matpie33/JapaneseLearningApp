package com.kanji.panels;

import com.guimaker.enums.ComponentType;
import com.guimaker.enums.FillType;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRow;
import com.kanji.Row.KanjiInformation;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.Prompts;
import com.kanji.controllers.ApplicationController;
import com.kanji.controllers.InsertWordController;
import com.kanji.myList.MyList;
import com.kanji.utilities.CommonGuiElementsMaker;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class InsertWordPanel extends AbstractPanelWithHotkeysInfo {

	private JTextArea insertWordTextField;
	private JTextArea insertNumberTextField;
	private InsertWordController controller;

	public InsertWordPanel(MyList<KanjiInformation> list,
			ApplicationController applicationController) {
		controller = new InsertWordController(list, applicationController);
	}

	@Override
	void createElements() {

		controller.setParentDialog(parentDialog);
		JLabel addWordPrompt = new JLabel(Prompts.ADD_DIALOG);
		insertWordTextField = CommonGuiElementsMaker.createKanjiWordInput("");

		JLabel addNumberPrompt = new JLabel(Prompts.ADD_NUMBER);
		insertNumberTextField = CommonGuiElementsMaker.createKanjiIdInput();

		AbstractButton cancel = createButtonClose();
		AbstractButton approve = createButtonValidate(ButtonsNames.ADD_WORD);

		MainPanel addWordPanel = new MainPanel(null);

		addWordPanel.addRows(new SimpleRow(FillType.BOTH, addWordPrompt, insertWordTextField)
				.fillHorizontallySomeElements(insertWordTextField)
				.fillVertically(insertWordTextField)
				.nextRow(FillType.NONE, addNumberPrompt, insertNumberTextField));

		mainPanel.addRow(
				new SimpleRow(FillType.BOTH, addWordPanel.getPanel()).useAllExtraVerticalSpace());
		setNavigationButtons(cancel, approve);

	}

	private AbstractButton createButtonValidate(String text) {
		AbstractAction action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.validateAndAddWordIfValid(insertNumberTextField, insertWordTextField);
			}
		};
		return GuiMaker.createButtonlikeComponent(ComponentType.BUTTON, text, action,
				KeyEvent.VK_ENTER);
	}

}

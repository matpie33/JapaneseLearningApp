package com.kanji.panels;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.text.AbstractDocument;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.ComponentType;
import com.guimaker.enums.FillType;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRow;
import com.guimaker.utilities.CommonActionsMaker;
import com.kanji.Row.KanjiInformation;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.NumberValues;
import com.kanji.constants.Prompts;
import com.kanji.controllers.ApplicationController;
import com.kanji.controllers.InsertWordController;
import com.kanji.myList.MyList;
import com.kanji.utilities.CommonGuiElementsMaker;
import com.kanji.utilities.LimitDocumentFilter;

public class InsertWordPanel extends AbstractPanelWithHotkeysInfo {

	private JTextArea insertWordTextField;
	private JTextArea insertNumberTextField;
	private InsertWordController controller;

	public InsertWordPanel(MyList<KanjiInformation> list,
			ApplicationController applicationController) {
		super(true);
		controller = new InsertWordController(list, applicationController);
	}

	@Override
	void createElements() {

		controller.setParentDialog(parentDialog);
		JLabel addWordPrompt = new JLabel(Prompts.ADD_DIALOG);
		insertWordTextField = CommonGuiElementsMaker.createKanjiWordInput("");

		JLabel addNumberPrompt = new JLabel(Prompts.ADD_NUMBER);
		insertNumberTextField = CommonGuiElementsMaker.createKanjiIdInput();

		AbstractButton cancel = GuiMaker.createButtonlikeComponent(ComponentType.BUTTON,
				ButtonsNames.CANCEL,
				CommonActionsMaker.createDisposeAction(parentDialog.getContainer()));

		AbstractButton approve = createButtonValidate(ButtonsNames.APPROVE);

		MainPanel addWordPanel = new MainPanel(null);

		addWordPanel.addRows(new SimpleRow(FillType.HORIZONTAL, addWordPrompt, insertWordTextField)
				.fillHorizontallySomeElements(insertWordTextField)
				.nextRow(FillType.NONE, addNumberPrompt, insertNumberTextField));

		mainPanel.addRow(new SimpleRow(FillType.BOTH, addWordPanel.getPanel()));
		addHotkeysPanelHere();
		mainPanel.addRow(new SimpleRow(FillType.NONE, Anchor.EAST, cancel, approve));

	}

	private void limitCharactersAccordingToInteger(JTextArea textField) {
		((AbstractDocument) textField.getDocument()).setDocumentFilter(
				new LimitDocumentFilter(NumberValues.INTEGER_MAX_VALUE_DIGITS_AMOUNT));
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

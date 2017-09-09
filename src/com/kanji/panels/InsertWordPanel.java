package com.kanji.panels;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.ComponentType;
import com.guimaker.enums.FillType;
import com.guimaker.panels.GuiMaker;
import com.guimaker.row.SimpleRow;
import com.guimaker.utilities.CommonActionsMaker;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.NumberValues;
import com.kanji.constants.Prompts;
import com.kanji.controllers.ApplicationController;
import com.kanji.controllers.InsertWordController;
import com.kanji.myList.MyList;
import com.kanji.utilities.LimitDocumentFilter;

public class InsertWordPanel extends AbstractPanelWithHotkeysInfo {

	private JTextField insertWordTextField;
	private JTextField insertNumberTextField;
	private InsertWordController controller;

	public InsertWordPanel(MyList list, ApplicationController applicationController) {
		super(true);
		controller = new InsertWordController(list, applicationController);
	}

	@Override
	void createElements() {

		controller.setParentDialog(parentDialog);
		JLabel addWordPrompt = new JLabel(Prompts.ADD_DIALOG);
		insertWordTextField = new JTextField(20);

		JLabel addNumberPrompt = new JLabel(Prompts.ADD_NUMBER);
		insertNumberTextField = new JTextField(20);
		limitCharactersAccordingToInteger(insertNumberTextField);

		AbstractButton cancel = GuiMaker.createButtonlikeComponent(ComponentType.BUTTON,
				ButtonsNames.CANCEL,
				CommonActionsMaker.createDisposeAction(parentDialog.getContainer()));

		AbstractButton approve = createButtonValidate(ButtonsNames.APPROVE);

		mainPanel.addRows(new SimpleRow(FillType.HORIZONTAL, addWordPrompt, insertWordTextField)
				.fillHorizontallySomeElements(insertWordTextField)
				.nextRow(addNumberPrompt, insertNumberTextField)
				.fillHorizontallySomeElements(insertNumberTextField));
		addHotkeysPanelHere();
		mainPanel.addRow(new SimpleRow(FillType.NONE, Anchor.EAST, cancel, approve));

	}

	private void limitCharactersAccordingToInteger(JTextField textField) {
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

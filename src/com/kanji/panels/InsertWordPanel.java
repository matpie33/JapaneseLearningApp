package com.kanji.panels;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;

import com.guimaker.row.RowMaker;
import com.kanji.actions.CommonActionsMaker;
import com.kanji.actions.GuiElementsMaker;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.NumberValues;
import com.kanji.constants.Prompts;
import com.kanji.controllers.InsertWordController;
import com.kanji.myList.MyList;
import com.kanji.utilities.LimitDocumentFilter;
import com.kanji.windows.DialogWindow;

public class InsertWordPanel extends AbstractPanelWithHotkeysInfo {

	private DialogWindow parentDialog;
	private JTextField insertWordTextField;
	private JTextField insertNumberTextField;
	private InsertWordController controller;

	public InsertWordPanel(MyList list) {
		super(true);
		controller = new InsertWordController(list);
	}

	@Override
	public void setParentDialog(DialogWindow parent) {
		parentDialog = parent;
		controller.setParentDialog(parent);
	}

	@Override
	void createElements() {

		JLabel addWordPrompt = new JLabel(Prompts.wordAddDialogPrompt);
		insertWordTextField = new JTextField(20);

		JLabel addNumberPrompt = new JLabel(Prompts.wordAddNumberPrompt);
		insertNumberTextField = new JTextField(20);
		limitCharactersAccordingToInteger(insertNumberTextField);

		JButton cancel = GuiElementsMaker.createButton(ButtonsNames.buttonCancelText,
				CommonActionsMaker.createDisposeAction(parentDialog));

		JButton approve = createButtonValidate(ButtonsNames.buttonApproveText);

		mainPanel.addRow(RowMaker.createHorizontallyFilledRow(addWordPrompt, insertWordTextField)
				.fillHorizontallySomeElements(insertWordTextField));
		mainPanel
				.addRow(RowMaker.createHorizontallyFilledRow(addNumberPrompt, insertNumberTextField)
						.fillHorizontallySomeElements(insertNumberTextField));
		addHotkeysPanelHere();
		mainPanel.addRow(RowMaker.createUnfilledRow(GridBagConstraints.EAST, cancel, approve));

	}

	private void limitCharactersAccordingToInteger(JTextField textField) {
		((AbstractDocument) textField.getDocument()).setDocumentFilter(
				new LimitDocumentFilter(NumberValues.INTEGER_MAX_VALUE_DIGITS_AMOUNT));
	}

	private JButton createButtonValidate(String text) {
		AbstractAction action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean validInput = controller.validateAndAddWordIfValid(
						insertNumberTextField.getText(), insertWordTextField.getText());
				if (validInput) {
					insertWordTextField.selectAll();
					insertWordTextField.requestFocusInWindow();
				}
			}
		};
		return GuiElementsMaker.createButton(text, action, KeyEvent.VK_ENTER);
	}

}

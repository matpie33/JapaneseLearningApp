package com.kanji.panels;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.MainPanel;
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

public class InsertWordPanel implements PanelCreator {

	private MainPanel main;
	private DialogWindow parentDialog;
	private JTextField insertWordTextField;
	private JTextField insertNumberTextField;
	private InsertWordController controller;

	public InsertWordPanel(MyList list) {
		main = new MainPanel(BasicColors.OCEAN_BLUE);
		controller = new InsertWordController(list);
	}

	@Override
	public void setParentDialog(DialogWindow parent) {
		parentDialog = parent;
		controller.setParentDialog(parent);
	}

	@Override
	public JPanel createPanel() {

		JLabel addWordPrompt = new JLabel(Prompts.wordAddDialogPrompt);
		insertWordTextField = new JTextField(20);

		JLabel addNumberPrompt = new JLabel(Prompts.wordAddNumberPrompt);
		insertNumberTextField = new JTextField(20);
		limitCharactersAccordingToInteger(insertNumberTextField);

		JButton cancel = CommonActionsMaker.createButtonDispose(ButtonsNames.buttonCancelText,
				java.awt.event.KeyEvent.VK_ESCAPE, parentDialog);
		JButton approve = createButtonValidate(ButtonsNames.buttonApproveText);

		main.addRow(RowMaker.createHorizontallyFilledRow(addWordPrompt, insertWordTextField)
				.fillHorizontallySomeElements(insertWordTextField));
		main.addRow(RowMaker.createHorizontallyFilledRow(addNumberPrompt, insertNumberTextField)
				.fillHorizontallySomeElements(insertNumberTextField));
		main.addRow(RowMaker.createHorizontallyFilledRow(cancel, approve));

		return main.getPanel();
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

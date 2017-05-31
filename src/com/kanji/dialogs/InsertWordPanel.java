package com.kanji.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.AbstractDocument;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
import com.kanji.Row.KanjiWords;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.ExceptionsMessages;
import com.kanji.constants.NumberValues;
import com.kanji.constants.Prompts;
import com.kanji.myList.MyList;
import com.kanji.window.LimitDocumentFilter;

public class InsertWordPanel {

	private MainPanel main;
	private MyDialog parentDialog;
	private MyList list;
	private JTextField insertWordTextField;
	private JTextField insertNumberTextField;

	public InsertWordPanel(JPanel panel, MyDialog parent) {
		main = new MainPanel(BasicColors.OCEAN_BLUE);
		parentDialog = parent;
	}

	public JPanel createPanel(MyList list) {
		this.list = list;
		JLabel addWordPrompt = new JLabel(Prompts.wordAddDialogPrompt);
		insertWordTextField = new JTextField(20);

		JLabel addNumberPrompt = new JLabel(Prompts.wordAddNumberPrompt);
		insertNumberTextField = new JTextField(20);
		limitCharactersAccordingToInteger(insertNumberTextField);

		JButton cancel = parentDialog.createButtonDispose(ButtonsNames.buttonCancelText,
				javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
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
		JButton button = new JButton(text);
		AbstractAction action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String numberInput = insertNumberTextField.getText();

				String wordInput = insertWordTextField.getText();
				if (isNumberValid(numberInput)) {

					int number = Integer.parseInt(numberInput);
					if (checkIfInputIsValid(wordInput, number)) {
						System.out.println("adding: ");
						addWordToList(wordInput, number);
						parentDialog.save();
						insertWordTextField.selectAll();
						insertWordTextField.requestFocusInWindow();

					}

				}

			}
		};
		button.addActionListener(action);
		button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "save");
		button.getActionMap().put("save", action);
		return button;
	}

	private boolean isNumberValid(String number) {
		boolean valid = number.matches("\\d+");

		if (!valid)
			parentDialog.showErrorDialogInNewWindow(ExceptionsMessages.numberFormatException);
		return valid;
	}

	private boolean checkIfInputIsValid(String word, int number) {
		return (isWordIdUndefinedYet(number) && isWordUndefinedYet(word));
	}

	private boolean isWordIdUndefinedYet(int number) {
		boolean defined = ((KanjiWords) list.getWords()).isIdDefined(number);
		if (defined)
			parentDialog.showErrorDialogInNewWindow(ExceptionsMessages.idAlreadyDefinedException);
		return !defined;
	}

	private boolean isWordUndefinedYet(String word) {
		boolean defined = ((KanjiWords) list.getWords()).isWordDefined(word);
		if (defined)
			parentDialog.showErrorDialogInNewWindow(ExceptionsMessages.wordAlreadyDefinedException);
		return !defined;
	}

	private void addWordToList(String word, int number) {

		((KanjiWords) list.getWords()).addNewRow(word, number);
		list.scrollToBottom();
	}

}

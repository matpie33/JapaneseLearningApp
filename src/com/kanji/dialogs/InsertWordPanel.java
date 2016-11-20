package com.kanji.dialogs;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;

import com.kanji.Row.KanjiWords;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.ExceptionsMessages;
import com.kanji.constants.NumberValues;
import com.kanji.constants.Prompts;
import com.kanji.myList.MyList;
import com.kanji.window.LimitDocumentFilter;

public class InsertWordPanel {

	private JPanel mainPanel;
	private GridBagConstraints layoutConstraints;
	private MyDialog parentDialog;
	private MyList list;
	private JTextField insertWord;
	private JTextField insertNumber;

	public InsertWordPanel(JPanel panel, MyDialog parent) {
		mainPanel = panel;
		parentDialog = parent;
		layoutConstraints = new GridBagConstraints();
	}

	public void setLayoutConstraints(GridBagConstraints c) {
		layoutConstraints = c;
	}

	public JPanel createPanel(MyList list) {
		this.list = list;
		int level = 0;
		insertWord = addPromptAndTextField(level, Prompts.wordAddDialogPrompt);

		level++;
		insertNumber = addPromptAndTextField(level, Prompts.wordAddNumberPrompt);
		limitCharactersAccordingToInteger(insertNumber);

		level++;
		JButton cancel = parentDialog.createButtonDispose(ButtonsNames.buttonCancelText,
				javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
		JButton approve = createButtonValidate(ButtonsNames.buttonApproveText);
		addButtonsAtLevel(level, new JButton[] { cancel, approve });
		return mainPanel;
	}

	private JTextField addPromptAndTextField(int level, String promptMessage) {

		JLabel prompt = new JLabel(promptMessage);
		JTextField insertWord = new JTextField(20);

		JPanel panel = new JPanel();
		panel.add(prompt);
		panel.add(insertWord);
		layoutConstraints.gridy = level;
		mainPanel.add(panel, layoutConstraints);

		return insertWord;
	}

	private void limitCharactersAccordingToInteger(JTextField textField) {
		((AbstractDocument) textField.getDocument())
				.setDocumentFilter(new LimitDocumentFilter(NumberValues.INTEGER_MAX_VALUE_DIGITS_AMOUNT));
	}

	private JButton createButtonValidate(String text) {
		JButton button = new JButton(text);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String numberInput = insertNumber.getText();

				String wordInput = insertWord.getText();
				if (isNumberValid(numberInput)) {

					int number = Integer.parseInt(numberInput);
					if (checkIfInputIsValid(wordInput, number)) {
						System.out.println("adding: ");
						addWordToList(wordInput, number);
						parentDialog.save();
					}

				}

			}
		});
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

	private void addButtonsAtLevel(int level, JComponent[] buttons) {
		JPanel panel = new JPanel();
		for (JComponent button : buttons)
			panel.add(button);

		layoutConstraints.gridy = level;
		mainPanel.add(panel, layoutConstraints);
	}

}

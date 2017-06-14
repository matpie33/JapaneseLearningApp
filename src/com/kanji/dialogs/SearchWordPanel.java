package com.kanji.dialogs;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
import com.kanji.actions.CommonActionsMaker;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.ExceptionsMessages;
import com.kanji.constants.NumberValues;
import com.kanji.constants.Options;
import com.kanji.constants.Prompts;
import com.kanji.myList.MyList;
import com.kanji.myList.SearchOptions;

public class SearchWordPanel {

	private MainPanel main;
	private JTextField textField;
	private DialogWindow parentDialog;
	private JRadioButton fullWordsSearchOption;
	private JRadioButton perfectMatchSearchOption;
	private SearchOptions options;
	private MyList list;

	public SearchWordPanel(DialogWindow parent) {
		main = new MainPanel(BasicColors.OCEAN_BLUE, true);
		parentDialog = parent;
		options = new SearchOptions();
	}

	public JPanel createPanel(MyList list) {
		this.list = list;
		int level = 0;
		JLabel prompt = new JLabel(Prompts.wordSearchDialogPrompt);
		textField = addPromptAndTextFieldAndReturnTextField(level, Prompts.wordSearchDialogPrompt);
		textField = new JTextField(20);
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					tryToFindNextOccurence(NumberValues.FORWARD_DIRECTION);
				}
			}
		});

		JRadioButton defaultSearchOption = createRadioButton(level,
				Options.wordSearchDefaultOption);
		defaultSearchOption.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				options.setDefaultOption();
			}
		});

		fullWordsSearchOption = createRadioButton(level, Options.wordSearchOnlyFullWordsOption);
		fullWordsSearchOption.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				options.enableMatchByWordOnly();
			}
		});

		perfectMatchSearchOption = createRadioButton(level, Options.wordSearchPerfectMatchOption);
		perfectMatchSearchOption.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				options.enableMatchByExpressionOnly();
			}
		});

		addRadioButtonsToGroup(new JRadioButton[] { defaultSearchOption, fullWordsSearchOption,
				perfectMatchSearchOption });

		defaultSearchOption.setSelected(true);

		JButton previous = createButtonPrevious(ButtonsNames.buttonPreviousText);
		JButton next = createButtonNext(ButtonsNames.buttonNextText);
		JButton cancel = CommonActionsMaker.createButtonDispose(ButtonsNames.buttonCancelText,
				java.awt.event.KeyEvent.VK_ESCAPE, parentDialog);

		main.addRow(RowMaker.createHorizontallyFilledRow(prompt, textField)
				.fillHorizontallySomeElements(textField));
		main.addRow(RowMaker.createHorizontallyFilledRow(defaultSearchOption));
		main.addRow(RowMaker.createHorizontallyFilledRow(fullWordsSearchOption));
		main.addRow(RowMaker.createHorizontallyFilledRow(perfectMatchSearchOption));
		main.addRow( // TODO fix in gui maker: if putting rows as highest as
						// possible, then west
						// should be as highest as possible, but now I need to
						// use northwest
				RowMaker.createUnfilledRow(GridBagConstraints.NORTHWEST, previous, next, cancel));
		return main.getPanel();
	}

	private JTextField addPromptAndTextFieldAndReturnTextField(int level, String promptMessage) {

		JLabel prompt = new JLabel(promptMessage);
		JTextField insertWord = new JTextField(20);
		insertWord.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					tryToFindNextOccurence(NumberValues.FORWARD_DIRECTION);
				}
			}
		});

		JPanel panel = new JPanel();
		panel.add(prompt);
		panel.add(insertWord);

		return insertWord;
	}

	private JRadioButton createRadioButton(int level, String text) {
		JRadioButton radioButton = new JRadioButton(text);
		return radioButton;
	}

	private void addRadioButtonsToGroup(JRadioButton[] buttons) {
		ButtonGroup group = new ButtonGroup();
		for (JRadioButton button : buttons)
			group.add(button);
	}

	private JButton createButtonPrevious(String text) {
		JButton button = new JButton(text);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tryToFindNextOccurence(NumberValues.BACKWARD_DIRECTION);
			}
		});

		return button;
	}

	private JButton createButtonNext(String text) {
		JButton button = new JButton(text);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tryToFindNextOccurence(NumberValues.FORWARD_DIRECTION);
			}
		});
		return button;
	}

	private void tryToFindNextOccurence(int direction) {
		try {
			boolean found = list.findAndHighlightNextOccurence(textField.getText(), direction,
					options);
			if (!found)
				parentDialog.showMsgDialog(ExceptionsMessages.wordNotFoundMessage);
		}
		catch (Exception e) {
			e.printStackTrace();
			parentDialog.showMsgDialog(e.getMessage()); // TODO to
														// nie
														// zawsze
														// dobry
														// pomysł
		}
	}

}

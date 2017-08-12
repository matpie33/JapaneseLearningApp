package com.kanji.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.guimaker.row.Anchor;
import com.guimaker.row.RowMaker;
import com.kanji.actions.CommonActionsMaker;
import com.kanji.actions.GuiElementsMaker;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.NumberValues;
import com.kanji.constants.Options;
import com.kanji.constants.Prompts;
import com.kanji.myList.MyList;
import com.kanji.myList.SearchOptions;

public class SearchWordPanel extends AbstractPanelWithHotkeysInfo {

	private JTextField textField;
	private JRadioButton fullWordsSearchOption;
	private JRadioButton perfectMatchSearchOption;
	private SearchOptions searchOptions;
	private MyList list;

	public SearchWordPanel(MyList list) {
		super(true);
		this.list = list;
		searchOptions = SearchOptions.BY_LETTERS;
	}

	@Override
	void createElements() {
		JLabel prompt = new JLabel(Prompts.wordSearchDialogPrompt);
		textField = createInputTextField();

		JRadioButton defaultSearchOption = createRadioButtonForSearchingOption(
				SearchOptions.BY_LETTERS, Options.wordSearchDefaultOption);
		fullWordsSearchOption = createRadioButtonForSearchingOption(SearchOptions.BY_WORD,
				Options.wordSearchOnlyFullWordsOption);
		perfectMatchSearchOption = createRadioButtonForSearchingOption(
				SearchOptions.BY_FULL_EXPRESSION, Options.wordSearchPerfectMatchOption);

		addRadioButtonsToGroup(new JRadioButton[] { defaultSearchOption, fullWordsSearchOption,
				perfectMatchSearchOption });

		defaultSearchOption.setSelected(true);

		JButton previous = createButtonPrevious(ButtonsNames.buttonPreviousText);
		JButton next = createButtonNext(ButtonsNames.buttonNextText);
		JButton cancel = GuiElementsMaker.createButton(ButtonsNames.buttonCancelText,
				CommonActionsMaker.createDisposeAction(parentDialog));

		mainPanel.addRow(RowMaker.createHorizontallyFilledRow(prompt, textField)
				.fillHorizontallySomeElements(textField));
		mainPanel.addRow(RowMaker.createHorizontallyFilledRow(defaultSearchOption));
		mainPanel.addRow(RowMaker.createHorizontallyFilledRow(fullWordsSearchOption));
		mainPanel.addRow(RowMaker.createHorizontallyFilledRow(perfectMatchSearchOption));
		addHotkeysPanelHere();
		mainPanel.addRow( // TODO fix in gui maker: if putting rows as highest
							// as
							// possible, then west
							// should be as highest as possible, but now I need
							// to
							// use northwest
				RowMaker.createUnfilledRow(Anchor.WEST, cancel, previous, next));
	}

	private JRadioButton createRadioButtonForSearchingOption(SearchOptions searchOption,
			String optionLabel) {
		JRadioButton searchOptionRadioButton = new JRadioButton(optionLabel);
		searchOptionRadioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchOptions = searchOption;
			}
		});
		return searchOptionRadioButton;
	}

	private JTextField createInputTextField() {

		JTextField insertWord = new JTextField(20);
		insertWord.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					tryToFindNextOccurence(NumberValues.FORWARD_DIRECTION);
				}
			}
		});

		return insertWord;
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
		list.findAndHighlightNextOccurence(textField.getText(), direction, searchOptions,
				parentDialog);
	}

}

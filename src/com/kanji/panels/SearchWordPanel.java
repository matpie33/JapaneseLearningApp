package com.kanji.panels;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.Anchor;
import com.guimaker.row.RowMaker;
import com.kanji.Row.KanjiInformation;
import com.kanji.actions.CommonActionsMaker;
import com.kanji.actions.GuiElementsMaker;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.Labels;
import com.kanji.constants.Options;
import com.kanji.constants.Prompts;
import com.kanji.listSearching.KanjiIdChecker;
import com.kanji.listSearching.KanjiKeywordChecker;
import com.kanji.listSearching.SearchOptions;
import com.kanji.listSearching.SearchingDirection;
import com.kanji.myList.MyList;

public class SearchWordPanel extends AbstractPanelWithHotkeysInfo {

	private JTextField textField;
	private JRadioButton fullWordsSearchOption;
	private JRadioButton perfectMatchSearchOption;
	private SearchOptions searchOptions;
	private MyList<KanjiInformation> list;
	private final String OPTION_BY_KEYWORD = Labels.COMBOBOX_OPTION_SEARCH_BY_KEYWORD;
	private final String OPTION_BY_KANJI_ID = Labels.COMBOBOX_OPTION_SEARCH_BY_KANJI_ID;
	private CardLayout cardLayout;
	private JPanel searchingPanel;
	private final String SEARCH_BY_KEYWORD_PANEL_NAME = "Search by keyword panel";
	private final String SEARCH_BY_KANJI_ID_PANEL_NAME = "Search by kanji id panel";
	private SearchKryteria searchKryteria;
	private JTextField kanjiIdTextfield;

	private enum SearchKryteria {
		BY_KEYWORD, BY_KANJI_ID
	}

	public SearchWordPanel(MyList<KanjiInformation> list) {
		super(true);
		this.list = list;
		searchOptions = SearchOptions.BY_LETTERS;
		searchKryteria = SearchKryteria.BY_KEYWORD;
	}

	@Override
	void createElements() {
		JComboBox<String> comboBox = createCombobox();

		JButton previous = createButtonFindPrevious(ButtonsNames.buttonPreviousText);
		JButton next = createButtonFindNext(ButtonsNames.buttonNextText);
		JButton cancel = GuiElementsMaker.createButton(ButtonsNames.buttonCancelText,
				CommonActionsMaker.createDisposeAction(parentDialog));
		JLabel searchOptionPrompt = new JLabel(Prompts.SEARCH_OPTION_PROMPT);

		mainPanel.addRow(RowMaker.createUnfilledRow(Anchor.WEST, searchOptionPrompt, comboBox));
		MainPanel keywordSearchPanel = createSearchByKeywordPanel();
		MainPanel kanjiIdSearchPanel = createSearchByKanjiIdPanel();

		this.cardLayout = new CardLayout();
		searchingPanel = new JPanel(this.cardLayout);

		searchingPanel.add(SEARCH_BY_KEYWORD_PANEL_NAME, keywordSearchPanel.getPanel());
		searchingPanel.add(SEARCH_BY_KANJI_ID_PANEL_NAME, kanjiIdSearchPanel.getPanel());

		mainPanel.addRow(RowMaker.createUnfilledRow(Anchor.WEST, searchingPanel));

		addHotkeysPanelHere();
		mainPanel.addRow( // TODO fix in gui maker: if putting rows as highest
							// as
							// possible, then west
							// should be as highest as possible, but now I need
							// to
							// use northwest
				RowMaker.createUnfilledRow(Anchor.WEST, cancel, previous, next));

	}

	// TODO searching by word sometimes enters infinite loop
	private MainPanel createSearchByKanjiIdPanel() {
		kanjiIdTextfield = createInputTextField();

		MainPanel kanjiIdSearchPanel = new MainPanel(BasicColors.DARK_BLUE, true);
		kanjiIdSearchPanel.addRow(RowMaker.createUnfilledRow(Anchor.NORTHWEST,
				new JLabel(Labels.KANJI_ID_LABEL), kanjiIdTextfield));
		return kanjiIdSearchPanel;
	}

	private MainPanel createSearchByKeywordPanel() {
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

		MainPanel keywordSearchPanel = new MainPanel(BasicColors.DARK_BLUE);
		keywordSearchPanel.addRow(RowMaker.createHorizontallyFilledRow(prompt, textField)
				.fillHorizontallySomeElements(textField));
		keywordSearchPanel.addRow(RowMaker.createHorizontallyFilledRow(defaultSearchOption));
		keywordSearchPanel.addRow(RowMaker.createHorizontallyFilledRow(fullWordsSearchOption));
		keywordSearchPanel.addRow(RowMaker.createHorizontallyFilledRow(perfectMatchSearchOption));
		return keywordSearchPanel;
	}

	private JComboBox<String> createCombobox() {
		JComboBox<String> comboBox = new JComboBox<>();
		comboBox.addItem(OPTION_BY_KEYWORD);
		comboBox.addItem(OPTION_BY_KANJI_ID);
		comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox comboBox = (JComboBox) e.getSource();
				String option = (String) comboBox.getSelectedItem();
				if (option == OPTION_BY_KEYWORD) {
					cardLayout.show(searchingPanel, SEARCH_BY_KEYWORD_PANEL_NAME);
					searchKryteria = SearchKryteria.BY_KEYWORD;
				}
				else if (option == OPTION_BY_KANJI_ID) {
					cardLayout.show(searchingPanel, SEARCH_BY_KANJI_ID_PANEL_NAME);
					searchKryteria = SearchKryteria.BY_KANJI_ID;
				}
			}
		});
		return comboBox;
	}

	private JRadioButton createRadioButtonForSearchingOption(SearchOptions searchOption,
			String optionLabel) {
		JRadioButton searchOptionRadioButton = new JRadioButton(optionLabel);
		searchOptionRadioButton.setFocusable(false);
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
					tryToFindNextOccurence(SearchingDirection.FORWARD);
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

	private JButton createButtonFindPrevious(String text) {
		JButton button = new JButton(text);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tryToFindNextOccurence(SearchingDirection.BACKWARD);
			}
		});

		return button;
	}

	private JButton createButtonFindNext(String text) {
		JButton button = new JButton(text);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tryToFindNextOccurence(SearchingDirection.FORWARD);
			}
		});
		return button;
	}

	private void tryToFindNextOccurence(SearchingDirection direction) {
		if (searchKryteria.equals(SearchKryteria.BY_KEYWORD))
			list.findAndHighlightRowBasedOnProperty(new KanjiKeywordChecker(searchOptions),
					textField.getText(), direction, parentDialog);
		else if (searchKryteria.equals(SearchKryteria.BY_KANJI_ID))
			list.findAndHighlightRowBasedOnProperty(new KanjiIdChecker(),
					Integer.parseInt(kanjiIdTextfield.getText()), SearchingDirection.FORWARD,
					parentDialog);

	}

}

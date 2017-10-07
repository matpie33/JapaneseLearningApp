package com.kanji.panels;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.KeyModifiers;
import com.kanji.Row.KanjiInformation;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.HotkeysDescriptions;
import com.kanji.constants.Labels;
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
	private SearchCriteria searchKryteria;
	private JTextField kanjiIdTextfield;

	private enum SearchCriteria {
		BY_KEYWORD, BY_KANJI_ID
	}

	public SearchWordPanel(MyList<KanjiInformation> list) {
		this.list = list;
		searchOptions = SearchOptions.BY_LETTERS;
		searchKryteria = SearchCriteria.BY_KEYWORD;
	}

	@Override
	void createElements() {
		JComboBox<String> comboBox = createCombobox();

		AbstractButton previous = createButtonFindPrevious();
		AbstractButton next = createButtonFindNext();
		AbstractButton cancel = createButtonClose();
		JLabel searchOptionPrompt = new JLabel(Prompts.SEARCH_OPTION_PROMPT);

		MainPanel keywordSearchPanel = createSearchByKeywordPanel();
		MainPanel kanjiIdSearchPanel = createSearchByKanjiIdPanel();

		this.cardLayout = new CardLayout();
		searchingPanel = new JPanel(this.cardLayout);
		searchingPanel.setOpaque(false);

		searchingPanel.add(SEARCH_BY_KEYWORD_PANEL_NAME, keywordSearchPanel.getPanel());
		searchingPanel.add(SEARCH_BY_KANJI_ID_PANEL_NAME, kanjiIdSearchPanel.getPanel());

		MainPanel searchPanel = new MainPanel(null);

		searchPanel.addRow(
				SimpleRowBuilder.createRow(FillType.NONE, Anchor.WEST, searchOptionPrompt, comboBox));
		searchPanel.addRow(SimpleRowBuilder.createRow(FillType.HORIZONTAL, Anchor.NORTHWEST, searchingPanel)
				.useAllExtraVerticalSpace());
		mainPanel.addRow(SimpleRowBuilder.createRow(FillType.BOTH, Anchor.NORTHWEST, searchPanel.getPanel()));

		// TODO fix in gui maker: if putting rows as highest
		// as
		// possible, then west
		// should be as highest as possible, but now I need
		// to
		// use northwest
		setNavigationButtons(cancel, previous, next);

	}

	// TODO searching by word sometimes enters infinite loop
	private MainPanel createSearchByKanjiIdPanel() {
		kanjiIdTextfield = createInputTextField();

		MainPanel kanjiIdSearchPanel = new MainPanel(null, true);
		kanjiIdSearchPanel.addRow(SimpleRowBuilder.createRow(FillType.NONE, Anchor.NORTHWEST,
				new JLabel(Labels.KANJI_ID_LABEL), kanjiIdTextfield));
		return kanjiIdSearchPanel;
	}

	private MainPanel createSearchByKeywordPanel() {
		JLabel prompt = new JLabel(Prompts.SEARCH_DIALOG);
		textField = createInputTextField();

		JRadioButton defaultSearchOption = createRadioButtonForSearchingOption(
				SearchOptions.BY_LETTERS, Labels.WORD_SEARCH_DEFAULT_OPTION);
		fullWordsSearchOption = createRadioButtonForSearchingOption(SearchOptions.BY_WORD,
				Labels.WORD_SEARCH_ONLY_FULL_WORDS_OPTION);
		perfectMatchSearchOption = createRadioButtonForSearchingOption(
				SearchOptions.BY_FULL_EXPRESSION, Labels.WORD_SEARCH_PERFECT_MATCH_OPTION);

		addRadioButtonsToGroup(new JRadioButton[] { defaultSearchOption, fullWordsSearchOption,
				perfectMatchSearchOption });

		defaultSearchOption.setSelected(true);

		MainPanel keywordSearchPanel = new MainPanel(null);
		keywordSearchPanel.addRows(SimpleRowBuilder.createRow(FillType.HORIZONTAL, prompt, textField)
				.fillHorizontallySomeElements(textField).nextRow(defaultSearchOption)
				.nextRow(fullWordsSearchOption).nextRow(perfectMatchSearchOption));
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
					searchKryteria = SearchCriteria.BY_KEYWORD;
				}
				else if (option == OPTION_BY_KANJI_ID) {
					cardLayout.show(searchingPanel, SEARCH_BY_KANJI_ID_PANEL_NAME);
					searchKryteria = SearchCriteria.BY_KANJI_ID;
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
		return insertWord;
	}

	private void addRadioButtonsToGroup(JRadioButton[] buttons) {
		ButtonGroup group = new ButtonGroup();
		for (JRadioButton button : buttons)
			group.add(button);
	}

	private AbstractButton createButtonFindPrevious() {
		AbstractAction a = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tryToFindNextOccurence(SearchingDirection.BACKWARD);
			}
		};
		return createButtonWithHotkey(KeyModifiers.SHIFT, KeyEvent.VK_ENTER, a,
				ButtonsNames.FIND_PREVIOUS, HotkeysDescriptions.SEARCH_PREVIOUS_KANJI);
	}

	private AbstractButton createButtonFindNext() {
		AbstractAction a = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tryToFindNextOccurence(SearchingDirection.FORWARD);
			}
		};
		return createButtonWithHotkey(KeyEvent.VK_ENTER, a, ButtonsNames.FIND_NEXT,
				HotkeysDescriptions.SEARCH_NEXT_KANJI);
	}

	private void tryToFindNextOccurence(SearchingDirection direction) {
		if (searchKryteria.equals(SearchCriteria.BY_KEYWORD))
			list.findAndHighlightRowBasedOnPropertyStartingFromHighlightedWord(
					new KanjiKeywordChecker(searchOptions), textField.getText(), direction,
					parentDialog);
		else if (searchKryteria.equals(SearchCriteria.BY_KANJI_ID))
			list.findAndHighlightRowBasedOnPropertyStartingFromHighlightedWord(new KanjiIdChecker(),
					Integer.parseInt(kanjiIdTextfield.getText()), SearchingDirection.FORWARD,
					parentDialog);

	}

	@Override
	public void afterVisible() {
		textField.requestFocusInWindow();
	}

}

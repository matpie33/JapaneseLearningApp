package com.kanji.panels;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.text.JTextComponent;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.KeyModifiers;
import com.kanji.listElements.KanjiInformation;
import com.kanji.strings.ButtonsNames;
import com.kanji.strings.HotkeysDescriptions;
import com.kanji.strings.Labels;
import com.kanji.strings.Prompts;
import com.kanji.controllers.SearchWordController;
import com.kanji.enums.SearchCriteria;
import com.kanji.listSearching.KanjiIdChecker;
import com.kanji.listSearching.KanjiKeywordChecker;
import com.kanji.listSearching.SearchOptions;
import com.kanji.listSearching.SearchingDirection;
import com.kanji.myList.MyList;
import com.kanji.utilities.CommonGuiElementsMaker;

public class SearchWordPanel extends AbstractPanelWithHotkeysInfo {

	private JTextComponent textField;
	private SearchOptions searchOptions;
	private MyList<KanjiInformation> list;
	private CardLayout cardLayout;
	private JPanel searchingPanel;
	private SearchCriteria searchCriteria;
	private JTextComponent kanjiIdTextfield;
	private SearchWordController searchWordController;

	public SearchWordPanel(MyList<KanjiInformation> list) {
		searchWordController = new SearchWordController(this);
		this.list = list;
		searchOptions = SearchOptions.BY_WORD_FRAGMENT;
		searchCriteria = SearchCriteria.BY_KEYWORD;
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

		searchingPanel.add(SearchCriteria.BY_KEYWORD.getPanelName(), keywordSearchPanel.getPanel());
		searchingPanel.add(SearchCriteria.BY_KANJI_ID.getPanelName(), kanjiIdSearchPanel.getPanel());

		MainPanel searchPanel = new MainPanel(null);

		searchPanel.addRows(
				SimpleRowBuilder.createRow(FillType.NONE, Anchor.WEST, searchOptionPrompt, comboBox));
		searchPanel.addRows(SimpleRowBuilder.createRow(FillType.HORIZONTAL, Anchor.NORTHWEST, searchingPanel)
				.useAllExtraVerticalSpace());
		mainPanel.addRows(SimpleRowBuilder.createRow(FillType.BOTH, Anchor.WEST, searchPanel.getPanel()));

		// TODO fix in gui maker: if putting rows as highest
		// as
		// possible, then west
		// should be as highest as possible, but now I need
		// to
		// use northwest
		setNavigationButtons(cancel, previous, next);

	}

	private MainPanel createSearchByKanjiIdPanel() {
		kanjiIdTextfield = createInputTextField();

		MainPanel kanjiIdSearchPanel = new MainPanel(null, true);
		kanjiIdSearchPanel.addRows(SimpleRowBuilder.createRow(FillType.NONE, Anchor.NORTHWEST,
				new JLabel(Labels.KANJI_ID_LABEL), kanjiIdTextfield));
		return kanjiIdSearchPanel;
	}

	private MainPanel createSearchByKeywordPanel() {
		JLabel prompt = new JLabel(Prompts.SEARCH_DIALOG);
		textField = CommonGuiElementsMaker.createKanjiWordInput("");
		List<JRadioButton> radioButtons = Arrays.asList(SearchOptions.values()).stream().
				map(option->createRadioButtonForSearchingOption(option)).collect(
				Collectors.toList());
		radioButtons.get(0).setSelected(true);
		addRadioButtonsToGroup(radioButtons);
		MainPanel keywordSearchPanel = new MainPanel(null);
		keywordSearchPanel.addRows(SimpleRowBuilder.createRow(FillType.HORIZONTAL, prompt, textField)
				.fillHorizontallySomeElements(textField));
		radioButtons.stream().forEach(radioButton -> keywordSearchPanel.addRow(
				SimpleRowBuilder.createRow(FillType.HORIZONTAL, radioButton )));
		return keywordSearchPanel;
	}

	private JComboBox<String> createCombobox() {
		JComboBox<String> comboBox = new JComboBox<>();
		Arrays.asList(SearchCriteria.values()).stream().forEach(
				option -> comboBox.addItem(option.getComboboxLabel()));
		comboBox.addActionListener(searchWordController.createActionSwitchSearchingByOption());
		return comboBox;
	}

	public void switchToSearchingBy (SearchCriteria searchCriteria){
		cardLayout.show(searchingPanel, searchCriteria.getPanelName());
		this.searchCriteria = searchCriteria;
	}

	private JRadioButton createRadioButtonForSearchingOption(SearchOptions searchOption) {
		JRadioButton searchOptionRadioButton = new JRadioButton(searchOption.getPanelLabel());
		searchOptionRadioButton.setFocusable(false);
		searchOptionRadioButton.addActionListener(
				searchWordController.createActionSwitchSearchCriteria(searchOption));
		return searchOptionRadioButton;
	}

	public void setSearchOptions (SearchOptions options){
		searchOptions = options;
	}

	private JTextComponent createInputTextField() {
		JTextComponent insertWord = CommonGuiElementsMaker.createKanjiIdInput();
		return insertWord;
	}

	private void addRadioButtonsToGroup(List <JRadioButton> buttons) {
		ButtonGroup group = new ButtonGroup();
		for (JRadioButton button : buttons)
			group.add(button);
	}

	private AbstractButton createButtonFindPrevious() {
		return createButtonWithHotkey(KeyModifiers.SHIFT, KeyEvent.VK_ENTER,
				searchWordController.createActionFindWord(SearchingDirection.BACKWARD),
				ButtonsNames.FIND_PREVIOUS, HotkeysDescriptions.SEARCH_PREVIOUS_KANJI);
	}

	private AbstractButton createButtonFindNext() {
		return createButtonWithHotkey(KeyEvent.VK_ENTER,
				searchWordController.createActionFindWord(SearchingDirection.FORWARD),
				ButtonsNames.FIND_NEXT,	HotkeysDescriptions.SEARCH_NEXT_KANJI);
	}

	public void tryToFindNextOccurence(SearchingDirection direction) {
		if (searchCriteria.equals(SearchCriteria.BY_KEYWORD))
			list.findAndHighlightRowBasedOnPropertyStartingFromHighlightedWord(
					new KanjiKeywordChecker(searchOptions), textField.getText(), direction);
		else if (searchCriteria.equals(SearchCriteria.BY_KANJI_ID))
			list.findAndHighlightRowBasedOnPropertyStartingFromHighlightedWord(new KanjiIdChecker(),
					Integer.parseInt(kanjiIdTextfield.getText()), SearchingDirection.FORWARD
					);
	}

	@Override
	public void afterVisible() {
		textField.requestFocusInWindow();
	}

}

package com.kanji.panelsAndControllers.panels;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.KeyModifiers;
import com.kanji.constants.enums.ListPanelViewMode;
import com.kanji.constants.enums.SearchingDirection;
import com.kanji.constants.enums.WordSearchOptions;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.constants.strings.Labels;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElementPropertyManagers.JapaneseWordWritingsChecker;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElementPropertyManagers.WordSearchOptionsHolder;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.listElements.ListElementData;
import com.kanji.list.listRows.panelCreators.JapaneseWordPanelCreator;
import com.kanji.list.myList.MyList;
import com.kanji.model.TextInputAndPropertyManagerForListElement;
import com.kanji.panelsAndControllers.controllers.SearchWordController;
import com.kanji.utilities.CommonGuiElementsMaker;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SearchWordPanel<Word extends ListElement>
		extends AbstractPanelWithHotkeysInfo {

	private MyList<Word> list;
	private CardLayout cardLayout;
	private JPanel searchingPanel;
	private SearchWordController<Word> searchWordController;
	private String selectedComboboxLabel;
	private List<String> comboboxLabels;
	private List<TextInputAndPropertyManagerForListElement> listOfInputsAndPropertyManagersForListElementType;
	private ApplicationWindow applicationWindow;

	public SearchWordPanel(ApplicationWindow applicationWindow,
			MyList<Word> list) {
		searchWordController = new SearchWordController<>(this, list);
		this.list = list;
		listOfInputsAndPropertyManagersForListElementType = new ArrayList<>();
		comboboxLabels = new ArrayList<>();
		this.applicationWindow = applicationWindow;
	}

	@Override public void createElements() {

		AbstractButton previous = createButtonFindPrevious();
		AbstractButton next = createButtonFindNext();
		AbstractButton cancel = createButtonClose();
		JLabel searchOptionPrompt = new JLabel(Prompts.SEARCH_OPTION_PROMPT);

		this.cardLayout = new CardLayout();
		searchingPanel = new JPanel(this.cardLayout);
		searchingPanel.setOpaque(false);

		createPanelForElementType();
		JComboBox<String> comboBox = createCombobox();

		MainPanel searchPanel = new MainPanel(null);

		searchPanel.addRows(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.WEST, searchOptionPrompt,
						comboBox));
		searchPanel.addRows(SimpleRowBuilder
				.createRow(FillType.HORIZONTAL, Anchor.NORTHWEST,
						searchingPanel).useAllExtraVerticalSpace());
		mainPanel.addRows(SimpleRowBuilder
				.createRow(FillType.BOTH, Anchor.WEST, searchPanel.getPanel()));

		// TODO fix in gui maker: if putting rows as highest
		// as
		// possible, then west
		// should be as highest as possible, but now I need
		// to
		// use northwest
		setNavigationButtons(cancel, previous, next);

	}

	private void createPanelForElementType() {
		for (ListElementData elementData : list.getListElementData()) {
			comboboxLabels.add(elementData.getComboboxLabel());
			JTextComponent textInputForElementType;
			JPanel panelForElementType;

			switch (elementData.getListElementPropertyType()) {
			case STRING_LONG_WORD:
				textInputForElementType = CommonGuiElementsMaker
						.createShortInput("");
				panelForElementType = createSearchByWordPanel(
						textInputForElementType).getPanel();
				break;
			case NUMERIC_INPUT:
				textInputForElementType = CommonGuiElementsMaker
						.createKanjiIdInput();
				panelForElementType = createSearchByNumericInputPanel(
						textInputForElementType).getPanel();
				break;
			case STRING_SHORT_WORD:
				textInputForElementType = CommonGuiElementsMaker
						.createTextField("");
				panelForElementType = createSearchByWordPanel(
						textInputForElementType).getPanel();
				break;
			case KANA_KANJI_WRITINGS:
				JapaneseWordPanelCreator japaneseWordPanelCreator = new JapaneseWordPanelCreator(
						applicationWindow, false, parentDialog);
				panelForElementType = createSearchByKanaAndKanjiWritingsPanel(
						japaneseWordPanelCreator,
						(JapaneseWordWritingsChecker) elementData
								.getListElementPropertyManager()).getPanel();
				textInputForElementType = japaneseWordPanelCreator
						.getKanaToKanjiWritingsTextComponents().keySet()
						.iterator().next();
				break;

			default:
				throw new RuntimeException("Unknown type of list word element");
			}
			listOfInputsAndPropertyManagersForListElementType
					.add(new TextInputAndPropertyManagerForListElement(
							elementData.getComboboxLabel(),
							textInputForElementType,
							elementData.getListElementPropertyManager()));
			searchingPanel
					.add(elementData.getComboboxLabel(), panelForElementType);
		}
	}

	private MainPanel createSearchByNumericInputPanel(
			JTextComponent inputField) {
		MainPanel kanjiIdSearchPanel = new MainPanel(null, true);
		kanjiIdSearchPanel.addRows(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.NORTHWEST,
						new JLabel(Labels.KANJI_ID_LABEL), inputField));
		return kanjiIdSearchPanel;
	}

	private MainPanel createSearchByKanaAndKanjiWritingsPanel(
			JapaneseWordPanelCreator japaneseWordPanelCreator,
			JapaneseWordWritingsChecker writingsChecker) {

		MainPanel panel = new MainPanel(null, true);

		japaneseWordPanelCreator.addKanaAndKanjiWritingRow(
				JapaneseWordInformation.getInitializer().initializeElement(),
				panel, null, ListPanelViewMode.ADD, false);

		writingsChecker.setJapaneseWordPanelCreator(japaneseWordPanelCreator);
		return panel;
	}

	private MainPanel createSearchByWordPanel(JTextComponent inputField) {
		JLabel prompt = new JLabel(Prompts.SEARCH_DIALOG);
		List<JRadioButton> radioButtons = Arrays
				.asList(WordSearchOptions.values()).stream().
						map(option -> createRadioButtonForSearchingOption(
								option)).collect(Collectors.toList());
		radioButtons.get(0).setSelected(true);
		addRadioButtonsToGroup(radioButtons);
		MainPanel keywordSearchPanel = new MainPanel(null);
		keywordSearchPanel.addRows(SimpleRowBuilder
				.createRow(FillType.HORIZONTAL, prompt, inputField)
				.fillHorizontallySomeElements(inputField));
		radioButtons.stream().forEach(radioButton -> keywordSearchPanel
				.addRow(SimpleRowBuilder
						.createRow(FillType.HORIZONTAL, radioButton)));
		return keywordSearchPanel;
	}

	private JComboBox<String> createCombobox() {
		JComboBox<String> comboBox = new JComboBox<>();
		comboboxLabels.stream().forEach(comboBox::addItem);

		comboBox.addActionListener(
				searchWordController.createActionSwitchSearchingByOption());
		comboBox.setSelectedIndex(0);
		return comboBox;
	}

	public void switchToPanel(String selectedComboboxLabel) {
		this.selectedComboboxLabel = selectedComboboxLabel;
		cardLayout.show(searchingPanel, selectedComboboxLabel);
		SwingUtilities.invokeLater(
				() -> getTextInputAndPropertyManager().getTextComponent()
						.requestFocusInWindow());

	}

	private JRadioButton createRadioButtonForSearchingOption(
			WordSearchOptions searchOption) {
		JRadioButton searchOptionRadioButton = new JRadioButton(
				searchOption.getPanelLabel());
		searchOptionRadioButton.setFocusable(false);
		searchOptionRadioButton.addActionListener(searchWordController
				.createActionSwitchSearchCriteria(searchOption));
		return searchOptionRadioButton;
	}

	private void addRadioButtonsToGroup(List<JRadioButton> buttons) {
		ButtonGroup group = new ButtonGroup();
		for (JRadioButton button : buttons)
			group.add(button);
	}

	private AbstractButton createButtonFindPrevious() {
		return createButtonWithHotkey(KeyModifiers.SHIFT, KeyEvent.VK_ENTER,
				searchWordController
						.createActionFindWord(SearchingDirection.BACKWARD),
				ButtonsNames.FIND_PREVIOUS,
				HotkeysDescriptions.SEARCH_PREVIOUS_KANJI);
	}

	private AbstractButton createButtonFindNext() {
		return createButtonWithHotkey(KeyEvent.VK_ENTER, searchWordController
						.createActionFindWord(SearchingDirection.FORWARD),
				ButtonsNames.FIND_NEXT, HotkeysDescriptions.SEARCH_NEXT_KANJI);
	}

	public TextInputAndPropertyManagerForListElement getTextInputAndPropertyManager() {
		for (TextInputAndPropertyManagerForListElement textAndProperty : listOfInputsAndPropertyManagersForListElementType) {
			if (textAndProperty.getComboboxLabel()
					.equals(selectedComboboxLabel)) {
				return textAndProperty;
			}
		}
		return null;
	}

	public void setSearchOptions(WordSearchOptions wordSearchOptions) {
		ListElementPropertyManager currentListElementPropertyManager = getTextInputAndPropertyManager()
				.getListElementPropertyManager();
		if (currentListElementPropertyManager instanceof WordSearchOptionsHolder) {
			((WordSearchOptionsHolder) currentListElementPropertyManager)
					.setWordSearchOptions(wordSearchOptions);
		}
	}

}

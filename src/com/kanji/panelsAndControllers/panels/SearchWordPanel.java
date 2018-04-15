package com.kanji.panelsAndControllers.panels;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.KeyModifiers;
import com.kanji.constants.enums.SearchingDirection;
import com.kanji.constants.enums.WordSearchOptions;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElementPropertyManagers.WordSearchOptionsHolder;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.myList.ListPropertyInformation;
import com.kanji.list.myList.ListRowData;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.SearchWordController;
import com.kanji.utilities.CommonListElements;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SearchWordPanel<Word extends ListElement>
		extends AbstractPanelWithHotkeysInfo {

	private CardLayout cardLayout;
	private JPanel searchingPanel;
	private SearchWordController<Word> searchWordController;
	private ListRowData listRowData;
	private String currentlySearchedProperty;
	private MyList<Word> searchedList;

	public SearchWordPanel(ApplicationWindow applicationWindow,
			MyList<Word> searchedList) {
		searchWordController = new SearchWordController<>(this, searchedList);
		this.searchedList = searchedList;
	}

	@Override
	public void createElements() {

		AbstractButton previous = createButtonFindPrevious();
		AbstractButton next = createButtonFindNext();
		AbstractButton cancel = createButtonClose();
		JLabel searchOptionPrompt = new JLabel(Prompts.SEARCH_OPTION_PROMPT);

		this.cardLayout = new CardLayout();
		searchingPanel = new JPanel(this.cardLayout);
		searchingPanel.setOpaque(false);

		listRowData = searchedList.getListRowMaker().createListRow(
				searchedList.getWordInitializer().initializeElement(),
				CommonListElements.forSingleRowOnly(Color.BLACK), true);
		for (Map.Entry<String, ListPropertyInformation> listPropertyData : listRowData
				.getRowPropertiesData().entrySet()) {
			MainPanel rowForProperty = new MainPanel(null);
			rowForProperty
					.addRow(listPropertyData.getValue().getRowForProperty());
			Map<JTextComponent, ListElementPropertyManager> textFieldsWithPropertyManagers = listPropertyData
					.getValue().getTextFieldsWithPropertyManagers();
			if (textFieldsWithPropertyManagers.values().iterator()
					.next() instanceof WordSearchOptionsHolder) {
				addWordSearchOptions(rowForProperty);
			}
			searchingPanel
					.add(listPropertyData.getKey(), rowForProperty.getPanel());
		}

		JComboBox<String> comboBox = createComboboxForSearchedProperty();
		addHotkeyForSwitchingComboboxValue(comboBox);

		MainPanel searchPanel = new MainPanel(null);
		JLabel prompt = new JLabel(Prompts.SEARCH_DIALOG);
		searchPanel.addRow(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.CENTER, prompt));
		searchPanel.addRow(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.WEST, searchOptionPrompt,
						comboBox));
		searchPanel.addRow(SimpleRowBuilder
				.createRow(FillType.HORIZONTAL, Anchor.NORTHWEST,
						searchingPanel).useAllExtraVerticalSpace());
		mainPanel.addRow(SimpleRowBuilder
				.createRow(FillType.BOTH, Anchor.WEST, searchPanel.getPanel()));

		// TODO fix in gui maker: if putting rows as highest
		// as
		// possible, then west
		// should be as highest as possible, but now I need
		// to
		// use northwest
		setNavigationButtons(cancel, previous, next);

	}

	private void addHotkeyForSwitchingComboboxValue(JComboBox comboBox) {
		AbstractAction action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (comboBox.getSelectedIndex() < comboBox.getItemCount() - 1) {
					comboBox.setSelectedIndex(comboBox.getSelectedIndex() + 1);
				}
				else {
					comboBox.setSelectedIndex(0);
				}

			}
		};
		addHotkey(KeyModifiers.CONTROL, KeyEvent.VK_SPACE, action,
				mainPanel.getPanel(),
				HotkeysDescriptions.SWITCH_SEARCH_CRITERIA);
	}

	private void addWordSearchOptions(MainPanel panel) {

		List<JRadioButton> radioButtons = Arrays
				.stream(WordSearchOptions.values()).
						map(this::createRadioButtonForSearchingOption)
				.collect(Collectors.toList());
		radioButtons.get(0).setSelected(true);
		addRadioButtonsToGroup(radioButtons);
		radioButtons.forEach(radioButton -> panel.addRow(SimpleRowBuilder
				.createRow(FillType.HORIZONTAL, radioButton)));
	}

	private JComboBox<String> createComboboxForSearchedProperty() {
		JComboBox<String> comboBox = new JComboBox<>();
		listRowData.getRowPropertiesData().keySet().forEach(comboBox::addItem);
		comboBox.addActionListener(
				searchWordController.createActionSwitchSearchingByOption());
		comboBox.setSelectedIndex(0);
		return comboBox;
	}

	public void switchToListProperty(String property) {
		this.currentlySearchedProperty = property;
		cardLayout.show(searchingPanel, property);
		SwingUtilities.invokeLater(this::focusFirstTextfieldForCurrentProperty);

	}

	private void focusFirstTextfieldForCurrentProperty() {
		Map<JTextComponent, ListElementPropertyManager> textFieldsWithPropertyManagers = listRowData
				.getRowPropertiesData().get(currentlySearchedProperty)
				.getTextFieldsWithPropertyManagers();
		textFieldsWithPropertyManagers.keySet().iterator().next()
				.requestFocusInWindow();
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

	public void setSearchOptions(WordSearchOptions wordSearchOptions) {
		Map<JTextComponent, ListElementPropertyManager> textFieldsWithPropertyManagers = getTextFieldsWithPropertyManagersForCurrentProperty();
		ListElementPropertyManager currentListElementPropertyManager = textFieldsWithPropertyManagers
				.values().iterator().next();
		if (currentListElementPropertyManager instanceof WordSearchOptionsHolder) {
			((WordSearchOptionsHolder) currentListElementPropertyManager)
					.setWordSearchOptions(wordSearchOptions);
		}
	}

	public Map<JTextComponent, ListElementPropertyManager> getTextFieldsWithPropertyManagersForCurrentProperty() {
		return listRowData.getRowPropertiesData().get(currentlySearchedProperty)
				.getTextFieldsWithPropertyManagers();
	}

}

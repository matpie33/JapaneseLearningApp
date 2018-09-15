package com.kanji.list.myList;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.ButtonType;
import com.guimaker.enums.FillType;
import com.guimaker.options.ButtonOptions;
import com.guimaker.options.ComboboxOptions;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.InputGoal;
import com.kanji.constants.enums.WordSearchOptions;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElementPropertyManagers.WordSearchOptionsHolder;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.listElements.ListElementInitializer;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ListSearchPanelCreator<Word extends ListElement> {

	private ListRowData<Word> listRowData;
	private String currentlySearchedProperty = "";
	private CardLayout cardLayout;
	private JPanel searchingPanel;
	private JComboBox<String> comboBox;



	public JPanel createPanel(ListRowData<Word> listRowData){

		cardLayout = new CardLayout();
		searchingPanel = new JPanel(cardLayout);
		searchingPanel.setOpaque(false);
		this.listRowData = listRowData;
		JLabel searchOptionPrompt = GuiElementsCreator.createLabel(
				new ComponentOptions().text(Prompts.SEARCH_OPTION_PROMPT));

		comboBox = createComboboxForSearchedProperty();

		for (Map.Entry<String, ListPropertyInformation<Word>> listPropertyData : listRowData
				.getRowPropertiesData().entrySet()) {
			MainPanel rowForProperty = new MainPanel(null);
			rowForProperty
					.addRow(listPropertyData.getValue().getRowForProperty());
			Map<JTextComponent, ListElementPropertyManager<?, Word>> textFieldsWithPropertyManagers = listPropertyData
					.getValue().getTextFieldsWithPropertyManagers();
			if (!textFieldsWithPropertyManagers.isEmpty() && textFieldsWithPropertyManagers.values().iterator()
					.next() instanceof WordSearchOptionsHolder) {
				addWordSearchOptions(rowForProperty);
			}
			searchingPanel
					.add(listPropertyData.getKey(), rowForProperty.getPanel());
		}

		MainPanel searchPanel = new MainPanel(null);
		JLabel prompt = GuiElementsCreator.createLabel(
				new ComponentOptions().text(Prompts.SEARCH_DIALOG));
		searchPanel.addRow(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.CENTER, prompt));
		searchPanel.addRow(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.WEST, searchOptionPrompt,
						comboBox));
		searchPanel.addRow(SimpleRowBuilder
				.createRow(FillType.HORIZONTAL, Anchor.NORTHWEST,
						searchingPanel).useAllExtraVerticalSpace());

		return searchPanel.getPanel();
	}

	private JComboBox<String> createComboboxForSearchedProperty() {
		JComboBox<String> comboBox = GuiElementsCreator
				.createCombobox(new ComboboxOptions());
		if (listRowData.getRowPropertiesData().isEmpty()){
			return comboBox;
		}
		listRowData.getRowPropertiesData().keySet().forEach(comboBox::addItem);
		comboBox.addActionListener(createActionSwitchSearchingByOption());
		comboBox.setSelectedIndex(0);
		return comboBox;
	}

	private void addWordSearchOptions(MainPanel panel) {

		List<AbstractButton> radioButtons = Arrays
				.stream(WordSearchOptions.values()).
						map(this::createRadioButtonForSearchingOption)
				.collect(Collectors.toList());
		radioButtons.get(0).setSelected(true);
		addRadioButtonsToGroup(radioButtons);
		radioButtons.forEach(radioButton -> panel.addRow(SimpleRowBuilder
				.createRow(FillType.HORIZONTAL, radioButton)));
	}

	private void setSearchOptions(WordSearchOptions wordSearchOptions) {
		Map<JTextComponent, ListElementPropertyManager<?, Word>> textFieldsWithPropertyManagers = getTextFieldsWithPropertyManagersForCurrentProperty();
		ListElementPropertyManager currentListElementPropertyManager = textFieldsWithPropertyManagers
				.values().iterator().next();
		if (currentListElementPropertyManager instanceof WordSearchOptionsHolder) {
			((WordSearchOptionsHolder) currentListElementPropertyManager)
					.setWordSearchOptions(wordSearchOptions);
		}
	}

	private AbstractButton createRadioButtonForSearchingOption(
			WordSearchOptions searchOption) {
		AbstractButton searchOptionRadioButton = GuiElementsCreator
				.createButtonlikeComponent(
						new ButtonOptions(ButtonType.RADIOBUTTON)
								.text(searchOption.getPanelLabel()),
						createActionSwitchSearchCriteria(searchOption));
		searchOptionRadioButton.setFocusable(false);
		return searchOptionRadioButton;
	}

	private AbstractAction createActionSwitchSearchCriteria(
			WordSearchOptions searchOption) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setSearchOptions(searchOption);
			}
		};
	}

	private AbstractAction createActionSwitchSearchingByOption() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox comboBox = (JComboBox) e.getSource();
				String optionLabel = (String) comboBox.getSelectedItem();
				switchToListProperty(optionLabel);
			}
		};
	}

	public void switchToListProperty(String property) {
		this.currentlySearchedProperty = property;
		cardLayout.show(searchingPanel, property);
		SwingUtilities.invokeLater(this::focusFirstTextfieldForCurrentProperty);
	}

	private void focusFirstTextfieldForCurrentProperty() {
		Map<JTextComponent, ListElementPropertyManager<?, Word>> textFieldsWithPropertyManagers = listRowData
				.getRowPropertiesData().get(currentlySearchedProperty)
				.getTextFieldsWithPropertyManagers();
		if (textFieldsWithPropertyManagers.isEmpty()){
			return;
		}
		textFieldsWithPropertyManagers.keySet().iterator().next()
				.requestFocusInWindow();
	}

	public AbstractAction createActionSwitchComboboxValue() {
		return new AbstractAction() {
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

	}

	private void addRadioButtonsToGroup(List<AbstractButton> buttons) {
		ButtonGroup group = new ButtonGroup();
		for (AbstractButton button : buttons)
			group.add(button);
	}

	public Map<JTextComponent, ListElementPropertyManager<?, Word>> getTextFieldsWithPropertyManagersForCurrentProperty() {
		return listRowData.getRowPropertiesData().get(currentlySearchedProperty)
				.getTextFieldsWithPropertyManagers();
	}

	public JTextComponent getFirstTextComponent() {
		if (listRowData
				.getRowPropertiesData().isEmpty()){
			return new JTextField();
		}
		Map<JTextComponent, ListElementPropertyManager<?, Word>> textFieldsWithPropertyManagers = listRowData
				.getRowPropertiesData().get(currentlySearchedProperty)
				.getTextFieldsWithPropertyManagers();
		return textFieldsWithPropertyManagers.keySet().iterator().next();
	}

}

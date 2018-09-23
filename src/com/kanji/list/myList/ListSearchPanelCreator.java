package com.kanji.list.myList;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.options.ComboboxOptions;
import com.guimaker.options.ComponentOptions;
import com.guimaker.options.TextComponentOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElements.ListElement;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;

public class ListSearchPanelCreator<Word extends ListElement> {

	private ListRowData<Word> listRowData;
	private JComboBox<String> comboBox;
	private JLabel filteringProperty;
	private JTextComponent filteringInput;
	private ListElementPropertyManager<?, Word> listElementPropertyManager;
	private MainPanel searchPanel;
	private AbstractButton buttonFilter;
	public static final String COLON = ":";

	public JPanel createPanel(ListRowData<Word> listRowData,
			AbstractButton buttonFilter) {

		this.buttonFilter = buttonFilter;
		this.listRowData = listRowData;

		searchPanel = new MainPanel(null);

		JLabel searchOptionPrompt = GuiElementsCreator.createLabel(
				new ComponentOptions().text(Prompts.SEARCH_OPTION_PROMPT));

		filteringProperty = GuiElementsCreator
				.createLabel(new ComponentOptions());
		filteringInput = GuiElementsCreator.createTextField(
				new TextComponentOptions().rowsAndColumns(1, 15));

		comboBox = createComboboxForSearchedProperty();
		searchPanel.addRow(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.WEST, searchOptionPrompt,
						comboBox));
		addFilteringInputAndButton();

		return searchPanel.getPanel();
	}

	private void addFilteringInputAndButton() {
		searchPanel.addRow(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.WEST, filteringProperty,
						filteringInput, buttonFilter));
	}

	private JComboBox<String> createComboboxForSearchedProperty() {
		JComboBox<String> comboBox = GuiElementsCreator
				.createCombobox(new ComboboxOptions());
		if (listRowData.getRowPropertiesData().isEmpty()) {
			return comboBox;
		}
		listRowData.getRowPropertiesData().keySet().forEach(comboBox::addItem);
		comboBox.addActionListener(createActionSwitchSearchingByOption());
		comboBox.setSelectedIndex(0);
		return comboBox;
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
		filteringProperty.setText(property + COLON);
		listElementPropertyManager = listRowData.getRowPropertiesData()
				.get(property).getFilteringHandler();
		filteringInput = listRowData.getRowPropertiesData().get(property)
				.getFilteringTextComponent();
		searchPanel.removeLastRow();
		addFilteringInputAndButton();
		SwingUtilities.invokeLater(this::focusFirstTextfieldForCurrentProperty);
	}

	private void focusFirstTextfieldForCurrentProperty() {
		filteringInput.requestFocusInWindow();
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

	public JTextComponent getFilteringInput() {
		return filteringInput;
	}

	public ListElementPropertyManager<?, Word> getPropertyManagerForInput() {
		return listElementPropertyManager;
	}
}

package com.kanji.panelsAndControllers.controllers;

import com.kanji.constants.enums.SearchingDirection;
import com.kanji.constants.enums.WordSearchOptions;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.panels.SearchWordPanel;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Map;

public class SearchWordController<Word extends ListElement> {

	private SearchWordPanel searchWordPanel;
	private MyList<Word> list;

	public SearchWordController(SearchWordPanel panel, MyList list) {
		this.searchWordPanel = panel;
		this.list = list;
	}

	public AbstractAction createActionSwitchSearchingByOption() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox comboBox = (JComboBox) e.getSource();
				String optionLabel = (String) comboBox.getSelectedItem();
				searchWordPanel.switchToListProperty(optionLabel);
			}
		};
	}

	public AbstractAction createActionSwitchSearchCriteria(
			WordSearchOptions options) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchWordPanel.setSearchOptions(options);
			}
		};
	}

	public AbstractAction createActionFindWord(
			SearchingDirection searchingDirection) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Map<JTextComponent, ListElementPropertyManager> textInputAndPropertyManagerForListElement = searchWordPanel
						.getTextFieldsWithPropertyManagersForCurrentProperty();
				Component c = searchWordPanel.getDialog().getContainer()
						.getFocusOwner();
				JTextComponent focusedTextField;
				if (c instanceof JTextComponent) {
					focusedTextField = (JTextComponent) c;
				}
				else {
					focusedTextField = textInputAndPropertyManagerForListElement
							.keySet().iterator().next();
				}
				ListElementPropertyManager propertyManager = textInputAndPropertyManagerForListElement
						.get(focusedTextField);
				Object property = propertyManager
						.validateInputAndConvertToProperty(focusedTextField);
				//TODO if focused component isn't a textfield - all inputs are valid
				//because they lost focus, that means why just have to convert to property
				// but not validate - separate the methods

				if (property == null) {
					KeyboardFocusManager.getCurrentKeyboardFocusManager()
							.clearGlobalFocusOwner();
					return;
				}
				list.findAndHighlightRowBasedOnPropertyStartingFromHighlightedWord(
						propertyManager, property, searchingDirection);
			}
		};
	}

}

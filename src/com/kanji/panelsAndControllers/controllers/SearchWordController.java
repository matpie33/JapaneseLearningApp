package com.kanji.panelsAndControllers.controllers;

import com.kanji.constants.enums.MovingDirection;
import com.kanji.constants.enums.WordSearchOptions;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.myList.InputValidationListener;
import com.kanji.list.myList.MyList;
import com.kanji.model.PropertyPostValidationData;
import com.kanji.panelsAndControllers.panels.SearchWordPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SearchWordController<Word extends ListElement>
		implements InputValidationListener<Word> {

	private SearchWordPanel searchWordPanel;
	private MyList<Word> list;
	private boolean searchWasRequested = false;
	private MovingDirection currentSearchDirection;
	private Component lastFocusedElement;

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
			MovingDirection searchDirection) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				lastFocusedElement = searchWordPanel.getDialog().getContainer().getFocusOwner();
				validateFocusedElement();
				currentSearchDirection = searchDirection;
				searchWasRequested = true;
			}
		};
	}

	private void validateFocusedElement (){
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.clearGlobalFocusOwner();
	}

	@Override
	public <Property> void inputValidated(
			PropertyPostValidationData<Property, Word> postValidationData) {
		if (postValidationData.isValid() && searchWasRequested) {
			list.findAndHighlightRowBasedOnPropertyStartingFromHighlightedWord(
					postValidationData.getPropertyManager(),
					postValidationData.getRecentlyValidatedProperty(),
					currentSearchDirection);
			lastFocusedElement.requestFocusInWindow();
		}
		searchWasRequested = false;


	}
}

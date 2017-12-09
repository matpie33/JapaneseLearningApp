package com.kanji.controllers;

import com.kanji.enums.SearchOptions;
import com.kanji.enums.SearchingDirection;
import com.kanji.listElements.ListElement;
import com.kanji.model.TextInputAndPropertyManagerForListElement;
import com.kanji.myList.MyList;
import com.kanji.panels.SearchWordPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class SearchWordController <Word extends ListElement> {

	private SearchWordPanel searchWordPanel;
	private MyList<Word> list;

	public SearchWordController (SearchWordPanel panel, MyList list){
		this.searchWordPanel = panel;
		this.list = list;
	}

	public AbstractAction createActionSwitchSearchingByOption (){
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox comboBox = (JComboBox) e.getSource();
				String optionLabel = (String) comboBox.getSelectedItem();
				searchWordPanel.switchToPanel(optionLabel);
			}
		};
	}

	public AbstractAction createActionSwitchSearchCriteria (SearchOptions options){
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchWordPanel.setSearchOptions(options);
			}
		};
	}

	public AbstractAction createActionFindWord (SearchingDirection searchingDirection){
		return new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) {
				TextInputAndPropertyManagerForListElement textInputAndPropertyManagerForListElement
						= searchWordPanel.getTextInputAndPropertyManager();
				list.findAndHighlightRowBasedOnPropertyStartingFromHighlightedWord(
						textInputAndPropertyManagerForListElement.getPropertyManager(),
						textInputAndPropertyManagerForListElement.getPropertyManager().
								convertStringToProperty(
										textInputAndPropertyManagerForListElement.getTextComponent().
												getText()),
						searchingDirection);
			}
		};
	}



}

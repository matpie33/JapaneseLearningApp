package com.kanji.controllers;

import com.kanji.enums.SearchCriteria;
import com.kanji.listSearching.SearchOptions;
import com.kanji.listSearching.SearchingDirection;
import com.kanji.panels.SearchWordPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class SearchWordController {

	private SearchWordPanel searchWordPanel;

	public SearchWordController (SearchWordPanel panel){
		this.searchWordPanel = panel;
	}

	public AbstractAction createActionSwitchSearchingByOption (){
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox comboBox = (JComboBox) e.getSource();
				String optionLabel = (String) comboBox.getSelectedItem();
				SearchCriteria searchCriteria = SearchCriteria.findByComboboxLabel(optionLabel);
				searchWordPanel.switchToSearchingBy(searchCriteria);
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
				searchWordPanel.tryToFindNextOccurence(searchingDirection);
			}
		};
	}


}

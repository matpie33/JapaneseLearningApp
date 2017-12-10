package com.kanji.constants.enums;

import com.kanji.constants.strings.Labels;

import java.util.Arrays;

public enum SearchCriteria {

	BY_KEYWORD (Labels.COMBOBOX_OPTION_SEARCH_BY_KEYWORD, "Search by keyword panel"),
	BY_KANJI_ID(Labels.COMBOBOX_OPTION_SEARCH_BY_KANJI_ID, "Search by kanji id panel");

	private String comboboxLabel;
	private String panelName;

	private SearchCriteria(String label, String panelName){
		this.comboboxLabel = label;
		this.panelName = panelName;
	}

	public String getPanelName() {
		return panelName;
	}


	public String getComboboxLabel() {
		return comboboxLabel;
	}

	public static SearchCriteria findByComboboxLabel (String label){
		return Arrays.asList(values()).stream().filter(option-> option.getComboboxLabel().
				equals(label)).findAny().orElse(null);
	}

}

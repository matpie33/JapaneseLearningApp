package com.kanji.enums;

import com.kanji.strings.Labels;

public enum WordSearchOptions {

	BY_WORD_FRAGMENT(Labels.WORD_SEARCH_WORD_FRAGMENT), BY_WORD (Labels.WORD_SEARCH_ONLY_FULL_WORDS_OPTION),
	BY_FULL_EXPRESSION (Labels.WORD_SEARCH_PERFECT_MATCH_OPTION);

	private String panelLabel;

	private WordSearchOptions(String panelLabel){
		this.panelLabel = panelLabel;
	}

	public String getPanelLabel() {
		return panelLabel;
	}
}

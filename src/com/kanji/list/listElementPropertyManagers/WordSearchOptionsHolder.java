package com.kanji.list.listElementPropertyManagers;

import com.kanji.constants.enums.WordSearchOptions;

public class WordSearchOptionsHolder {


	private WordSearchOptions options;

	public WordSearchOptionsHolder() {
		this(WordSearchOptions.BY_WORD_FRAGMENT);
	}

	public WordSearchOptionsHolder(WordSearchOptions options) {
		this.options = options;
	}

	public WordSearchOptions getWordSearchOptions (){
		return options;
	}

	public void setWordSearchOptions (WordSearchOptions options){
		this.options = options;
	}

}

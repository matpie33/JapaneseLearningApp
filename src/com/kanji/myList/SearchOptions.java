package com.kanji.myList;

public class SearchOptions {
	
	private boolean matchByWord;
	private boolean matchByExpression;
	
	public SearchOptions (){
		matchByWord = false;
		matchByExpression = false;
	}

	public boolean isMatchByWordEnabled() {
		return matchByWord;
	}

	public void enableMatchByWordOnly() {
		matchByWord = true;
		matchByExpression = false;
	}

	public boolean isMatchByExpressionEnabled() {
		return matchByExpression;
	}

	public void enableMatchByExpressionOnly() {
		matchByExpression = true;
		matchByWord = false;
	}
	
	public void setDefaultOption(){
		matchByExpression = false;
		matchByWord = false;
	}

}

package com.kanji.myList;

import java.util.Map;

public class WordsAsMap{
	
	private Map <String, Integer> words;

	public void add(String word, int number) {
		words.put(word,number);
	}

	public void remove(String word) {
		words.remove(word);
		
	}

}

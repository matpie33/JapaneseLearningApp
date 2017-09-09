package com.kanji.myList;

import java.util.ArrayList;
import java.util.List;

public class ListWordsController<WordsType> {
	private static final long serialVersionUID = -3144332338336535803L;
	private List<WordsType> wordsList;

	public ListWordsController() {
		wordsList = new ArrayList<WordsType>();
	}

	public boolean add(WordsType r) {
		if (!wordsList.contains(r)) {
			wordsList.add(r);
			return true;
		}
		return false;

	}

	public int remove(WordsType r) {
		int index = wordsList.indexOf(r);
		wordsList.remove(r);
		return index;
	}

	public List<WordsType> getWords() {
		return wordsList;
	}

	public int getNumberOfWords() {
		return wordsList.size();
	}

	public void replace(WordsType wordToReplace, WordsType newWord) {
		getWords().set(getWords().indexOf(wordToReplace), newWord);
	}

	public WordsType getWordInRow(int rowNumber1Based) {
		return wordsList.get(rowNumber1Based);
	}

}

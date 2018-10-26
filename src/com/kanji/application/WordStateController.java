package com.kanji.application;

import com.guimaker.list.ListElement;
import com.guimaker.list.myList.MyList;
import com.kanji.list.listElements.RepeatingData;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.panelsAndControllers.controllers.RepeatingWordsController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WordStateController<Word extends ListElement> {

	private RepeatingWordsController<Word> repeatingWordsController;
	private ProblematicWordsController<Word> problematicWordsController;
	private Set<Word> problematicWords;
	private MyList<Word> wordList;
	private MyList<RepeatingData> repeatingData;

	public WordStateController(
			RepeatingWordsController<Word> repeatingWordsController,
			ProblematicWordsController<Word> problematicWordsController) {
		this.repeatingWordsController = repeatingWordsController;
		this.problematicWordsController = problematicWordsController;
		problematicWordsController.initialize();
		problematicWords = new HashSet<>();
	}

	public RepeatingWordsController<Word> getRepeatingWordsController() {
		return repeatingWordsController;
	}

	public ProblematicWordsController<Word> getProblematicWordsController() {
		return problematicWordsController;
	}

	public Set<Word> getProblematicWords() {
		return problematicWords;
	}

	public void setProblematicWords(Set<Word> problematicWords) {
		this.problematicWords.clear();
		this.problematicWords.addAll(problematicWords);
	}

	public void setWords(MyList<Word> words) {
		this.wordList = words;
	}

	public void setWords(List<Word> words) {
		words.forEach(wordList::addWord);
	}

	public void setRepeatingDates(List<RepeatingData> repeatingDates) {
		repeatingDates.forEach(repeatingData::addWord);
	}

	public void setRepeatingDates(MyList<RepeatingData> repeatingDates) {
		this.repeatingData = repeatingDates;
	}

	public void clearData() {
		wordList.cleanWords();
		repeatingData.cleanWords();
	}

	public MyList<Word> getWords() {
		return wordList;
	}

	public MyList<RepeatingData> getRepeatingList() {
		return repeatingData;
	}
}

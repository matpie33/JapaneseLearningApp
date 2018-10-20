package com.kanji.application;

import com.guimaker.list.ListElement;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.panelsAndControllers.controllers.RepeatingWordsController;

import java.util.Set;

public class WordStateController<Word extends ListElement> {

	private RepeatingWordsController<Word> repeatingWordsController;
	private ProblematicWordsController<Word> problematicWordsController;
	private Set<Word> problematicWords;

	public WordStateController(
			RepeatingWordsController<Word> repeatingWordsController,
			ProblematicWordsController<Word> problematicWordsController) {
		this.repeatingWordsController = repeatingWordsController;
		this.problematicWordsController = problematicWordsController;
		problematicWordsController.initialize();
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
}

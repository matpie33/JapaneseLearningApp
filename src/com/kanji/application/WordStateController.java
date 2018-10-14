package com.kanji.application;

import com.kanji.list.listElements.ListElement;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.panelsAndControllers.controllers.RepeatingWordsController;
import com.kanji.panelsAndControllers.controllers.WordSpecificRepeatingController;
import com.kanji.problematicWords.ProblematicWordsDisplayer;
import com.kanji.windows.ApplicationWindow;

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

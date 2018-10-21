package com.kanji.application;

import com.guimaker.list.myList.MyList;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.Kanji;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.panelsAndControllers.controllers.RepeatingWordsController;
import com.kanji.panelsAndControllers.controllers.WordSpecificRepeatingController;
import com.kanji.problematicWords.ProblematicJapaneseWordsDisplayer;
import com.kanji.problematicWords.ProblematicKanjiDisplayer;
import com.kanji.repeating.RepeatingJapaneseWordsDisplayer;
import com.kanji.repeating.RepeatingKanjiDisplayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ApplicationStateController {

	private Map<String, WordStateController> wordStateControllerByMeaningfulNameMap = new HashMap<>();
	private String activeWordsControllerKey;
	private ListTestDataCreator listTestDataCreator;

	public ApplicationStateController(
			ApplicationController applicationController) {
		listTestDataCreator = new ListTestDataCreator(applicationController);
	}

	public void initialize(ApplicationController applicationController) {
		initializeKanjiControllers(applicationController);
		initializeJapaneseWordsControllers(applicationController);
		listTestDataCreator.initializeTestData();
	}

	private void initializeJapaneseWordsControllers(
			ApplicationController applicationController) {
		RepeatingJapaneseWordsDisplayer repeatingJapaneseWordsDisplayer = new RepeatingJapaneseWordsDisplayer(
				applicationController);
		MyList<JapaneseWord> japaneseWords = listTestDataCreator
				.initializeJapaneseWordsList();
		WordSpecificRepeatingController<JapaneseWord> japaneseWordSpecificController = new WordSpecificRepeatingController<>(
				japaneseWords, repeatingJapaneseWordsDisplayer);
		RepeatingWordsController<JapaneseWord> repeatingJapaneseWordsController = new RepeatingWordsController<>(
				applicationController, japaneseWordSpecificController);
		ProblematicJapaneseWordsDisplayer problematicJapaneseWordsDisplayer = new ProblematicJapaneseWordsDisplayer(
				applicationController);

		ProblematicWordsController<JapaneseWord> controller = problematicJapaneseWordsDisplayer
				.getController();
		japaneseWords.addListObserver(controller);

		WordStateController<JapaneseWord> japaneseWordsController = new WordStateController<>(
				repeatingJapaneseWordsController, controller);
		japaneseWordsController.setRepeatingDates(listTestDataCreator
				.initializeJapaneseWordsRepeatingData());
		japaneseWordsController.setWords(japaneseWords);
		wordStateControllerByMeaningfulNameMap
				.put(JapaneseWord.MEANINGFUL_NAME, japaneseWordsController);
	}

	private void initializeKanjiControllers(
			ApplicationController applicationController) {
		RepeatingKanjiDisplayer repeatingKanjiDisplayer = new RepeatingKanjiDisplayer();
		MyList<Kanji> kanjiList = listTestDataCreator.initializeKanjiList();
		WordSpecificRepeatingController<Kanji> kanjiSpecificController = new WordSpecificRepeatingController<>(
				kanjiList, repeatingKanjiDisplayer);
		RepeatingWordsController<Kanji> repeatingKanjiController = new RepeatingWordsController<>(
				applicationController, kanjiSpecificController);

		ProblematicKanjiDisplayer problematicKanjiDisplayer = new ProblematicKanjiDisplayer(
				applicationController);
		ProblematicWordsController<Kanji> problematicKanjiController = problematicKanjiDisplayer
				.getProblematicWordsController();
		kanjiList.addListObserver(problematicKanjiController);

		WordStateController<Kanji> kanjiController = new WordStateController<>(
				repeatingKanjiController, problematicKanjiController);
		kanjiController.setRepeatingDates(
				listTestDataCreator.initializeKanjiRepeatingList());
		kanjiController.setWords(kanjiList);
		wordStateControllerByMeaningfulNameMap
				.put(Kanji.MEANINGFUL_NAME, kanjiController);
	}

	public WordStateController getController(String meaningfulName) {
		return wordStateControllerByMeaningfulNameMap.get(meaningfulName);
	}

	public Set getProblematicWordsForActiveTab() {
		return wordStateControllerByMeaningfulNameMap
				.get(activeWordsControllerKey).getProblematicWords();
	}

	public void setActiveWordStateControllerKey(
			String activeWordStateControllerKey) {
		this.activeWordsControllerKey = activeWordStateControllerKey;
	}

	public ProblematicWordsController getActiveProblematicWordsController() {
		return wordStateControllerByMeaningfulNameMap
				.get(activeWordsControllerKey).getProblematicWordsController();
	}

	public RepeatingWordsController getActiveRepeatingWordsController() {
		return wordStateControllerByMeaningfulNameMap
				.get(activeWordsControllerKey).getRepeatingWordsController();
	}
}

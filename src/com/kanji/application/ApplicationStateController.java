package com.kanji.application;

import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.Kanji;
import com.guimaker.list.myList.MyList;
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

public class ApplicationStateController {

	private Map<String, WordStateController> wordStateControllerByMeaningfulNameMap = new HashMap<>();

	public void initialize(ApplicationController applicationController) {
		initializeKanjiControllers(applicationController);
		initializeJapaneseWordsControllers(applicationController);
	}

	private void initializeJapaneseWordsControllers(
			ApplicationController applicationController) {
		RepeatingJapaneseWordsDisplayer repeatingJapaneseWordsDisplayer = new RepeatingJapaneseWordsDisplayer(
				applicationController);
		MyList<JapaneseWord> japaneseWords = applicationController
				.getJapaneseWords();
		WordSpecificRepeatingController<JapaneseWord> japaneseWordSpecificController = new WordSpecificRepeatingController<>(
				japaneseWords, repeatingJapaneseWordsDisplayer);
		RepeatingWordsController<JapaneseWord> repeatingJapaneseWordsController =
				new RepeatingWordsController<>(
				applicationController,
				japaneseWordSpecificController);
		ProblematicJapaneseWordsDisplayer problematicJapaneseWordsDisplayer = new ProblematicJapaneseWordsDisplayer(
				applicationController);

		ProblematicWordsController<JapaneseWord> controller = problematicJapaneseWordsDisplayer
				.getController();
		japaneseWords.addListObserver(controller);

		wordStateControllerByMeaningfulNameMap.put(JapaneseWord.MEANINGFUL_NAME,
				new WordStateController<>(repeatingJapaneseWordsController,
						controller));
	}

	private void initializeKanjiControllers(
			ApplicationController applicationController) {
		RepeatingKanjiDisplayer repeatingKanjiDisplayer = new RepeatingKanjiDisplayer();
		MyList<Kanji> kanjiList = applicationController.getKanjiList();
		WordSpecificRepeatingController<Kanji> kanjiSpecificController = new WordSpecificRepeatingController<>(
				kanjiList, repeatingKanjiDisplayer);
		RepeatingWordsController<Kanji> repeatingKanjiController = new RepeatingWordsController<>(
				applicationController,
				kanjiSpecificController);

		ProblematicKanjiDisplayer problematicKanjiDisplayer = new ProblematicKanjiDisplayer(
				applicationController);
		ProblematicWordsController<Kanji> problematicKanjiController = problematicKanjiDisplayer
				.getProblematicWordsController();
		kanjiList.addListObserver(problematicKanjiController);

		wordStateControllerByMeaningfulNameMap.put(Kanji.MEANINGFUL_NAME,
				new WordStateController<>(repeatingKanjiController,
						problematicKanjiController));
	}

	public WordStateController getController(String meaningfulName) {
		return wordStateControllerByMeaningfulNameMap.get(meaningfulName);
	}

}

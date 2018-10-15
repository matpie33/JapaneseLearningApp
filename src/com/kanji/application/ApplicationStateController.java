package com.kanji.application;

import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.panelsAndControllers.controllers.RepeatingWordsController;
import com.kanji.panelsAndControllers.controllers.WordSpecificRepeatingController;
import com.kanji.problematicWords.ProblematicJapaneseWordsDisplayer;
import com.kanji.problematicWords.ProblematicKanjiDisplayer;
import com.kanji.repeating.RepeatingJapaneseWordsDisplayer;
import com.kanji.repeating.RepeatingKanjiDisplayer;
import com.kanji.windows.ApplicationWindow;

import java.util.HashMap;
import java.util.Map;

public class ApplicationStateController {

	private Map<String, WordStateController> wordStateControllerByMeaningfulNameMap = new HashMap<>();

	public void initialize(ApplicationWindow applicationWindow) {
		initializeKanjiControllers(applicationWindow);
		initializeJapaneseWordsControllers(applicationWindow);
	}

	private void initializeJapaneseWordsControllers(
			ApplicationWindow applicationWindow) {
		RepeatingJapaneseWordsDisplayer repeatingJapaneseWordsDisplayer = new RepeatingJapaneseWordsDisplayer(
				applicationWindow);
		MyList<JapaneseWord> japaneseWords = applicationWindow
				.getApplicationController().getJapaneseWords();
		WordSpecificRepeatingController<JapaneseWord> japaneseWordSpecificController = new WordSpecificRepeatingController<>(
				japaneseWords, repeatingJapaneseWordsDisplayer);
		RepeatingWordsController<JapaneseWord> repeatingJapaneseWordsController = new RepeatingWordsController<>(
				applicationWindow, japaneseWordSpecificController);
		ProblematicJapaneseWordsDisplayer problematicJapaneseWordsDisplayer = new ProblematicJapaneseWordsDisplayer(
				applicationWindow);

		ProblematicWordsController<JapaneseWord> controller = problematicJapaneseWordsDisplayer
				.getController();
		japaneseWords.addListObserver(controller);

		wordStateControllerByMeaningfulNameMap.put(JapaneseWord.MEANINGFUL_NAME,
				new WordStateController<>(repeatingJapaneseWordsController,
						controller));
	}

	private void initializeKanjiControllers(
			ApplicationWindow applicationWindow) {
		RepeatingKanjiDisplayer repeatingKanjiDisplayer = new RepeatingKanjiDisplayer();
		MyList<Kanji> kanjiList = applicationWindow.getApplicationController()
				.getKanjiList();
		WordSpecificRepeatingController<Kanji> kanjiSpecificController = new WordSpecificRepeatingController<>(
				kanjiList, repeatingKanjiDisplayer);
		RepeatingWordsController<Kanji> repeatingKanjiController = new RepeatingWordsController<>(
				applicationWindow, kanjiSpecificController);

		ProblematicKanjiDisplayer problematicKanjiDisplayer = new ProblematicKanjiDisplayer(
				applicationWindow);
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

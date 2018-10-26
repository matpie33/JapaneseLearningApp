package com.kanji.repeating;

import com.guimaker.enums.InputGoal;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.panels.MainPanel;
import com.guimaker.utilities.CommonListElements;
import com.guimaker.utilities.StringUtilities;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listRows.japanesePanelCreatingComponents.JapaneseWordPanelCreator;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.utilities.KanjiCharactersReader;

import java.awt.*;
import java.util.*;
import java.util.function.Function;

public class RepeatingJapaneseWordsDisplayer
		implements RepeatingWordsDisplayer<JapaneseWord> {

	private KanjiCharactersReader kanjiCharactersReader;
	private Map<Integer, Function<JapaneseWord, Set<String>>> hintTypeIntValues;
	private JapaneseWordPanelCreator japaneseWordPanelCreator;
	private String UNIQUE_NAME = "Repeating japanese words";

	public RepeatingJapaneseWordsDisplayer(
			ApplicationController applicationController) {
		japaneseWordPanelCreator = new JapaneseWordPanelCreator(
				applicationController,
				applicationController.getApplicationWindow(),
				PanelDisplayMode.VIEW);
		kanjiCharactersReader = KanjiCharactersReader.getInstance();
		kanjiCharactersReader.loadKanjisIfNeeded();
		//TODO kanjis can be loaded just once in the "get instance" method
		initializeHintTypeValues();
		this.japaneseWordPanelCreator = japaneseWordPanelCreator;
	}

	private void initializeHintTypeValues() {
		hintTypeIntValues = new HashMap<>();
		hintTypeIntValues.put(1, japaneseWordInformation -> new HashSet<>(
				Collections.singletonList(
						japaneseWordInformation.getMeaning())));
		hintTypeIntValues.put(2, JapaneseWord::getKanaWritings);
		hintTypeIntValues.put(3, JapaneseWord::getKanjiWritings);

	}

	@Override
	public void showFullWordDetailsPanel(JapaneseWord japaneseWord,
			MainPanel wordAssessmentPanel) {
		wordAssessmentPanel.clear();
		japaneseWordPanelCreator.addJapanesePanelToExistingPanel(
				wordAssessmentPanel, japaneseWord, InputGoal.NO_INPUT,
				CommonListElements.forSingleRowOnly(Color.WHITE), true);
	}

	@Override
	public String getUniqueName() {
		return UNIQUE_NAME;
	}

	@Override
	public String getWordHint(JapaneseWord kanjiInformation) {
		int possibilitiesAmount;
		if (kanjiInformation.hasKanjiWriting()) {
			possibilitiesAmount = 3;
		}
		else {
			possibilitiesAmount = 2;
		}
		Random random = new Random();
		int randomNumber = random.nextInt(possibilitiesAmount) + 1;
		Function<JapaneseWord, Set<String>> hintGetter = hintTypeIntValues.get(
				randomNumber);
		return StringUtilities.concatenateStrings(
				new ArrayList<>((hintGetter.apply(kanjiInformation))));
	}

}

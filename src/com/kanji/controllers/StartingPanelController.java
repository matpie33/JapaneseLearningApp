package com.kanji.controllers;

import java.util.HashSet;
import java.util.Set;

import com.kanji.Row.RepeatingInformation;
import com.kanji.range.SetOfRanges;
import com.kanji.utilities.ElementMaker;

public class StartingPanelController {

	private RepeatingWordsController repeatingWordsController;
	private ElementMaker maker;
	private Set<Integer> problematicKanjis;

	// TODO merge it with repeating controller?

	public StartingPanelController(ElementMaker maker,
			RepeatingWordsController repeatingWordsPanel) {
		problematicKanjis = new HashSet<Integer>();
		this.maker = maker;
		this.repeatingWordsController = repeatingWordsPanel;
	}

	public void setWordsRangeToRepeat(SetOfRanges ranges, boolean withProblematic) {

		repeatingWordsController.setRepeatingWords(maker.getWordsList());
		// TODO if set of ranges is empty, we should not call set ranges to
		// repeat all, so probably
		// split this method
		repeatingWordsController.setRangesToRepeat(ranges);
		repeatingWordsController.reset();
		System.out.println("setting: " + problematicKanjis);
		repeatingWordsController.setProblematicKanjis(problematicKanjis);
		if (withProblematic) {
			repeatingWordsController.addProblematicKanjisToList();
		}

		repeatingWordsController.startRepeating();
	}

	public void setRepeatingInformation(RepeatingInformation info) {
		repeatingWordsController.setRepeatingInformation(info);
	}

	public void addProblematicKanjis(Set<Integer> problematicKanjiList) {
		problematicKanjis = problematicKanjiList;
	}

	public Set<Integer> getProblematicKanjis() {
		return problematicKanjis;
	}

	public void addToRepeatsList(RepeatingInformation info) {
		maker.getRepeatsList().addWord(info);
	}

}

package com.kanji.controllers;

import java.util.HashSet;
import java.util.Set;

import com.kanji.Row.RepeatingInformation;
import com.kanji.panels.RepeatingWordsController;
import com.kanji.range.SetOfRanges;
import com.kanji.utilities.ElementMaker;

public class StartingPanelController {

	private RepeatingWordsController repeatingWordsPanel;
	private ElementMaker maker;
	private Set<Integer> problematicKanjis;

	// TODO merge it with repeating controller?

	public StartingPanelController(ElementMaker maker, RepeatingWordsController repeatingWordsPanel) {
		problematicKanjis = new HashSet<Integer>();
		this.maker = maker;
		this.repeatingWordsPanel = repeatingWordsPanel;
	}

	public void setWordsRangeToRepeat(SetOfRanges ranges, boolean withProblematic) {

		repeatingWordsPanel.setRepeatingWords(maker.getWordsList());
		// TODO if set of ranges is empty, we should not call set ranges to
		// repeat all, so probably
		// split this method
		repeatingWordsPanel.setRangesToRepeat(ranges);
		repeatingWordsPanel.reset();
		System.out.println("setting: " + problematicKanjis);
		if (withProblematic)
			repeatingWordsPanel.setProblematicKanjis(problematicKanjis);

		repeatingWordsPanel.startRepeating();
	}

	public void setRepeatingInformation(RepeatingInformation info) {
		repeatingWordsPanel.setRepeatingInformation(info);
	}

	public void addProblematicKanjis(Set<Integer> problematicKanjiList) {
		this.problematicKanjis.addAll(problematicKanjiList);
	}

	public Set<Integer> getProblematicKanjis() {
		return problematicKanjis;
	}

	public void addToRepeatsList(RepeatingInformation info) {
		maker.getRepeatsList().getWords().add(info);
	}

}

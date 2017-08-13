package com.kanji.utilities;

import java.util.Set;

import com.kanji.Row.KanjiWords;
import com.kanji.Row.RepeatingList;

public class SavingInformation {
	private KanjiWords kanjiWords;
	private RepeatingList repeatingList;
	private Set<Integer> problematicKanjis;

	public SavingInformation(KanjiWords kanjiWords, RepeatingList repeatingList,
			Set<Integer> problematicKanjis) {
		this.kanjiWords = kanjiWords;
		this.repeatingList = repeatingList;
		this.problematicKanjis = problematicKanjis;
	}

	public KanjiWords getKanjiWords() {
		return kanjiWords;
	}

	public RepeatingList getRepeatingList() {
		return repeatingList;
	}

	public Set<Integer> getProblematicKanjis() {
		return problematicKanjis;
	}

}

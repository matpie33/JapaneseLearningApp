package com.kanji.utilities;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.kanji.Row.KanjiInformation;
import com.kanji.Row.RepeatingInformation;

public class SavingInformation implements Serializable {
	private List<KanjiInformation> kanjiWords;
	private List<RepeatingInformation> repeatingList;
	private Set<Integer> problematicKanjis;

	public SavingInformation(List<KanjiInformation> kanjiWords,
			List<RepeatingInformation> repeatingList, Set<Integer> problematicKanjis) {
		this.kanjiWords = kanjiWords;
		this.repeatingList = repeatingList;
		this.problematicKanjis = problematicKanjis;
	}

	public List<KanjiInformation> getKanjiWords() {
		return kanjiWords;
	}

	public List<RepeatingInformation> getRepeatingList() {
		return repeatingList;
	}

	public Set<Integer> getProblematicKanjis() {
		return problematicKanjis;
	}

}

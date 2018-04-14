package com.kanji.model;

import com.kanji.list.listElements.Kanji;
import com.kanji.list.listElements.RepeatingData;

import java.util.List;
import java.util.Set;

public class KanjisAndRepeatingInfo {

	private List<Kanji> kanjis;
	private List<RepeatingData> repeatingData;
	private Set<Integer> problematicKanjis;

	public KanjisAndRepeatingInfo(List<Kanji> kanjis,
			List<RepeatingData> repeatingData,
			Set<Integer> problematicKanjis) {
		this.kanjis = kanjis;
		this.repeatingData = repeatingData;
		this.problematicKanjis = problematicKanjis;

	}

	public List<Kanji> getKanjis() {
		return kanjis;
	}

	public List<RepeatingData> getRepeatingData() {
		return repeatingData;
	}

	public Set<Integer> getProblematicKanjis() {
		return problematicKanjis;
	}

}

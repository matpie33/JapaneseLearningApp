package com.kanji.model;

import com.kanji.list.listElements.RepeatingData;

import java.util.List;
import java.util.Set;

public class WordsAndRepeatingInfo<WordType, ProblematicWordType> {

	private List<WordType> words;
	private List<RepeatingData> repeatingInformations;
	private Set<ProblematicWordType> problematicWords;

	public WordsAndRepeatingInfo(List<WordType> words,
			List<RepeatingData> repeatingInformations,
			Set<ProblematicWordType> problematicWords) {
		this.words = words;
		this.repeatingInformations = repeatingInformations;
		this.problematicWords = problematicWords;

	}

	public List<WordType> getWords() {
		return words;
	}

	public List<RepeatingData> getRepeatingInformations() {
		return repeatingInformations;
	}

	public Set<ProblematicWordType> getProblematicWords() {
		return problematicWords;
	}

}

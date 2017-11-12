package com.kanji.saving;

import com.kanji.listElements.RepeatingInformation;
import com.kanji.timer.TimeRepresentation;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class KanjiRepeatingState implements Serializable{

	private static final long serialVersionUID = -917684305611193624L;
	private Set<Integer> currentProblematicKanjis;
	private List<String> currentlyRepeatedWords;
	private RepeatingInformation repeatingInformation;
	private TimeRepresentation timeRepresentation;

	public KanjiRepeatingState(Set<Integer> currentProblematicKanjis, List <String> currentlyRepeatedWords,
			RepeatingInformation repeatingInformation, TimeRepresentation timeRepresentation){
		this.currentlyRepeatedWords = currentlyRepeatedWords;
		this.currentProblematicKanjis = currentProblematicKanjis;
		this.repeatingInformation = repeatingInformation;
		this.timeRepresentation = timeRepresentation;
	}

	public Set<Integer> getCurrentProblematicKanjis() {
		return currentProblematicKanjis;
	}

	public List<String> getCurrentlyRepeatedWords() {
		return currentlyRepeatedWords;
	}

	public RepeatingInformation getRepeatingInformation() {
		return repeatingInformation;
	}

	public TimeRepresentation getTimeRepresentation() {
		return timeRepresentation;
	}
}

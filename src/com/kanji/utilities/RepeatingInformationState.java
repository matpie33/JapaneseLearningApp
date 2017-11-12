package com.kanji.utilities;

import com.kanji.Row.RepeatingInformation;
import com.kanji.timer.TimeRepresentation;

import java.io.Serializable;
import java.sql.Time;
import java.util.List;
import java.util.Set;

public class RepeatingInformationState implements Serializable{

	private static final long serialVersionUID = -917684305611193624L;
	//TODO fix naming - repeating state and repeating information state
	private Set<Integer> currentProblematicKanjis;
	private List<String> currentlyRepeatedWords;
	private RepeatingInformation repeatingInformation;
	private TimeRepresentation timeRepresentation;

	public RepeatingInformationState (Set<Integer> currentProblematicKanjis, List <String> currentlyRepeatedWords,
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

	public void setRepeatingInformation(RepeatingInformation repeatingInformation) {
		this.repeatingInformation = repeatingInformation;
	}

	public TimeRepresentation getTimeRepresentation() {
		return timeRepresentation;
	}
}

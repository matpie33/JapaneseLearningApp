package com.kanji.saving;

import com.kanji.list.listElements.ListElement;
import com.kanji.list.listElements.RepeatingInformation;
import com.kanji.timer.TimeSpent;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class RepeatingState <Element extends ListElement> implements Serializable {

	private TimeSpent timeSpent;
	private RepeatingInformation repeatingInformation;
	private Set <Element> currentProblematicWords;
	private Set <Element> currentlyRepeatedWords;

	public RepeatingState(TimeSpent timeSpent, RepeatingInformation repeatingInformation,
			Set<Element> currentProblematicWords, Set<Element> currentlyRepeatedWords) {
		this.timeSpent = timeSpent;
		this.repeatingInformation = repeatingInformation;
		this.currentProblematicWords = currentProblematicWords;
		this.currentlyRepeatedWords = currentlyRepeatedWords;
	}

	public TimeSpent getTimeSpent() {
		return timeSpent;
	}

	public RepeatingInformation getRepeatingInformation() {
		return repeatingInformation;
	}

	public Set<Element> getCurrentProblematicWords() {
		return currentProblematicWords;
	}

	public Set<Element> getCurrentlyRepeatedWords() {
		return currentlyRepeatedWords;
	}
}

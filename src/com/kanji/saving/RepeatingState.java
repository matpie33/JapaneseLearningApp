package com.kanji.saving;

import com.kanji.list.listElements.ListElement;
import com.kanji.list.listElements.RepeatingData;
import com.kanji.timer.TimeSpent;

import java.io.Serializable;
import java.util.Set;

public class RepeatingState<Element extends ListElement>
		implements Serializable {

	private TimeSpent timeSpent;
	private RepeatingData repeatingData;
	private Set<Element> currentProblematicWords;
	private Set<Element> currentlyRepeatedWords;

	public RepeatingState(TimeSpent timeSpent, RepeatingData repeatingData,
			Set<Element> currentProblematicWords,
			Set<Element> currentlyRepeatedWords) {
		this.timeSpent = timeSpent;
		this.repeatingData = repeatingData;
		this.currentProblematicWords = currentProblematicWords;
		this.currentlyRepeatedWords = currentlyRepeatedWords;
	}

	public TimeSpent getTimeSpent() {
		return timeSpent;
	}

	public RepeatingData getRepeatingData() {
		return repeatingData;
	}

	public Set<Element> getCurrentProblematicWords() {
		return currentProblematicWords;
	}

	public Set<Element> getCurrentlyRepeatedWords() {
		return currentlyRepeatedWords;
	}
}

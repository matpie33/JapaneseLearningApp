package com.kanji.saving;

import com.guimaker.list.ListElement;
import com.guimaker.timer.TimeSpent;
import com.kanji.constants.enums.TypeOfWordForRepeating;
import com.kanji.list.listElements.RepeatingData;

import java.io.Serializable;
import java.util.Set;

public class RepeatingState<Element extends ListElement>
		implements Serializable {

	private static final long serialVersionUID = -3132496217400475244L;
	private TimeSpent timeSpent;
	private RepeatingData repeatingData;
	private Set<Element> currentProblematicWords;
	private Set<Element> currentlyRepeatedWords;
	private TypeOfWordForRepeating typeOfWordForRepeating;

	public RepeatingState(TimeSpent timeSpent, RepeatingData repeatingData,
			Set<Element> currentProblematicWords,
			Set<Element> currentlyRepeatedWords,
			TypeOfWordForRepeating typeOfWordForRepeating) {
		this.timeSpent = timeSpent;
		this.repeatingData = repeatingData;
		this.currentProblematicWords = currentProblematicWords;
		this.currentlyRepeatedWords = currentlyRepeatedWords;
		this.typeOfWordForRepeating = typeOfWordForRepeating;
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

	public TypeOfWordForRepeating getTypeOfWordForRepeating() {
		return typeOfWordForRepeating;
	}
}

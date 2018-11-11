package com.kanji.model.saving;

import com.guimaker.list.ListElement;

import java.io.Serializable;
import java.util.List;

public class ProblematicWordsState<Element extends ListElement>
		implements Serializable {

	private static final long serialVersionUID = -1667110131673682347L;
	private List<Element> repeatedWords;
	private List<Element> notRepeatedWords;

	public ProblematicWordsState(List<Element> repeatedWords,
			List<Element> notRepeatedWords) {
		this.repeatedWords = repeatedWords;
		this.notRepeatedWords = notRepeatedWords;
	}

	public List<? extends ListElement> getReviewedWords() {
		return repeatedWords;
	}

	public List<? extends ListElement> getNotReviewedWords() {
		return notRepeatedWords;
	}
}

package com.kanji.saving;

import com.kanji.list.listElements.KanjiInformation;
import com.kanji.list.listElements.ListElement;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ProblematicKanjisState implements Serializable {

	private static final long serialVersionUID = -1667110131673682347L;
	private Map<KanjiInformation, Boolean> problematicKanjis;
	private List <? extends ListElement> repeatedWords;
	private List <? extends ListElement> notRepeatedWords;

	public ProblematicKanjisState(List <? extends ListElement> repeatedWords,
			List <? extends ListElement> notRepeatedWords){
		this.repeatedWords = repeatedWords;
		this.notRepeatedWords = notRepeatedWords;
	}

	public List<? extends ListElement> getReviewedKanjis() {
		return repeatedWords;
	}

	public List<? extends ListElement> getNotReviewKanjis() {
		return notRepeatedWords;
	}
}

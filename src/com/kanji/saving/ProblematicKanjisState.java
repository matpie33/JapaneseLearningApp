package com.kanji.saving;

import com.kanji.listElements.KanjiInformation;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ProblematicKanjisState implements Serializable {

	private static final long serialVersionUID = -1667110131673682347L;
	private Map<KanjiInformation, Boolean> problematicKanjis;
	private List <KanjiInformation> repeatedWords;
	private List <KanjiInformation> notRepeatedWords;

	public ProblematicKanjisState(List <KanjiInformation> repeatedWords,
			List <KanjiInformation> notRepeatedWords){
		this.repeatedWords = repeatedWords;
		this.notRepeatedWords = notRepeatedWords;
	}

	public List<KanjiInformation> getReviewedKanjis() {
		return repeatedWords;
	}

	public List<KanjiInformation> getNotReviewKanjis() {
		return notRepeatedWords;
	}
}

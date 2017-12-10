package com.kanji.model;

import com.kanji.list.listElements.KanjiInformation;
import com.kanji.list.listElements.RepeatingInformation;

import java.util.List;
import java.util.Set;

public class KanjisAndRepeatingInfo {

	private List <KanjiInformation> kanjiInformations;
	private List <RepeatingInformation> repeatingInformations;
	private Set<Integer> problematicKanjis;

	public KanjisAndRepeatingInfo (List <KanjiInformation> kanjiInformations, List <RepeatingInformation> repeatingInformations, Set<Integer> problematicKanjis){
		this.kanjiInformations = kanjiInformations;
		this.repeatingInformations = repeatingInformations;
		this.problematicKanjis = problematicKanjis;

	}

	public List <KanjiInformation> getKanjiInformations (){
		return kanjiInformations;
	}

	public List <RepeatingInformation> getRepeatingInformations (){
		return repeatingInformations;
	}

	public Set<Integer> getProblematicKanjis() {
		return problematicKanjis;
	}

}

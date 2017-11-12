package com.kanji.utilities;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.kanji.Row.KanjiInformation;
import com.kanji.Row.RepeatingInformation;
import com.kanji.model.ProblematicKanjisState;

public class SavingInformation implements Serializable {
	private final static long serialVersionUID = -8017224611162128282L;
	private List<KanjiInformation> kanjiWords;
	private List<RepeatingInformation> repeatingList;
	private Set<Integer> problematicKanjis;
	private RepeatingInformationState repeatingInformationState;
	private ProblematicKanjisState problematicKanjisState;

	public SavingInformation(List<KanjiInformation> kanjiWords,
			List<RepeatingInformation> repeatingList, Set<Integer> problematicKanjis) {
		this.kanjiWords = kanjiWords;
		this.repeatingList = repeatingList;
		this.problematicKanjis = problematicKanjis;
	}

	public List<KanjiInformation> getKanjiWords() {
		return kanjiWords;
	}

	public List<RepeatingInformation> getRepeatingList() {
		return repeatingList;
	}

	public Set<Integer> getProblematicKanjis() {
		return problematicKanjis;
	}

	public boolean hasRepeatingInformationState (){
		return repeatingInformationState != null;
	}

	public boolean hasProblematicKanjiState (){
		return problematicKanjisState != null;
	}

	public RepeatingInformationState getRepeatingInformationState() {
		return repeatingInformationState;
	}

	public void setRepeatingInformationState (RepeatingInformationState state){
		this.repeatingInformationState = state;
	}

	public ProblematicKanjisState getProblematicKanjisState() {
		return problematicKanjisState;
	}

	public void setProblematicKanjisState(
			ProblematicKanjisState problematicKanjisState) {
		this.problematicKanjisState = problematicKanjisState;
	}
}

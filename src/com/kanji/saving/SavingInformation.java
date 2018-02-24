package com.kanji.saving;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.kanji.enums.ApplicationSaveableState;
import com.kanji.listElements.KanjiInformation;
import com.kanji.listElements.RepeatingInformation;

public class SavingInformation implements Serializable {
	private final static long serialVersionUID = -8017224611162128282L;
	private List<KanjiInformation> kanjiWords;
	private List<RepeatingInformation> repeatingList;
	private Set<Integer> problematicKanjis;
	private KanjiRepeatingState kanjiRepeatingState;
	private ProblematicKanjisState problematicKanjisState;
	private ApplicationSaveableState applicationSaveableState;
	private String kanjiKoohiCookiesHeader;


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

	public KanjiRepeatingState getKanjiRepeatingState() {
		return kanjiRepeatingState;
	}

	public void setKanjiRepeatingState(KanjiRepeatingState state){
		this.kanjiRepeatingState = state;
		applicationSaveableState = ApplicationSaveableState.REPEATING_WORDS;
	}

	public ProblematicKanjisState getProblematicKanjisState() {
		return problematicKanjisState;
	}

	public void setProblematicKanjisState(
			ProblematicKanjisState problematicKanjisState) {
		this.problematicKanjisState = problematicKanjisState;
		applicationSaveableState = ApplicationSaveableState.REVIEWING_PROBLEMATIC_KANJIS;
	}

	public ApplicationSaveableState getApplicationSaveableState (){
		return applicationSaveableState;
	}

	public boolean hasStateToRestore(){
		return applicationSaveableState != null;
	}

	public void setKanjiKoohiiCookiesHeader(String kanjiKoohiCookiesHeader){
		this.kanjiKoohiCookiesHeader = kanjiKoohiCookiesHeader;
	}

	public String getKanjiKoohiiCookiesHeader(){
		return kanjiKoohiCookiesHeader;
	}

}

package com.kanji.saving;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.kanji.constants.enums.ApplicationSaveableState;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listElements.KanjiInformation;
import com.kanji.list.listElements.RepeatingInformation;

public class SavingInformation implements Serializable {
	private final static long serialVersionUID = -8017224611162128282L;
	private List<KanjiInformation> kanjiWords;
	private List<RepeatingInformation> repeatingList;
	private Set<Integer> problematicKanjis;
	private KanjiRepeatingState kanjiRepeatingState;
	private ProblematicKanjisState problematicKanjisState;
	private ApplicationSaveableState applicationSaveableState;
	private List <String> kanjiKoohiCookiesHeaders;
	private List <JapaneseWordInformation> japaneseWordInformations;
	private List <RepeatingInformation> japaneseWordsRepeatingLists;

	public SavingInformation(List<KanjiInformation> kanjiWords,
			List<RepeatingInformation> repeatingList, Set<Integer> problematicKanjis,
			List <JapaneseWordInformation> japaneseWordInformations, List <RepeatingInformation>
			japaneseWordsRepeatingLists) {
		this.kanjiWords = kanjiWords;
		this.repeatingList = repeatingList;
		this.problematicKanjis = problematicKanjis;
		this.japaneseWordInformations = japaneseWordInformations;
		this.japaneseWordsRepeatingLists = japaneseWordsRepeatingLists;
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

	public void setKanjiKoohiCookiesHeaders (List <String> kanjiKoohiCookiesHeaders){
		this.kanjiKoohiCookiesHeaders = kanjiKoohiCookiesHeaders;
	}

	public List<String> getKanjiKoohiCookiesHeaders(){
		System.out.println(kanjiKoohiCookiesHeaders);
		return kanjiKoohiCookiesHeaders;
	}

	public List <JapaneseWordInformation> getJapaneseWordInformations (){
		return japaneseWordInformations;
	}

	public List <RepeatingInformation> getJapaneseWordsRepeatingInformations(){
		return japaneseWordsRepeatingLists;
	}

}

package com.kanji.saving;

import com.kanji.constants.enums.ApplicationSaveableState;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.listElements.RepeatingData;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class SavingInformation implements Serializable {
	private final static long serialVersionUID = -8017224611162128282L;
	private List<Kanji> kanjiWords;
	private List<RepeatingData> repeatingList;
	private Set<Kanji> problematicKanjis;
	private Set<JapaneseWord> problematicJapaneseWords;
	private RepeatingState repeatingState;
	private ProblematicKanjisState problematicKanjisState;
	private ApplicationSaveableState applicationSaveableState;
	private String kanjiKoohiCookiesHeaders;
	private List<JapaneseWord> japaneseWords;
	private List<RepeatingData> japaneseWordsRepeatingLists;

	public SavingInformation(List<Kanji> kanjiWords,
			List<RepeatingData> repeatingList, Set<Kanji> problematicKanjis,
			Set<JapaneseWord> problematicJapaneseWords,
			List<JapaneseWord> japaneseWords,
			List<RepeatingData> japaneseWordsRepeatingLists) {
		this.kanjiWords = kanjiWords;
		this.repeatingList = repeatingList;
		this.problematicKanjis = problematicKanjis;
		this.problematicJapaneseWords = problematicJapaneseWords;
		this.japaneseWords = japaneseWords;
		this.japaneseWordsRepeatingLists = japaneseWordsRepeatingLists;
	}

	public boolean containsProblematicKanji() {
		return !problematicKanjis.isEmpty();
	}

	public boolean containsProblematicJapaneseWords() {
		return !problematicJapaneseWords.isEmpty();
	}

	public List<Kanji> getKanjiWords() {
		return kanjiWords;
	}

	public List<RepeatingData> getRepeatingList() {
		return repeatingList;
	}

	public Set<Kanji> getProblematicKanjis() {
		return problematicKanjis;
	}

	public RepeatingState getRepeatingState() {
		return repeatingState;
	}

	public void setRepeatingState(RepeatingState state) {
		this.repeatingState = state;
		applicationSaveableState = ApplicationSaveableState.REPEATING_WORDS;
	}

	public ProblematicKanjisState getProblematicKanjisState() {
		return problematicKanjisState;
	}

	public void setProblematicKanjisState(
			ProblematicKanjisState problematicKanjisState,
			ApplicationSaveableState state) {
		if (!state
				.equals(ApplicationSaveableState.REVIEWING_PROBLEMATIC_JAPANESE_WORDS)
				&& !state
				.equals(ApplicationSaveableState.REVIEWING_PROBLEMATIC_KANJIS)) {
			throw new IllegalArgumentException(
					"Only reviewing state can be used here");
		}
		this.problematicKanjisState = problematicKanjisState;
		applicationSaveableState = state;
	}

	public ApplicationSaveableState getApplicationSaveableState() {
		return applicationSaveableState;
	}

	public boolean hasStateToRestore() {
		return applicationSaveableState != null;
	}

	public void setKanjiKoohiiCookiesHeaders(String kanjiKoohiCookiesHeaders) {
		this.kanjiKoohiCookiesHeaders = kanjiKoohiCookiesHeaders;
	}

	public String getKanjiKoohiiCookiesHeaders() {
		return kanjiKoohiCookiesHeaders;
	}

	public List<JapaneseWord> getJapaneseWords() {
		return japaneseWords;
	}

	public List<RepeatingData> getJapaneseWordsRepeatingInformations() {
		return japaneseWordsRepeatingLists;
	}

	public Set<JapaneseWord> getProblematicJapaneseWords() {
		return problematicJapaneseWords;
	}
}

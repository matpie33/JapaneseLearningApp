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
	private ProblematicWordsState problematicWordsState;
	private ApplicationSaveableState applicationSaveableState;
	private String kanjiKoohiCookiesHeaders;
	private List<JapaneseWord> japaneseWords;
	private List<RepeatingData> japaneseWordsRepeatingLists;
	private int lastBackupFileNumber;

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
		Class<?> aClass = state.getCurrentlyRepeatedWords().iterator().next()
				.getClass();
		ApplicationSaveableState saveableState;
		//TODO do it better
		if (aClass.equals(Kanji.class)) {
			saveableState = ApplicationSaveableState.REPEATING_KANJI;
		}
		else {
			saveableState = ApplicationSaveableState.REPEATING_JAPANESE_WORDS;
		}
		applicationSaveableState = saveableState;
	}

	public ProblematicWordsState getProblematicWordsState() {
		return problematicWordsState;
	}

	public void setProblematicWordsState(
			ProblematicWordsState problematicWordsState,
			ApplicationSaveableState state) {
		if (!state
				.equals(ApplicationSaveableState.REVIEWING_PROBLEMATIC_JAPANESE_WORDS)
				&& !state
				.equals(ApplicationSaveableState.REVIEWING_PROBLEMATIC_KANJIS)) {
			throw new IllegalArgumentException(
					"Only reviewing state can be used here but was: " + state);
		}
		this.problematicWordsState = problematicWordsState;
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

	public int getLastBackupFileNumber() {
		return lastBackupFileNumber;
	}

	public void setLastBackupFileNumber(int lastBackupFileNumber) {
		this.lastBackupFileNumber = lastBackupFileNumber;
	}

	public void clearApplicationState() {
		applicationSaveableState = null;
	}

}

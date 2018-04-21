package com.kanji.list.listElements;

import com.kanji.utilities.StringUtilities;

import java.util.HashSet;
import java.util.Set;

public class JapaneseWriting implements ListElement {

	private static final String KANA = "kana";
	private static final String KANJI = "kanji";
	private String kanaWriting;
	private Set<String> kanjiWritings;

	public JapaneseWriting(String kanaWriting, Set<String> kanjiWritings) {
		this.kanaWriting = kanaWriting;
		this.kanjiWritings = kanjiWritings;
	}

	@Override
	public boolean isEmpty() {
		return kanaWriting.isEmpty() && kanjiWritings.isEmpty();
	}

	public String getKanaWriting() {
		return kanaWriting;
	}

	public void setKanaWriting(String kanaWriting) {
		this.kanaWriting = kanaWriting;
	}

	public Set<String> getKanjiWritings() {
		return kanjiWritings;
	}

	public static ListElementInitializer<JapaneseWriting> getInitializer() {
		return () -> {
			Set<String> arrayList = new HashSet<>();
			arrayList.add("");
			return new JapaneseWriting("", arrayList);
		};
	}

	@Override
	public boolean equals(Object element) {
		if (!element.getClass().equals(getClass())) {
			return false;
		}
		JapaneseWriting otherWriting = (JapaneseWriting) element;
		return !getKanaWriting().isEmpty() && otherWriting.getKanaWriting()
				.equals(getKanaWriting()) && getKanjiWritings()
				.containsAll(otherWriting.getKanjiWritings());
	}

	public void setKanjiWritings(Set<String> kanjiWritings) {
		this.kanjiWritings = kanjiWritings;
	}

	public void addKanjiWriting(String textValue) {
		kanjiWritings.add(textValue);
	}

	@Override
	public String getDisplayedText() {
		return StringUtilities.joinPropertyValuePairs(//
				StringUtilities.joinPropertyAndValue(KANA, getKanaWriting()),
				StringUtilities.joinPropertyAndValue(KANJI, StringUtilities
						.concatenateStrings(getKanjiWritings())));
	}

	public void replaceKanji(String previousValue, String newValue) {
		getKanjiWritings().remove(previousValue);
		getKanjiWritings().add(newValue);
	}

	@Override
	public String toString (){
		return getDisplayedText();
	}

}

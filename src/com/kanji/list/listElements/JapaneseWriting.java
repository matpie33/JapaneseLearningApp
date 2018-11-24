package com.kanji.list.listElements;

import com.guimaker.enums.InputGoal;
import com.guimaker.list.ListElement;
import com.guimaker.list.ListElementInitializer;
import com.guimaker.utilities.StringUtilities;
import com.kanji.constants.enums.TypeOfJapaneseWriting;
import com.kanji.list.listElementPropertyManagers.japaneseWordWritings.JapaneseWordWritingsChecker;
import com.kanji.utilities.JapaneseWritingUtilities;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class JapaneseWriting implements ListElement, Serializable {

	private static final String KANA = "kana";
	private static final String KANJI = "kanji";
	private static final long serialVersionUID = -7397611423482201633L;
	private String kanaWriting;
	private Set<String> kanjiWritings;

	public JapaneseWriting(String kanaWriting, Set<String> kanjiWritings) {
		this.kanaWriting = kanaWriting;
		this.kanjiWritings = kanjiWritings;
	}

	public JapaneseWriting(String kanaWriting, String... kanjiWritings) {
		this(kanaWriting, new HashSet<>(Arrays.asList(kanjiWritings)));
	}


	@Override
	public boolean isEmpty() {
		return JapaneseWritingUtilities.isInputEmpty(kanaWriting,
				TypeOfJapaneseWriting.KANA) && (kanjiWritings.isEmpty()
				|| JapaneseWritingUtilities.areKanjiWritingsEmpty(
				kanjiWritings));
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
			Set<String> kanjiWritings = new HashSet<>();
			kanjiWritings.add("");
			return new JapaneseWriting("", kanjiWritings);
		};
	}

	@Override
	public boolean equals(Object element) {
		if (!element.getClass()
					.equals(getClass())) {
			return false;
		}
		JapaneseWriting otherWriting = (JapaneseWriting) element;
		return JapaneseWordWritingsChecker.areWritingsEqual(otherWriting, this,
				InputGoal.ADD);
	}

	@Override
	public String getDisplayedText() {
		return StringUtilities.joinPropertyValuePairs(//
				StringUtilities.joinPropertyAndValue(KANA, getKanaWriting()),
				StringUtilities.joinPropertyAndValue(KANJI,
						StringUtilities.concatenateStrings(
								getKanjiWritings())));
	}

	public void replaceKanji(String previousValue, String newValue) {
		getKanjiWritings().remove(previousValue);
		getKanjiWritings().add(newValue);
	}

	@Override
	public String toString() {
		return getDisplayedText();
	}

	public void setKanjiWritings(Set<String> kanjiWritings) {
		this.kanjiWritings = kanjiWritings;
	}
}

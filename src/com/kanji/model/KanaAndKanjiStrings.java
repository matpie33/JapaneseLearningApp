package com.kanji.model;

import javax.swing.text.JTextComponent;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class KanaAndKanjiStrings {
	private String kana;
	private List<String> kanji;
	private String modifiedValue;
	private boolean kanaModified;

	public KanaAndKanjiStrings(String kana, List<String> kanji,
			String modifiedValue, boolean kanaModified) {
		this.kana = kana;
		this.kanji = kanji;
		this.modifiedValue = modifiedValue;
		this.kanaModified = kanaModified;
	}

	public KanaAndKanjiStrings(JTextComponent kana, List<JTextComponent> kanji,
			String modifiedValue, boolean kanaModified) {
		this.kana = kana.getText();
		this.kanji = convertKanjiTextfieldsToStrings(kanji);
		this.modifiedValue = modifiedValue;
		this.kanaModified = kanaModified;
	}

	public String getKana() {
		return kana;
	}

	public List<String> getKanji() {
		return kanji;
	}

	@Override public String toString() {
		return modifiedValue;
	}

	public boolean isKanaModified() {
		return kanaModified;
	}

	public String getModifiedValue() {
		return modifiedValue;
	}

	@Override public int hashCode() {
		return Objects.hash(getModifiedValue());
	}

	@Override public boolean equals(Object other) {
		if (!other.getClass().equals(getClass())) {
			return false;
		}
		KanaAndKanjiStrings otherKana = (KanaAndKanjiStrings) other;
		return otherKana.getModifiedValue().equals(getModifiedValue());
	}

	public List<String> convertKanjiTextfieldsToStrings(
			List<JTextComponent> kanjiTextFields) {
		return kanjiTextFields.stream().map(JTextComponent::getText)
				.collect(Collectors.toList());
	}

}

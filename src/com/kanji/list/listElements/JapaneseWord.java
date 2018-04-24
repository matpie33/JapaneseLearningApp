package com.kanji.list.listElements;

import com.kanji.constants.enums.AdditionalInformationTag;
import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.enums.WordSearchOptions;
import com.kanji.list.listElementAdditionalInformations.AdditionalInformation;
import com.kanji.list.listElementPropertyManagers.JapaneseWordMeaningChecker;
import com.kanji.list.listElementPropertyManagers.japaneseWordWritings.JapaneseWordWritingsChecker;
import com.kanji.utilities.StringUtilities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kanji.constants.strings.Labels.PART_OF_SPEECH;

public class JapaneseWord implements ListElement, Serializable {

	private static final long serialVersionUID = 7723326146436941154L;
	private static final String MEANING = "znaczenie";
	private static final String JAPANESE_WRITING = "zapis";
	private Set<JapaneseWriting> japaneseWritings;
	private String meaning;
	private PartOfSpeech partOfSpeech;
	private Set<AdditionalInformation> additionalInformations = new HashSet<>();
	private static JapaneseWordMeaningChecker meaningChecker = new JapaneseWordMeaningChecker(
			WordSearchOptions.BY_FULL_EXPRESSION);

	public JapaneseWord(PartOfSpeech partOfSpeech, String meaning) {
		this.partOfSpeech = partOfSpeech;
		this.meaning = meaning;
		japaneseWritings = new HashSet<>();
	}

	public void addWritings(String kanaWriting,
			String... kanjiWritingsForThisKana) {
		Set<String> kanjiWritings = new HashSet<>();
		for (String kanjiWriting : kanjiWritingsForThisKana) {
			kanjiWritings.add(kanjiWriting);
		}
		japaneseWritings.add(new JapaneseWriting(kanaWriting, kanjiWritings));
	}

	public Set<JapaneseWriting> getWritings() {
		return japaneseWritings;
	}

	public String getMeaning() {
		return meaning;
	}

	public boolean hasKanjiWriting() {
		for (JapaneseWriting japaneseWriting : japaneseWritings) {
			if (!japaneseWriting.getKanjiWritings().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public void setMeaning(String meaning) {
		this.meaning = meaning;
	}

	public void addAditionalInformation(AdditionalInformationTag tag,
			String value) {
		additionalInformations.add(new AdditionalInformation(tag, value));
	}

	public static ListElementInitializer<JapaneseWord> getInitializer() {
		return () -> {
			JapaneseWord japaneseWord = new JapaneseWord(PartOfSpeech.NOUN, "");
			japaneseWord.addWritings("", new String[] { "" });
			japaneseWord.addWritings("", new String[] { "" });
			return japaneseWord;
		};
	}

	public PartOfSpeech getPartOfSpeech() {
		return partOfSpeech;
	}

	public void setPartOfSpeech(PartOfSpeech partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}

	public boolean hasAdditionalVerbConjugationInformation() {
		return getVerbConjugationInformation().isEmpty() ? false : true;
	}

	public String getVerbConjugationInformation() {
		for (AdditionalInformation additionalInformation : additionalInformations) {
			if (additionalInformation.getTag()
					.equals(AdditionalInformationTag.VERB_CONJUGATION)) {
				return additionalInformation.getValue();
			}
		}
		return "";
	}

	public Set<String> getKanjiWritings() {
		Set<String> kanjiWritings = new HashSet<>();
		for (JapaneseWriting japaneseWriting : japaneseWritings) {
			kanjiWritings.addAll(japaneseWriting.getKanjiWritings());
		}
		return kanjiWritings;
	}

	public Set<String> getKanaWritings() {
		return japaneseWritings.stream().map(JapaneseWriting::getKanaWriting)
				.collect(Collectors.toSet());
	}

	@Override
	public boolean equals(Object element) {
		if (element instanceof JapaneseWord) {
			JapaneseWord otherWord = (JapaneseWord) element;

			for (JapaneseWriting japaneseWriting : otherWord.getWritings()) {
				if (new JapaneseWordWritingsChecker(japaneseWriting, true)
						.isPropertyFound(japaneseWriting, this)) {
					return true;
				}
			}
			if (meaningChecker.isPropertyFound(otherWord.getMeaning(), this)) {
				return true;
			}
			return false;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getWritings(), getMeaning(), getPartOfSpeech());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(20);
		builder.append("\nKana to kanji");
		builder.append(getWritings());
		builder.append("\nAdditionalInformations");
		for (AdditionalInformation additionalInformation : additionalInformations) {
			builder.append(additionalInformation.getTag());
			builder.append(" ");
			builder.append(additionalInformation.getValue());
		}

		builder.append("\nWord type: ");
		builder.append(partOfSpeech.getPolishMeaning());
		builder.append("\nWord meaning: " + meaning);

		return builder.toString();
	}

	@Override
	public boolean isEmpty() {
		return getMeaning().isEmpty() || getWritings().isEmpty();
	}

	public boolean setWriting(JapaneseWriting writing) {
		if (!writing.isEmpty()){
			return this.japaneseWritings.add(writing);
		}
		return false;
	}

	public void setWritings(Set<JapaneseWriting> writings) {
		japaneseWritings.clear();
		for (JapaneseWriting writing: writings){
			if (!writing.isEmpty()){
				japaneseWritings.add(writing);
			}
		}

	}

	@Override
	public String getDisplayedText() {
		return StringUtilities.joinPropertyValuePairs(//
				StringUtilities.joinPropertyAndValue(MEANING, getMeaning()),//
				StringUtilities
						.joinPropertyAndValue(PART_OF_SPEECH.toLowerCase()//
								, getPartOfSpeech().getPolishMeaning()),//
				StringUtilities.joinPropertyAndValue(JAPANESE_WRITING, //
						StringUtilities.concatenateStrings(//
								getWritings().//
										stream()
										.map(JapaneseWriting::getDisplayedText)//
										.collect(Collectors.toList()))));//
	}

	public boolean containsWriting(JapaneseWriting writing) {
		for (JapaneseWriting thisWriting: getWritings()){
			if (thisWriting.equals(writing)){
				return true;
			}
		}
		return false;
	}
}

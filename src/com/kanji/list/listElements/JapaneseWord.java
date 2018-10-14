package com.kanji.list.listElements;

import com.kanji.constants.enums.InputGoal;
import com.kanji.constants.enums.JapaneseParticle;
import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.enums.WordSearchOptions;
import com.kanji.list.listElementPropertyManagers.JapaneseWordMeaningChecker;
import com.kanji.list.listElementPropertyManagers.japaneseWordWritings.JapaneseWordWritingsChecker;
import com.kanji.model.AdditionalInformation;
import com.kanji.model.WordParticlesData;
import com.kanji.utilities.Pair;
import com.kanji.utilities.StringUtilities;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
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
	private Pair<PartOfSpeech, AdditionalInformation> partOfSpeechWithInformation;
	private Set<WordParticlesData> takenParticles = new HashSet<>();
	public final static String MEANINGFUL_NAME = "Japońskie słowo";

	private static JapaneseWordMeaningChecker meaningChecker = new JapaneseWordMeaningChecker(
			WordSearchOptions.BY_FULL_EXPRESSION);

	public JapaneseWord(PartOfSpeech partOfSpeech, String meaning) {
		setPartOfSpeech(partOfSpeech);
		this.meaning = meaning;
		japaneseWritings = new HashSet<>();
	}

	@Override
	public String getMeaningfulName() {
		return MEANINGFUL_NAME;
	}

	public void addWritingsForKana(String kanaWriting,
			String... kanjiWritingsForThisKana) {
		addWriting(new JapaneseWriting(kanaWriting,
				new HashSet<>(Arrays.asList(kanjiWritingsForThisKana))));
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

	public static ListElementInitializer<JapaneseWord> getInitializer() {
		return () -> {
			JapaneseWord japaneseWord = new JapaneseWord(PartOfSpeech.NOUN, "");
			japaneseWord.japaneseWritings
					.add(JapaneseWriting.getInitializer().initializeElement());
			japaneseWord.japaneseWritings
					.add(JapaneseWriting.getInitializer().initializeElement());
			return japaneseWord;
		};
	}

	public PartOfSpeech getPartOfSpeech() {
		return partOfSpeechWithInformation.getLeft();
	}

	public AdditionalInformation getAdditionalInformation() {
		return partOfSpeechWithInformation.getRight();
	}

	public void setPartOfSpeech(PartOfSpeech partOfSpeech) {
		partOfSpeechWithInformation = new Pair<>(partOfSpeech,
				new AdditionalInformation(
						partOfSpeech.getAdditionalInformationTag(),
						partOfSpeech.getPossibleValues()));
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
			if (isEmpty() && ((JapaneseWord) element).isEmpty()) {
				return true;
			}
			else if (isEmpty() || ((JapaneseWord) element).isEmpty()){
				return false;
			}
			JapaneseWord otherWord = (JapaneseWord) element;

			for (JapaneseWriting japaneseWriting : otherWord.getWritings()) {
				if (new JapaneseWordWritingsChecker(japaneseWriting,
						InputGoal.ADD).isPropertyFound(japaneseWriting, this)) {
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
		AdditionalInformation additionalInformation = partOfSpeechWithInformation
				.getRight();
		builder.append(
				additionalInformation.getTag() + ", " + additionalInformation
						.getValue());

		builder.append("\nWord type: ");
		builder.append(getPartOfSpeech().getPolishMeaning());
		builder.append("\nWord meaning: " + meaning);
		builder.append("\nParticles: " + getTakenParticles());

		return builder.toString();
	}

	@Override
	public boolean isEmpty() {
		return getMeaning().isEmpty() || getWritings().isEmpty();
	}

	public boolean addWriting(JapaneseWriting writing) {
		if (!writing.isEmpty()) {
			return this.japaneseWritings.add(writing);
		}
		return false;
	}

	public void setWritings(Set<JapaneseWriting> writings) {
		japaneseWritings.clear();
		for (JapaneseWriting writing : writings) {
			if (!writing.isEmpty()) {
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
		for (JapaneseWriting thisWriting : getWritings()) {
			if (thisWriting.equals(writing) || (thisWriting.isEmpty() && writing
					.isEmpty())) {
				return true;
			}
		}
		return false;
	}

	public Set<WordParticlesData> getTakenParticles() {
		if (takenParticles == null) {
			takenParticles = new HashSet<>();
		}
		return takenParticles;
	}

	public void addTakenParticle(JapaneseParticle particle,
			String... additionalInformation) {
		String mergedAdditionalInformations = StringUtilities
				.concatenateStrings(Arrays.asList(additionalInformation));
		takenParticles.add(new WordParticlesData(particle)
				.setAdditionalInformation(mergedAdditionalInformations));
	}

	public void removeParticle(JapaneseParticle particle) {
		WordParticlesData particleDataToRemove = null;
		for (WordParticlesData existingParticle : getTakenParticles()) {
			if (existingParticle.getJapaneseParticle().equals(particle)) {
				particleDataToRemove = existingParticle;
				break;
			}
		}
		getTakenParticles().remove(particleDataToRemove);
	}

	public boolean addParticleData(WordParticlesData particleData) {
		return getTakenParticles().add(particleData);
	}

	public boolean hasParticle(JapaneseParticle particle) {
		return getTakenParticles().contains(new WordParticlesData(particle));
	}

	public void setAdditionalInformation(
			AdditionalInformation additionalInformation) {
		this.partOfSpeechWithInformation = new Pair<>(
				partOfSpeechWithInformation.getLeft(), additionalInformation);
	}
}

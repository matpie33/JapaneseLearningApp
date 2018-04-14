package com.kanji.list.listElements;

import com.kanji.constants.enums.AdditionalInformationTag;
import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.enums.WordSearchOptions;
import com.kanji.list.listElementAdditionalInformations.AdditionalInformation;
import com.kanji.list.listElementPropertyManagers.JapaneseWordMeaningChecker;
import com.kanji.list.listElementPropertyManagers.JapaneseWordWritingsChecker;
import com.kanji.model.KanaAndKanjiStrings;

import java.io.Serializable;
import java.util.*;

public class JapaneseWord implements ListElement, Serializable {

	private static final long serialVersionUID = 7723326146436941154L;
	private Map<String, Set<String>> kanjiToAlternativeKanaWritingMap;
	private String meaning;
	private PartOfSpeech partOfSpeech;
	private Set<AdditionalInformation> additionalInformations = new HashSet<>();
	private static JapaneseWordMeaningChecker meaningChecker = new JapaneseWordMeaningChecker(
			WordSearchOptions.BY_FULL_EXPRESSION);
	private static JapaneseWordWritingsChecker writingsChecker = new JapaneseWordWritingsChecker(
			null, true, true, "");
	//TODO kana checker flag is not always needed

	public JapaneseWord(PartOfSpeech partOfSpeech,
			String meaning) {
		this.partOfSpeech = partOfSpeech;
		this.meaning = meaning;
		kanjiToAlternativeKanaWritingMap = new HashMap<>();
	}

	public void addWritings(String kanaWriting,
			String... kanjiWritingsForThisKana) {
		kanjiToAlternativeKanaWritingMap.put(kanaWriting,
				new HashSet<>(Arrays.asList(kanjiWritingsForThisKana)));
	}

	public Map<String, Set<String>> getKanaToKanjiWritingsMap() {
		//TODO remove this method - use "get japanese writings"
		return kanjiToAlternativeKanaWritingMap;
	}

	public List<JapaneseWriting> getWritings() {
		//TODO just store it as japanese writings instead of a map
		List<JapaneseWriting> japaneseWritings = new ArrayList<>();
		if (kanjiToAlternativeKanaWritingMap.isEmpty()) {
			japaneseWritings.add(new JapaneseWriting("",
					new HashSet<>(Arrays.asList(""))));
		}
		for (Map.Entry<String, Set<String>> kanaToKanjiWriting : kanjiToAlternativeKanaWritingMap
				.entrySet()) {
			japaneseWritings
					.add(new JapaneseWriting(kanaToKanjiWriting.getKey(),
							kanaToKanjiWriting.getValue()));
		}
		return japaneseWritings;
	}

	public String getMeaning() {
		return meaning;
	}

	public boolean hasKanjiWriting() {
		for (Set<String> kanjiWriting : kanjiToAlternativeKanaWritingMap
				.values()) {
			if (kanjiWriting.size() > 1 || (kanjiWriting.size() == 1
					&& !kanjiWriting.iterator().next().isEmpty())) {
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
		return () -> new JapaneseWord(PartOfSpeech.NOUN, "");
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
		Set<String> kanaWritingsSet = new HashSet<>();
		for (Set<String> kanaWritings : kanjiToAlternativeKanaWritingMap
				.values()) {
			kanaWritingsSet.addAll(kanaWritings);
		}
		return kanaWritingsSet;
	}

	public Set<String> getKanaWritings() {
		System.out.println(this);
		return kanjiToAlternativeKanaWritingMap.keySet();
	}

	@Override
	public boolean isSameAs(ListElement element) {
		if (element instanceof JapaneseWord) {
			JapaneseWord otherWord = (JapaneseWord) element;

			//TODO avoid passing null to japanese writings checker
			List<KanaAndKanjiStrings> kanaAndKanjiStrings = new ArrayList<>();
			for (Map.Entry<String, Set<String>> kanaToKanjis : otherWord
					.getKanaToKanjiWritingsMap().entrySet()) {
				kanaAndKanjiStrings
						.add(new KanaAndKanjiStrings(kanaToKanjis.getKey(),
								kanaToKanjis.getValue(), "", false));

			}
			if (writingsChecker.isPropertyFound(kanaAndKanjiStrings, this)) {
				return true;
			}
			if (meaningChecker
					.isPropertyFound(otherWord.getMeaning(), this)) {
				return true;
			}
			return false;
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(20);
		builder.append("\nKana to kanji");
		builder.append(getKanaToKanjiWritingsMap());
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
		return getMeaning().isEmpty() || getKanaToKanjiWritingsMap()
				.isEmpty();
	}
}

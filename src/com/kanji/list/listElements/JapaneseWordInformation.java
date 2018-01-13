package com.kanji.list.listElements;

import com.kanji.constants.enums.AdditionalInformationTag;
import com.kanji.constants.enums.ListElementPropertyType;
import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.list.listElementAdditionalInformations.AdditionalInformation;
import com.kanji.list.listElementPropertyManagers.*;
import com.kanji.constants.strings.Labels;

import java.io.Serializable;
import java.util.*;

public class JapaneseWordInformation implements ListElement, Serializable {

	private Map<String, List <String>> kanjiToAlternativeKanaWritingMap;
	private String wordMeaning;
	private PartOfSpeech partOfSpeech;
	private Set<AdditionalInformation> additionalInformations = new HashSet<>();

	public JapaneseWordInformation (PartOfSpeech partOfSpeech,
			String wordMeaning){
		this.partOfSpeech = partOfSpeech;
		this.wordMeaning = wordMeaning;
		kanjiToAlternativeKanaWritingMap = new HashMap<>();
	}

	public void addWritings (String kanaWriting, String ... kanjiWritingsForThisKana){
		kanjiToAlternativeKanaWritingMap.put(kanaWriting,
				Arrays.asList(kanjiWritingsForThisKana));
	}

	public void addWriting (String kanaWriting, String kanjiWriting){
		addWritings(kanaWriting, kanjiWriting);
	}

	public Map <String, List<String>> getKanaToKanjiWritingsMap(){
		return kanjiToAlternativeKanaWritingMap;
	}

	public String getWordMeaning() {
		return wordMeaning;
	}

	public boolean hasKanjiWriting(){
		for (String kanjiWriting: kanjiToAlternativeKanaWritingMap.keySet()){
			if (!kanjiWriting.isEmpty()){
				return true;
			}
		}
		return false;
	}

	public static List<ListElementData<JapaneseWordInformation>> getElementsTypesAndLabels() {
		List<ListElementData<JapaneseWordInformation>> listElementData = new ArrayList<>();
		listElementData.add(new ListElementData<>(Labels.WORD_MEANING,
				new JapaneseWordMeaningChecker(), ListElementPropertyType.STRING_SHORT_WORD, Labels.COMBOBOX_OPTION_SEARCH_BY_WORD_MEANING));
		listElementData.add(new ListElementData<>(Labels.WORD_IN_KANA,
				new JapaneseWordKanaChecker(), ListElementPropertyType.STRING_SHORT_WORD, Labels.COMBOBOX_OPTION_SEARCH_BY_KANA));
		listElementData.add(new ListElementData<>(Labels.WORD_IN_KANJI,
				new JapaneseWordKanjiChecker(), ListElementPropertyType.STRING_SHORT_WORD, Labels.COMBOBOX_OPTION_SEARCH_BY_KANJI));
		listElementData.add(new ListElementData<>(Labels.PART_OF_SPEECH,
				new NotChecker(), ListElementPropertyType.COMBOBOX_OPTION, Labels.COMBOBOX_OPTION_SEARCH_BY_PART_OF_SPEECH));

		return listElementData;
	}

	public void setWordMeaning(String wordMeaning) {
		this.wordMeaning = wordMeaning;
	}

	public void addAditionalInformation (AdditionalInformationTag tag, String value){
		additionalInformations.add(new AdditionalInformation(tag, value));
	}

	public static ListElementInitializer<JapaneseWordInformation> getInitializer (){
		return () -> new JapaneseWordInformation(PartOfSpeech.NOUN,"");
	}

	public PartOfSpeech getPartOfSpeech() {
		return partOfSpeech;
	}

	public void setPartOfSpeech(PartOfSpeech partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}

	public boolean hasAdditionalVerbConjugationInformation (){
		return getVerbConjugationInformation().isEmpty() ? false: true;
	}

	public String getVerbConjugationInformation (){
		for (AdditionalInformation additionalInformation: additionalInformations){
			if (additionalInformation.getTag().equals(AdditionalInformationTag.VERB_CONJUGATION)){
				return additionalInformation.getValue();
			}
		}
		return "";
	}

	public Set <String> getKanjiWritings(){
		Set <String> kanaWritingsSet = new HashSet<>();
		for (List<String> kanaWritings: kanjiToAlternativeKanaWritingMap.values()){
			kanaWritingsSet.addAll(kanaWritings);
		}
		return kanaWritingsSet;
	}

	public Set <String> getKanaWritings(){
		return kanjiToAlternativeKanaWritingMap.keySet();
	}

	@Override public boolean isSameAs(ListElement element) {
		if (element instanceof JapaneseWordInformation){
			JapaneseWordInformation otherWord = (JapaneseWordInformation)element;
			return otherWord.getKanaToKanjiWritingsMap().equals(
					kanjiToAlternativeKanaWritingMap);
		}
		return false;
	}

	@Override
	public String toString (){
		StringBuilder builder = new StringBuilder(20);
		builder.append("\nKana");
//		if (writingsInKana.length == 1 && writingsInKana[0].isEmpty()){
//			return "";
//		}
//		for (String kana: writingsInKana){
//			builder.append(kana);
//			builder.append(" ");
//		}
//		builder.append("\nKanjis");
//		for (String kanji: writingsInKanji){
//			builder.append(kanji);
//			builder.append(" ");
//		}
		builder.append("\nAdditionalInformations");
		for (AdditionalInformation additionalInformation: additionalInformations){
			builder.append(additionalInformation.getTag());
			builder.append(" ");
			builder.append(additionalInformation.getValue());
		}

		builder.append("\nWord type: ");
		builder.append(partOfSpeech.getPolishMeaning());
		builder.append("\nWord meaning: "+wordMeaning);

		return builder.toString();
	}

}

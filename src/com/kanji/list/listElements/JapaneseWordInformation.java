package com.kanji.list.listElements;

import com.kanji.constants.enums.AdditionalInformationTag;
import com.kanji.constants.enums.ListElementPropertyType;
import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.list.listElementAdditionalInformations.AdditionalInformation;
import com.kanji.list.listElementPropertyManagers.*;
import com.kanji.constants.strings.Labels;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JapaneseWordInformation implements ListElement, Serializable {

	private String[] writingsInKana;
	private String[] writingsInKanji = new String [] {};
	private String wordMeaning;
	private PartOfSpeech partOfSpeech;
	private Set<AdditionalInformation> additionalInformations = new HashSet<>();

	public JapaneseWordInformation (PartOfSpeech partOfSpeech, String[] writingsInKana,
			String wordMeaning){
		this.partOfSpeech = partOfSpeech;
		this.writingsInKana = writingsInKana;
		this.wordMeaning = wordMeaning;
	}

	public JapaneseWordInformation (PartOfSpeech partOfSpeech, String[] writingsInKanji,
			String[] writingsInKana, String wordMeaning){
		this(partOfSpeech, writingsInKana, wordMeaning);
		this.writingsInKanji = writingsInKanji;
	}

	public String[] getWritingsInKana() {
		return writingsInKana;
	}

	public String getWordMeaning() {
		return wordMeaning;
	}

	public String[] getWritingsInKanji() {
		return writingsInKanji;
	}

	public boolean hasKanjiWriting(){
		return writingsInKanji.length != 0;
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

	public void setWritingsInKana(String[] writingsInKana) {
		if (writingsInKana.length >1){
			for (int i=1; i<writingsInKana.length; i++){
				addAditionalInformation(AdditionalInformationTag.ALTERNATIVE_KANA_WRTING,
						writingsInKana[i]);
			}
		}
		this.writingsInKana = writingsInKana;
	}

	public void setWritingsInKanji(String[] writingsInKanji) {
		if (writingsInKanji.length >1){
			for (int i=1; i<writingsInKanji.length; i++){
				addAditionalInformation(AdditionalInformationTag.ALTERNATIVE_KANJI_WRITING,
						writingsInKanji[i]);
			}
		}
		this.writingsInKanji = writingsInKanji;
	}

	public void setWordMeaning(String wordMeaning) {
		this.wordMeaning = wordMeaning;
	}

	public void addAditionalInformation (AdditionalInformationTag tag, String value){
		additionalInformations.add(new AdditionalInformation(tag, value));
	}

	public static ListElementInitializer<JapaneseWordInformation> getInitializer (){
		return () -> new JapaneseWordInformation(PartOfSpeech.NOUN,
				new String [] {""}, "");
	}

	public PartOfSpeech getPartOfSpeech() {
		return partOfSpeech;
	}

	public void setPartOfSpeech(PartOfSpeech partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}

	@Override public boolean isSameAs(ListElement element) {
		if (element instanceof JapaneseWordInformation){
			JapaneseWordInformation otherWord = (JapaneseWordInformation)element;
			return otherWord.getWritingsInKana().equals(writingsInKana) && otherWord.getWritingsInKanji()
					.equals(writingsInKanji);
		}
		return false;
	}

	@Override
	public String toString (){
		StringBuilder builder = new StringBuilder(20);
		builder.append("\nKana");
		if (writingsInKana.length == 1 && writingsInKana[0].isEmpty()){
			return "";
		}
		for (String kana: writingsInKana){
			builder.append(kana);
			builder.append(" ");
		}
		builder.append("\nKanjis");
		for (String kanji: writingsInKanji){
			builder.append(kanji);
			builder.append(" ");
		}
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

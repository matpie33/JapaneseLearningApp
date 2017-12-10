package com.kanji.list.listElements;

import com.kanji.constants.enums.ListElementPropertyType;
import com.kanji.list.listElementPropertyManagers.*;
import com.kanji.constants.strings.Labels;

import java.util.ArrayList;
import java.util.List;

public class JapaneseWordInformation implements ListElement {

	private String wordInKana;
	private String wordInKanji = "";
	private String wordMeaning;

	public JapaneseWordInformation (String wordInKana, String wordMeaning){

		this.wordInKana = wordInKana;
		this.wordMeaning = wordMeaning;
	}

	public JapaneseWordInformation (String wordInKanji, String wordInKana, String wordMeaning){
		this(wordInKana, wordMeaning);
		this.wordInKanji = wordInKanji;
	}

	public String getWordInKana() {
		return wordInKana;
	}

	public String getWordMeaning() {
		return wordMeaning;
	}

	public String getWordInKanji() {
		return wordInKanji;
	}

	public boolean hasKanjiWriting(){
		return !wordInKanji.isEmpty();
	}

	public static List<ListElementData<JapaneseWordInformation>> getElementsTypesAndLabels() {
		List<ListElementData<JapaneseWordInformation>> listElementData = new ArrayList<>();
		listElementData.add(new ListElementData<>(Labels.WORD_MEANING,
				new JapaneseWordMeaningChecker(), ListElementPropertyType.STRING_SHORT_WORD, Labels.COMBOBOX_OPTION_SEARCH_BY_WORD_MEANING));
		listElementData.add(new ListElementData<>(Labels.WORD_IN_KANA,
				new JapaneseWordKanaChecker(), ListElementPropertyType.STRING_SHORT_WORD, Labels.COMBOBOX_OPTION_SEARCH_BY_KANA));
		listElementData.add(new ListElementData<>(Labels.WORD_IN_KANJI,
				new JapaneseWordKanjiChecker(), ListElementPropertyType.STRING_SHORT_WORD, Labels.COMBOBOX_OPTION_SEARCH_BY_KANJI));
		return listElementData;
	}

	public void setWordInKana(String wordInKana) {
		this.wordInKana = wordInKana;
	}

	public void setWordInKanji(String wordInKanji) {
		this.wordInKanji = wordInKanji;
	}

	public void setWordMeaning(String wordMeaning) {
		this.wordMeaning = wordMeaning;
	}

	public static ListElementInitializer<JapaneseWordInformation> getInitializer (){
		return () -> new JapaneseWordInformation("", "");
	}

	@Override public boolean isSameAs(ListElement element) {
		if (element instanceof JapaneseWordInformation){
			JapaneseWordInformation otherWord = (JapaneseWordInformation)element;
			return otherWord.getWordInKana().equals(wordInKana) && otherWord.getWordInKanji()
					.equals(wordInKanji);
		}
		return false;
	}

}

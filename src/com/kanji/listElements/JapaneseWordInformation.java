package com.kanji.listElements;

import com.kanji.enums.ListElementType;
import com.kanji.listSearching.JapaneseWordKanaChecker;
import com.kanji.listSearching.JapaneseWordKanjiChecker;
import com.kanji.listSearching.JapaneseWordMeaningChecker;
import com.kanji.listSearching.PropertyManager;
import com.kanji.strings.Labels;

import javax.swing.text.JTextComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

	public static List<ListElementData> getElementsTypesAndLabels() {
		List<ListElementData> listElementData = new ArrayList<>();
		listElementData.add(new ListElementData(Labels.WORD_IN_KANA,
				new JapaneseWordKanaChecker(), ListElementType.STRING_SHORT_WORD, Labels.COMBOBOX_OPTION_SEARCH_BY_KANA));
		listElementData.add(new ListElementData(Labels.WORD_IN_KANJI,
				new JapaneseWordKanjiChecker(), ListElementType.STRING_SHORT_WORD, Labels.COMBOBOX_OPTION_SEARCH_BY_KANJI));
		listElementData.add(new ListElementData(Labels.WORD_MEANING,
				new JapaneseWordMeaningChecker(), ListElementType.STRING_SHORT_WORD, Labels.COMBOBOX_OPTION_SEARCH_BY_WORD_MEANING));

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

	@Override public boolean isSameAs(ListElement element) {
		if (element instanceof JapaneseWordInformation){
			JapaneseWordInformation otherWord = (JapaneseWordInformation)element;
			return otherWord.getWordInKana().equals(wordInKana) && otherWord.getWordInKanji()
					.equals(wordInKanji);
		}
		return false;
	}

}

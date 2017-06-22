package com.kanji.row;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.kanji.myList.MyList;
import com.kanji.myList.RowInKanjiInformations;

public class KanjiWords implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7410245829899794103L;
	private List<KanjiInformation> kanjiWords;
	private transient MyList<KanjiWords> list;
	private transient RowInKanjiInformations rowMaker;

	public KanjiWords(MyList<KanjiWords> list) {
		this.list = list;
		kanjiWords = new ArrayList<KanjiInformation>();
		initialize();
	}

	public void initialize() {
		rowMaker = new RowInKanjiInformations(list);
	}

	public boolean isWordDefined(String word) {
		for (int i = 0; i < kanjiWords.size(); i++) {
			if (kanjiWords.get(i).getKanjiKeyword().equals(word))
				return true;
		}
		return false;

	}

	public boolean isIdDefined(int id) {
		for (int i = 0; i < kanjiWords.size(); i++) {
			if (kanjiWords.get(i).getKanjiID() == id)
				return true;
		}
		return false;
	}

	public String getWordForId(int id) {
		for (int i = 0; i < kanjiWords.size(); i++) {
			if (kanjiWords.get(i).getKanjiID() == id)
				return kanjiWords.get(i).getKanjiKeyword();
		}
		return "";
	}

	public int getIdOfTheWord(String word) {
		for (int i = 0; i < kanjiWords.size(); i++) {
			if (kanjiWords.get(i).getKanjiKeyword().equals(word))
				return kanjiWords.get(i).getKanjiID();
		}
		return -1;
	}

	public void changeWord(String oldWord, String newWord) {
		for (int i = 0; i < kanjiWords.size(); i++) {
			if (kanjiWords.get(i).getKanjiKeyword().equals(oldWord))
				kanjiWords.get(i).setKanjiKeyword(newWord);
		}
	}

	public void changeWord(int oldInt, int newInt) {
		for (int i = 0; i < kanjiWords.size(); i++) {
			if (kanjiWords.get(i).getKanjiID() == oldInt)
				kanjiWords.get(i).setKanjiID(newInt);
		}
	}

	public void addNewRow(String word, int id) {
		addRow(word, id, kanjiWords.size() + 1);
	}

	public void addRow(KanjiInformation row, int rowNumber) {

		if (!kanjiWords.contains(row))
			kanjiWords.add(row);
		JPanel panel = rowMaker.addWord(row, rowNumber);
		list.addWord(panel);
	}

	public void addRow(String word, int id, int rowNumber) {
		addRow(new KanjiInformation(word, id), rowNumber);
	}

	public void addAll() {

		for (int i = 0; i < kanjiWords.size(); i++) {
			addRow(kanjiWords.get(i), i + 1);
		}
	}

	public int getNumberOfKanjis() {
		return kanjiWords.size();
	}

	public void setList(MyList<KanjiWords> list) {
		this.list = list;
	}

	public List<KanjiInformation> getAllWords() {
		return kanjiWords;
	}

	public void remove(KanjiInformation kanji) {
		kanjiWords.remove(kanji);
	}

}

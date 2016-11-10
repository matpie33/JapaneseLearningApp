package com.kanji.Row;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.kanji.myList.MyList;
import com.kanji.myList.RowWithDeleteButton;

public class KanjiWords implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7410245829899794103L;
	private List <KanjiInformation> kanjiWords;
	private transient MyList <KanjiWords> list;
	private transient RowWithDeleteButton rowMaker;
	
	public KanjiWords(MyList <KanjiWords> list){
		this.list=list;
		kanjiWords = new ArrayList <KanjiInformation> ();
		initialize();
	}
	
	public void initialize(){		
		rowMaker = new RowWithDeleteButton(list);		
	}
	
	public boolean isWordDefined(String word){
		for (int i=0; i<kanjiWords.size(); i++){
			if (kanjiWords.get(i).getKanjiKeyword().equals(word))
				return true;
		}
		return false;
			
	}
	
	public boolean isIdDefined(int id){
		for (int i=0; i<kanjiWords.size(); i++){
			if (kanjiWords.get(i).getKanjiID()==id)
				return true;
		}
		return false;			
	}
	
	public String getWordForId(int id){
		for (int i=0; i<kanjiWords.size(); i++){
			if (kanjiWords.get(i).getKanjiID()==id)
				return kanjiWords.get(i).getKanjiKeyword();
		}
		return "";
	}
	  
	public int getIdOfTheWord(String word){
		for (int i=0; i<kanjiWords.size(); i++){
			if (kanjiWords.get(i).getKanjiKeyword().equals(word))
				return kanjiWords.get(i).getKanjiID();
		}
		return -1;
	}
	
	public void changeWord(String oldWord, String newWord){
		for (int i=0; i<kanjiWords.size(); i++){
			if (kanjiWords.get(i).getKanjiKeyword().equals(oldWord))
				kanjiWords.get(i).setKanjiKeyword(newWord);
		}
	}
	
	public void changeWord(int oldInt, int newInt){
		for (int i=0; i<kanjiWords.size(); i++){
			if (kanjiWords.get(i).getKanjiID()==oldInt)
				kanjiWords.get(i).setKanjiID(newInt);
		}
	}
	
	public void addRow(KanjiInformation row){
		
		if (!kanjiWords.contains(row))
			kanjiWords.add(row);
		JPanel panel = rowMaker.addWord(row, kanjiWords.size());
		list.addWord(panel);
	}
	
	public void addRow (String word, int id){
		addRow(new KanjiInformation(word, id));		
	}
	
	public void addAll(){
		for (int i=0; i<kanjiWords.size();i++){
			addRow(kanjiWords.get(i));
		}
	}
	
	public int getKanjis(){
		return kanjiWords.size();
	}
	
	public void setList (MyList <KanjiWords> list){
		this.list=list;
	}

}

package com.kanji.Row;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.kanji.myList.ListContentsManager;
import com.kanji.myList.MyList;
import com.kanji.myList.RowInKanjiInformations;
import com.kanji.myList.RowsCreator;

public class KanjiWords implements ListContentsManager <KanjiInformation>,Serializable {
	
	private static final long serialVersionUID = 7410245829899794103L;
	private List <KanjiInformation> kanjiWords;
	private transient MyList <KanjiInformation> list;
	private transient RowsCreator<KanjiInformation> rowMaker;
	
	public KanjiWords(MyList <KanjiInformation> list){
		this.list=list;
		kanjiWords = new ArrayList <KanjiInformation> ();
		initialize();
	}
	
	public void initialize(){		
		rowMaker = new RowInKanjiInformations(list);		
	}
	
	public boolean isWordDefined(String word){
		for (int i=0; i<kanjiWords.size(); i++){
			if (kanjiWords.get(i).getKanjiKeyword().equals(word)){
				System.out.println("WORD DEFINED");
				return true;
			}
		}
		System.out.println("WORD UNDEFINED");
		return false;
			
	}
	
	public boolean isIdDefined(int id){
		for (int i=0; i<kanjiWords.size(); i++){
			if (kanjiWords.get(i).getKanjiID()==id){
				System.out.println("ID DEFINED");
				return true;
			}
		}
		System.out.println("ID UNDEFINED");
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
	
	public void addNewRow(String word, int id){
		addRow(word,id);
	}
	
	public void addRow(KanjiInformation row){
		
		if (!kanjiWords.contains(row))
			kanjiWords.add(row);
		JPanel panel = rowMaker.addWord(row);
	
	}
	
	public void addRow (String word, int id){
		addRow(new KanjiInformation(word, id));		
	}
	
	public void addAll(){

		for (int i=0; i<kanjiWords.size();i++){
			addRow(kanjiWords.get(i));
		}
	}
	
	public int getNumberOfWords(){
		return kanjiWords.size();
	}
	
	public void setList (MyList <KanjiInformation> list){
		this.list=list;
	}
	
	@Override
	public List <KanjiInformation> getAllWords(){
		return kanjiWords;
	}
	
	public int remove (KanjiInformation kanji) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		int i = kanjiWords.indexOf(kanji);
		kanjiWords.remove(kanji);
		rowMaker.removeRow(i);
		return i;
		
	}
	
	public boolean isInputValid (String word, int kanjiId){
		return !isIdDefined(kanjiId) && !isWordDefined(word);
	}
	
	public JPanel getPanel(){
		return rowMaker.getPanel();
	}
	
	public RowsCreator getRowsCreator(){
		return rowMaker;
	}




	
}

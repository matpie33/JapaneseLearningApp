package com.kanji.Row;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JPanel;

import com.kanji.myList.MyList;
import com.kanji.myList.RowInRepeatingList;
import com.kanji.myList.RowInKanjiInformations;

public class RepeatingList implements Serializable {

	private List <RepeatingInformation> repeatingList;
	private transient MyList <RepeatingList> list;
	private transient RowInRepeatingList rowMaker;
	
	
	public RepeatingList (MyList <RepeatingList> list){
		this.list=list;
		repeatingList = new ArrayList <RepeatingInformation>();
		initialize();
	}
	public void initialize(){		
		rowMaker = new RowInRepeatingList(list);		
	}
	
	public void add (RepeatingInformation r){
		if (!repeatingList.contains(r))
			repeatingList.add(r);
		System.out.println(r);
		JPanel panel = rowMaker.addWord(r, repeatingList.size());
		list.addWord(panel);
	
	}
	
	public void add (String range, Date date, boolean wasRepeated){
		RepeatingInformation rep = new RepeatingInformation(range, date, wasRepeated);
		add(rep);
	}
	
	public void addAll(){
		for (int i=0; i<repeatingList.size();i++){
			add(repeatingList.get(i));
			System.out.println(repeatingList.get(i).getRepeatingRange());
		}
	}
	public void setList (MyList<RepeatingList> list){
		this.list=list;
	}
	
	public void remove (RepeatingInformation r){
		repeatingList.remove(r);
	}
	
	
}

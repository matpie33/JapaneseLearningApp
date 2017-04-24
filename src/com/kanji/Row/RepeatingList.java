package com.kanji.Row;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.kanji.myList.ListContentsManager;
import com.kanji.myList.MyList;
import com.kanji.myList.RowInRepeatingList;
import com.kanji.myList.RowsCreator;

public class RepeatingList implements ListContentsManager <RepeatingInformation>,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3144332338336535803L;
	private List <RepeatingInformation> repeatingList;
	private transient MyList <RepeatingInformation> list;
	private transient RowInRepeatingList rowMaker;
	
	
	public RepeatingList (MyList <RepeatingInformation> list){
		this.list=list;
		repeatingList = new ArrayList <RepeatingInformation>();
		initialize();
	}
	public void initialize(){		
		rowMaker = new RowInRepeatingList(list);		
	}
	
	@Override
	public void addRow (RepeatingInformation r){
		if (!repeatingList.contains(r))
			repeatingList.add(r);
		System.out.println(r);
		JPanel panel = rowMaker.addWord(r);
//		list.addWord(panel,r);
	
	}
	
	
	public void addAll(){
		for (int i=0; i<repeatingList.size();i++){
			addRow(repeatingList.get(i));
			System.out.println(repeatingList.get(i).getRepeatingRange());
		}
	}
	public void setList (MyList<RepeatingInformation> list){
		this.list=list;
	}
	
	public int remove (RepeatingInformation r){
		repeatingList.remove(r);
		return 1;
	}
	
	@Override
	public int getNumberOfWords() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public List<RepeatingInformation> getAllWords() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public JPanel getPanel(){
		return rowMaker.getPanel();
	}
	
	public RowsCreator getRowsCreator(){
		return rowMaker;
	}
	
	
}

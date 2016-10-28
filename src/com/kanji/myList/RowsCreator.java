package com.kanji.myList;

import java.io.Serializable;

import javax.swing.JPanel;
import javax.swing.JTextArea;

public abstract class RowsCreator implements Serializable  {
	
	public abstract JPanel addWord(String word, int rowsNumber);
	public abstract void setList (MyList list);

	

}

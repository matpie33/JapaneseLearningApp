package com.kanji.myList;

import java.io.Serializable;

import javax.swing.JPanel;
import javax.swing.JTextArea;

public abstract class RowsCreator<Row> implements Serializable  {
	
	public abstract JPanel addWord(Row row, int rowsNumber);
	public abstract void setList (MyList list);

	

}

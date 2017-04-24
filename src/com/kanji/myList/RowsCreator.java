package com.kanji.myList;

import java.io.Serializable;

import javax.swing.JPanel;

public interface RowsCreator<Row> extends Serializable  {
	
	public JPanel addWord(Row row);
	public void setList (MyList<Row> list);
	public JPanel getPanel();
	public void removeRow (int rowNumber) throws ClassNotFoundException, InstantiationException, IllegalAccessException;
	public JPanel getRow(int number);
	

}

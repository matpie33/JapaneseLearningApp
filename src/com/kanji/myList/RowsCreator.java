package com.kanji.myList;

import javax.swing.JPanel;
import javax.swing.JTextArea;

public interface RowsCreator {
	
	public JPanel addWord(String word, int rowsNumber);
	public void setList (MyList list);


}

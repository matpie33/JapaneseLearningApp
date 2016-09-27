package com.kanji.myList;

import javax.swing.JPanel;
import javax.swing.JTextArea;

public interface RowsCreator {
	
	public void addWord(String word);
	public Object findElementInsideOrCreate(JPanel panel, Class classs) throws ClassNotFoundException,
										InstantiationException, IllegalAccessException;
	public void setList (MyList list);

}

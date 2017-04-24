package com.kanji.myList;

import java.util.List;

import javax.swing.JPanel;

public interface ListContentsManager<ListContents>{
	public void addRow (ListContents contents);
//	public void setList (MyList <? extends ListContentsManager<ListContents>> list);
	public int remove (ListContents contents) throws ClassNotFoundException, InstantiationException, IllegalAccessException;
	public void addAll();
	public int getNumberOfWords();
	public List <ListContents> getAllWords();
	public JPanel getPanel();
	public RowsCreator getRowsCreator();
}

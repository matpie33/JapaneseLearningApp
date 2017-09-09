package com.kanji.myList;

import javax.swing.JLabel;

import com.guimaker.panels.MainPanel;

public interface ListRowMaker<Word> {
	public MainPanel createListRow(Word data, JLabel rowNumberLabel);

	public void setList(MyList<Word> list);
}

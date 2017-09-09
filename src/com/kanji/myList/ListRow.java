package com.kanji.myList;

import javax.swing.JLabel;

import com.guimaker.panels.MainPanel;

public interface ListRow<Data> {
	public MainPanel listRow(Data data, JLabel rowNumberLabel);

	public void setList(MyList<Data> list);
}

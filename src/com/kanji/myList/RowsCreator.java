package com.kanji.myList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

public interface RowsCreator<Row> {

	public JPanel createRow(Row row);

	public ListWordsController<Row> getController();

	public void setList(MyList<Row> list);

	public JPanel getPanel();

	public void highlightRowAndScroll(int rowNumber);

	public int getHighlightedRowNumber();

	public void scrollToBottom();

	public JScrollPane getScrollPane();

}

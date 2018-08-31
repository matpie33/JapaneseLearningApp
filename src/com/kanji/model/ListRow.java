package com.kanji.model;

import com.guimaker.panels.MainPanel;

import javax.swing.*;

public class ListRow<Word> {

	private Word word;
	private MainPanel wrappingPanel;
	private JLabel indexLabel;
	private boolean highlighted;
	private int rowNumber;

	public ListRow(Word word, MainPanel wrappingPanel, JLabel indexLabel,
			int rowNumber) {
		this.word = word;
		this.wrappingPanel = wrappingPanel;
		this.indexLabel = indexLabel;
		this.rowNumber = rowNumber;
	}

	public Word getWord() {
		return word;
	}

	public JComponent getJPanel() {
		return wrappingPanel.getPanel();
	}

	public MainPanel getWrappingPanel() {
		return wrappingPanel;
	}

	public JLabel getIndexLabel() {
		return indexLabel;
	}

	public boolean isHighlighted() {
		return highlighted;
	}

	public boolean isShowing() {
		return wrappingPanel != null;
	}

	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public void decrementRowNumber() {
		rowNumber--;
	}

	public void setPanel(MainPanel panel) {
		this.wrappingPanel = panel;
	}
}

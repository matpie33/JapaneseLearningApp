package com.kanji.model;

import com.guimaker.panels.MainPanel;

import javax.swing.*;

public class ListRow<Word> {

	private Word word;
	private JComponent panel;
	private JLabel indexLabel;
	private boolean highlighted;

	public ListRow(Word word, JComponent panel, JLabel indexLabel) {
		this.word = word;
		this.panel = panel;
		this.indexLabel = indexLabel;
	}

	public Word getWord() {
		return word;
	}

	public JComponent getPanel() {
		return panel;
	}

	public JLabel getIndexLabel() {
		return indexLabel;
	}

	public void setIndexLabel(JLabel label) {
		indexLabel = label;
	}

	public boolean isHighlighted() {
		return highlighted;
	}

	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}


}

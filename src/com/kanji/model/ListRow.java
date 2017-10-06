package com.kanji.model;

import javax.swing.JComponent;
import javax.swing.JLabel;

public class ListRow<Word> {

	private Word word;
	private JComponent panel;
	private JLabel indexLabel;

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

}

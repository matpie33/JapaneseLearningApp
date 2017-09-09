package com.kanji.model;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class ListRow<Word> {

	private Word word;
	private JPanel panel;
	private JLabel indexLabel;

	public ListRow(Word word, JPanel panel, JLabel indexLabel) {
		this.word = word;
		this.panel = panel;
		this.indexLabel = indexLabel;
	}

	public Word getWord() {
		return word;
	}

	public JPanel getPanel() {
		return panel;
	}

	public JLabel getIndexLabel() {
		return indexLabel;
	}

	public void setIndexLabel(JLabel label) {
		indexLabel = label;
	}

}

package com.kanji.list.myList;

import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRow;
import com.kanji.range.Range;

import javax.swing.*;

public class LoadNextWordsHandler implements LoadWordsHandler {
	private ListWordsController listWordsController;
	private MainPanel rowsPanel;
	//TODO move to "loadAdditionalWordsHandling" package

	public LoadNextWordsHandler(ListWordsController listWordsController,
			MainPanel rowsPanel) {
		this.listWordsController = listWordsController;
		this.rowsPanel = rowsPanel;
	}

	@Override
	public void addWord() {
		listWordsController.showNextWord(this);
	}

	@Override
	public Range getRangeOfWordsToRemove(int numberOfAddedWords) {
		return new Range(1, numberOfAddedWords);
	}

	@Override
	public JComponent showWord(SimpleRow simpleRow) {
		return rowsPanel.insertRow(rowsPanel.getNumberOfRows() - 1, simpleRow);
	}

	@Override
	public boolean shouldContinue(int lastRowVisible,
			int allWordsToRowNumberMapSize) {
		return lastRowVisible < allWordsToRowNumberMapSize;
	}

	@Override
	public void enableOrDisableLoadWordsButtons(
			AbstractButton buttonLoadNextWords,
			AbstractButton buttonLoadPreviousWords,
			boolean hasMoreWordsToShow) {
		if (!hasMoreWordsToShow) {
			buttonLoadNextWords.setEnabled(false);
		}
		else if (!buttonLoadPreviousWords.isEnabled()) {
			buttonLoadPreviousWords.setEnabled(true);
		}
	}
}

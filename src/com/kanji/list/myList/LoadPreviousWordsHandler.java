package com.kanji.list.myList;

import com.guimaker.panels.MainPanel;
import com.guimaker.row.AbstractSimpleRow;
import com.kanji.range.Range;

import javax.swing.*;

public class LoadPreviousWordsHandler implements LoadWordsHandler {

	private MainPanel rowsPanel;
	private ListWordsController listWordsController;

	public LoadPreviousWordsHandler(ListWordsController listWordsController,
			MainPanel rowsPanel) {
		this.rowsPanel = rowsPanel;
		this.listWordsController = listWordsController;
	}

	@Override
	public void addWord() {
		listWordsController.showPreviousWord(this);
	}

	@Override
	public Range getRangeOfWordsToRemove(int numberOfAddedWords) {
		int lastRowIndex = rowsPanel.getNumberOfRows() - 2;
		return new Range(lastRowIndex - numberOfAddedWords + 1, lastRowIndex);
	}

	@Override
	public JComponent showWord(AbstractSimpleRow abstractSimpleRow) {
		return rowsPanel.insertRow(1, abstractSimpleRow);
	}

	@Override
	public boolean shouldContinue(int lastRowVisible,
			int allWordsToRowNumbersMapSize) {
		return listWordsController.getFirstVisibleRowNumber() >= 0;
	}

	@Override
	public void enableOrDisableLoadWordsButtons(
			AbstractButton buttonLoadNextWords,
			AbstractButton buttonLoadPreviousWords,
			boolean hasMoreWordsToShow) {
		if (!hasMoreWordsToShow) {
			buttonLoadPreviousWords.setEnabled(false);
		}
		else if (!buttonLoadNextWords.isEnabled()) {
			buttonLoadNextWords.setEnabled(true);
		}
	}
}

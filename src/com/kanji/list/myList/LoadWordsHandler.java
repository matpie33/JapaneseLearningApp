package com.kanji.list.myList;

import com.guimaker.row.SimpleRow;
import com.kanji.range.Range;

import javax.swing.*;

public interface LoadWordsHandler {
	public void addWord();

	public Range getRangeOfWordsToRemove(int numberOfAddedWords);

	public JComponent showWord(SimpleRow simpleRow);

	public boolean shouldContinue(int lastRowVisible,
			int allWordsToRowNumberMapSize);
}

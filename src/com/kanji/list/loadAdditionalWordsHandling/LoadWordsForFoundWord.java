package com.kanji.list.loadAdditionalWordsHandling;

import com.guimaker.utilities.Range;

public interface LoadWordsForFoundWord {
	public boolean isApplicable(int foundWordRowNumber,
			Range visibleWordsRange);

	public void execute();
}

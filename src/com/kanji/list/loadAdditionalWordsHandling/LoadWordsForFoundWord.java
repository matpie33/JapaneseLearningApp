package com.kanji.list.loadAdditionalWordsHandling;

import com.kanji.range.Range;

public interface LoadWordsForFoundWord {
	public boolean isApplicable(int foundWordRowNumber, Range visibleWordsRange);

	public void execute();
}

package com.kanji.list.loadAdditionalWordsHandling;

import com.kanji.range.Range;

public class FoundWordInsideVisibleRangeStrategy implements LoadWordsForFoundWord {
	@Override
	public boolean isApplicable(int foundWordRowNumber, Range visibleWordsRange ) {
		return visibleWordsRange.isValueInsideRange(foundWordRowNumber);
	}

	@Override
	public void execute() {
		return;
	}
}

package com.kanji.list.loadAdditionalWordsHandling;

import com.kanji.list.myList.ListWordsController;
import com.kanji.list.myList.LoadNextWordsHandler;
import com.kanji.list.myList.LoadPreviousWordsHandler;
import com.kanji.list.myList.LoadWordsHandler;
import com.guimaker.utilities.Range;

public class FoundWordInsideVisibleRangePlusMaximumWordsStrategy
		implements LoadWordsForFoundWord {

	private boolean wordAboveRange;
	private boolean wordBelowRange;
	private int distanceFromLastRow;
	private int distanceFromFirstRow;
	private int maximumWordsDisplayed;
	private ListWordsController listWordsController;
	private LoadNextWordsHandler loadNextWordsHandler;
	private LoadPreviousWordsHandler loadPreviousWordsHandler;

	public FoundWordInsideVisibleRangePlusMaximumWordsStrategy(
			int maximumWordsDisplayed, ListWordsController listWordsController,
			LoadPreviousWordsHandler loadPreviousWordsHandler,
			LoadNextWordsHandler loadNextWordsHandler) {
		this.maximumWordsDisplayed = maximumWordsDisplayed;
		this.listWordsController = listWordsController;
		this.loadNextWordsHandler = loadNextWordsHandler;
		this.loadPreviousWordsHandler = loadPreviousWordsHandler;
	}

	@Override
	public boolean isApplicable(int foundWordRowNumber,
			Range visibleWordsRange) {
		distanceFromLastRow = Math
				.abs(foundWordRowNumber - visibleWordsRange.getRangeEnd());
		distanceFromFirstRow = Math.
				abs(foundWordRowNumber - visibleWordsRange.getRangeStart());
		wordAboveRange = distanceFromLastRow < maximumWordsDisplayed;
		wordBelowRange = distanceFromFirstRow < maximumWordsDisplayed;
		return wordAboveRange || wordBelowRange;
	}

	@Override
	public void execute() {
		LoadWordsHandler loadWordsHandler = null;
		int numberOfWordsToLoad = 0;
		if (wordAboveRange) {
			loadWordsHandler = loadNextWordsHandler;
			numberOfWordsToLoad = distanceFromLastRow;
		}
		else if (wordBelowRange) {
			loadWordsHandler = loadPreviousWordsHandler;
			numberOfWordsToLoad = distanceFromFirstRow;
		}
		else {
			throw new IllegalStateException("Impossible state");
		}
		listWordsController.removeRowsFromRangeInclusive(
				loadWordsHandler.getRangeOfWordsToRemove(numberOfWordsToLoad));
		listWordsController
				.addSuccessiveWords(loadWordsHandler, numberOfWordsToLoad);
	}
}

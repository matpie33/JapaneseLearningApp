package com.kanji.model;

import java.util.Comparator;

public class FilteredWordMatch implements Comparable {

	private double averagePercentageOfLettersMatched;

	private int numberOfUnmatchedWords;

	public FilteredWordMatch(double averagePercentageOfLettersMatched,
			int numberOfUnmatchedWords) {
		this.averagePercentageOfLettersMatched = averagePercentageOfLettersMatched;
		this.numberOfUnmatchedWords = numberOfUnmatchedWords;
	}

	public double getAveragePercentageOfLettersMatched() {
		return averagePercentageOfLettersMatched;
	}

	public int getNumberOfUnmatchedWords() {
		return numberOfUnmatchedWords;
	}

	@Override
	public int compareTo(Object o) {
		if (!getClass().equals(o.getClass())) {
			return -1;
		}
		FilteredWordMatch other = (FilteredWordMatch) o;

		return Comparator.comparing(
				FilteredWordMatch::getNumberOfUnmatchedWords).reversed()
				.thenComparing(FilteredWordMatch::getAveragePercentageOfLettersMatched)
				.compare(this, other);
	}
}

package com.kanji.panelsAndControllers.controllers;

import com.kanji.list.listElements.ListElement;
import com.kanji.list.listElements.RepeatingData;
import com.kanji.list.myList.MyList;
import com.kanji.range.Range;
import com.kanji.range.SetOfRanges;
import com.kanji.repeating.RepeatingWordDisplayer;
import com.kanji.saving.RepeatingState;
import com.kanji.timer.TimeSpent;

import javax.swing.*;
import java.util.*;

public class WordSpecificRepeatingController<Word extends ListElement> {

	private Word currentWord;
	private Word previousWord;
	private MyList<Word> wordsList;
	private List<Word> wordsLeftToRepeat;
	private RepeatingWordDisplayer<Word> wordDisplayer;

	public WordSpecificRepeatingController(MyList<Word> wordsList,
			RepeatingWordDisplayer wordDisplayer) {
		wordsLeftToRepeat = new ArrayList<>();
		this.wordsList = wordsList;
		this.wordDisplayer = wordDisplayer;
	}

	public void collectSelectedWordsToList(SetOfRanges rangesOfRowNumbers) {
		for (Range range : rangesOfRowNumbers.getRangesAsList()) {
			if (!range.isEmpty()) {
				for (int i = range.getRangeStart();
					 i <= range.getRangeEnd(); i++) {
					wordsLeftToRepeat.add(wordsList.getWordInRow(i));
				}
			}
		}
	}

	public void addProblematicWordsToList(Set<Word> problematicWords) {
		for (Word word : problematicWords) {
			if (!wordsLeftToRepeat.contains(word)) {
				wordsLeftToRepeat.add(word);
			}
		}
	}

	public void setListOfAllProblematicWords(Set<Word> problematicWords) {
		wordDisplayer.setListOfAllProblematicWords(problematicWords);
	}

	public JPanel getWordGuessingPanel() {
		return wordDisplayer.getWordGuessingPanel();
	}

	public JPanel getWordAssessmentPanel() {
		return wordDisplayer.getWordAssessmentPanel();
	}

	public void addWordsToRepeat(Set<Word> currentlyRepeatedWords) {
		wordsLeftToRepeat.addAll(currentlyRepeatedWords);
	}

	public String getWordHint(Word word) {
		return wordDisplayer.getWordHint(word);
	}

	public int getNumberOfWordsLeft() {
		return this.wordsLeftToRepeat.size();
	}

	public boolean hasProblematicWords() {
		return wordDisplayer.hasProblematicWords();
	}

	public Set<Word> getProblematicWords() {
		return wordDisplayer.getProblematicWords();
	}

	public void reset() {
		wordDisplayer.clearRepeatingData();
		this.wordsLeftToRepeat = new ArrayList<>();
	}

	public Word switchToPreviousWord() {
		currentWord = previousWord;
		wordDisplayer.removeWordFromProblematic(currentWord);
		return previousWord;
	}

	public void showWordAssessmentPanel(Word word) {
		wordDisplayer.showWordAssessmentPanel(word);
	}

	public void markCurrentWordAsRecognized() {
		wordDisplayer.removeWordFromProblematic(currentWord);
	}

	public void removeCurrentWordFromListToRepeat() {
		wordsLeftToRepeat.remove(currentWord);
	}

	public Word pickRandomWord() {
		previousWord = currentWord;
		Random randomizer = new Random();
		int indexOfNextWord = randomizer.nextInt(wordsLeftToRepeat.size());
		currentWord = wordsLeftToRepeat.get(indexOfNextWord);
		return currentWord;

	}

	public void markWordAsProblematic() {
		wordDisplayer.markWordAsProblematic(currentWord);
	}

	public boolean previousWordExists() {
		return previousWord != null;
	}

	public Word getCurrentWord() {
		return currentWord;
	}

	public void showWordGuessingPanel() {
		wordDisplayer.showWordGuessingPanel();
	}

	public RepeatingState getRepeatingState(TimeSpent timeForSerialization,
			RepeatingData repeatingData) {
		return wordDisplayer.getRepeatingState(timeForSerialization,
				repeatingData,
				convertWordsListToSet());
	}

	private Set<Word> convertWordsListToSet() {
		//TODO why don't we serialize list?
		Set<Word> wordsSet = new HashSet<>();
		for (Word word : wordsLeftToRepeat) {
			wordsSet.add(word);
		}
		return wordsSet;
	}

}

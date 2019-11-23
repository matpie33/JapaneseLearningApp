package com.kanji.panelsAndControllers.controllers;

import com.guimaker.list.ListElement;
import com.guimaker.list.myList.MyList;
import com.guimaker.panels.mainPanel.MainPanel;
import com.guimaker.timer.TimeSpent;
import com.guimaker.utilities.Range;
import com.guimaker.utilities.SetOfRanges;
import com.kanji.constants.enums.TypeOfWordForRepeating;
import com.kanji.list.listElements.RepeatingData;
import com.kanji.repeating.RepeatingWordsDisplayer;
import com.kanji.model.saving.RepeatingState;

import java.util.*;

public class WordSpecificRepeatingController<Word extends ListElement> {

	private Word currentWord;
	private Word previousWord;
	private MyList<Word> wordsList;
	private List<Word> wordsLeftToRepeat;
	private RepeatingWordsDisplayer<Word> wordDisplayer;
	private Set<Word> currentProblematicWords;
	private Set<Word> allProblematicWords;

	public WordSpecificRepeatingController(MyList<Word> wordsList,
			RepeatingWordsDisplayer<Word> wordDisplayer) {
		wordsLeftToRepeat = new ArrayList<>();
		this.wordsList = wordsList;
		this.wordDisplayer = wordDisplayer;
		allProblematicWords = new HashSet<>();
		currentProblematicWords = new HashSet<>();
	}

	public RepeatingWordsDisplayer<Word> getWordDisplayer() {
		return wordDisplayer;
	}

	public void setListOfAllProblematicWords(Set<Word> problematicWords) {
		this.allProblematicWords = problematicWords;
	}

	private void markWordAsProblematic(Word word) {
		allProblematicWords.add(word);
		currentProblematicWords.add(word);
	}

	private void removeWordFromProblematic(Word word) {
		allProblematicWords.remove(word);
		currentProblematicWords.remove(word);
	}

	public Set<Word> getProblematicWords() {
		return currentProblematicWords;
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

	public void setCurrentProblematcWords(Set<Word> words){
		currentProblematicWords.addAll(words);
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

	public void reset() {
		clearRepeatingData();
		this.wordsLeftToRepeat = new ArrayList<>();
	}

	public Word switchToPreviousWord() {
		currentWord = previousWord;
		removeWordFromProblematic(currentWord);
		return previousWord;
	}

	public void showFullWordDetailsPanel(Word word, MainPanel wordDataPanel) {
		wordDisplayer.showFullWordDetailsPanel(word, wordDataPanel);
	}

	public void markCurrentWordAsRecognized() {
		removeWordFromProblematic(currentWord);
	}

	private void clearRepeatingData() {
		currentProblematicWords.clear();
		currentWord = null;
		previousWord = null;
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
		markWordAsProblematic(currentWord);
	}

	public boolean previousWordExists() {
		return previousWord != null;
	}

	public Word getCurrentWord() {
		return currentWord;
	}

	public RepeatingState<Word> getRepeatingState(TimeSpent timeSpent,
			RepeatingData repeatingData,
			TypeOfWordForRepeating typeOfWordForRepeating) {
		return new RepeatingState<>(timeSpent, repeatingData,
				currentProblematicWords, convertWordsListToSet(),
				typeOfWordForRepeating);
	}

	public boolean hasProblematicWords() {
		return !currentProblematicWords.isEmpty();
	}

	private Set<Word> convertWordsListToSet() {
		//TODO why don't we serialize list?
		return new HashSet<>(wordsLeftToRepeat);
	}

}

package com.kanji.repeating;

import com.kanji.list.listElements.ListElement;
import com.kanji.list.listElements.RepeatingData;
import com.kanji.saving.RepeatingState;
import com.kanji.timer.TimeSpent;

import javax.swing.*;
import java.util.Set;

public interface RepeatingWordDisplayer<Word extends ListElement> {

	public void showWordAssessmentPanel(Word kanjiInformation);

	public void showWordGuessingPanel();

	public JPanel getWordAssessmentPanel();

	public JPanel getWordGuessingPanel();

	public void markWordAsProblematic(Word kanjiInformation);

	public void removeWordFromProblematic(Word kanjiInformation);

	public String getWordHint(Word word);

	public Set<Word> getProblematicWords();

	public RepeatingState getRepeatingState(TimeSpent timeSpent,
			RepeatingData repeatingData, Set<Word> words);

	public boolean hasProblematicWords();

	public void clearRepeatingData();

	public void setAllProblematicWords(Set<Word> problematicWords);

	public void setCurrentProblematicWords(Set<Word> currentProblematicWords);

}

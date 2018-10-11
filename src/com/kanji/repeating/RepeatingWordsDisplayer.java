package com.kanji.repeating;

import com.kanji.list.listElements.ListElement;
import com.kanji.list.listElements.RepeatingData;
import com.kanji.saving.RepeatingState;
import com.kanji.timer.TimeSpent;

import javax.swing.*;
import java.util.Set;

public interface RepeatingWordsDisplayer<Word extends ListElement> {

	public void showWordAssessmentPanel(Word kanjiInformation);

	public void showWordGuessingPanel();

	public JPanel getWordAssessmentPanel();

	public JPanel getWordGuessingPanel();

	public String getWordHint(Word word);



}

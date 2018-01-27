package com.kanji.repeating;

import com.guimaker.panels.MainPanel;
import com.kanji.list.listElements.KanjiInformation;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.listElements.RepeatingInformation;
import com.kanji.saving.RepeatingState;
import com.kanji.timer.TimeSpent;

import javax.swing.*;
import java.util.List;
import java.util.Set;

public interface RepeatingWordDisplayer <Word extends ListElement> {

	public void showWordFullInformation (Word kanjiInformation);
	public void showRecognizingWordPanel();
	public JPanel getFullInformationPanel ();
	public JPanel getRecognizingWordPanel ();
	public void markWordAsProblematic(Word kanjiInformation);
	public void removeWordFromProblematic (Word kanjiInformation);
	public String getWordHint (Word word);
	public Set<Word> getProblematicWords ();
	public RepeatingState getRepeatingState (TimeSpent timeSpent,
			RepeatingInformation repeatingInformation, Set <Word> words);
	public boolean hasProblematicWords ();
	public void clearRepeatingData();
	public void setAllProblematicWords (Set <Word> problematicWords);
}

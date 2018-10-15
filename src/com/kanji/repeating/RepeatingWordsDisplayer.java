package com.kanji.repeating;

import com.guimaker.panels.MainPanel;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.listElements.RepeatingData;
import com.kanji.saving.RepeatingState;
import com.kanji.timer.TimeSpent;

import javax.swing.*;
import java.util.Set;

public interface RepeatingWordsDisplayer<Word extends ListElement> {

	public void showFullWordDetailsPanel(Word kanjiInformation, MainPanel mainPanel);

	public String getWordHint(Word word);

	public String getUniqueName();



}

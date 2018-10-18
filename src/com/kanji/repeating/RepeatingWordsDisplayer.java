package com.kanji.repeating;

import com.guimaker.panels.MainPanel;
import com.guimaker.list.listElements.ListElement;

public interface RepeatingWordsDisplayer<Word extends ListElement> {

	public void showFullWordDetailsPanel(Word kanjiInformation, MainPanel mainPanel);

	public String getWordHint(Word word);

	public String getUniqueName();



}

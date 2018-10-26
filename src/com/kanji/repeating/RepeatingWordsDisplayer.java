package com.kanji.repeating;

import com.guimaker.list.ListElement;
import com.guimaker.panels.MainPanel;

public interface RepeatingWordsDisplayer<Word extends ListElement> {

	public void showFullWordDetailsPanel(Word kanjiInformation,
			MainPanel mainPanel);

	public String getWordHint(Word word);

	public String getUniqueName();

}

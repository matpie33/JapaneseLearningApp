package com.kanji.list.listRows.japanesePanelCreatingService;

import com.guimaker.panels.MainPanel;
import com.kanji.constants.enums.InputGoal;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;

public interface JapanesePanelCreatingService {

	public JComponent[] addWritingsRow(JapaneseWriting japaneseWriting,
			CommonListElements commonListElements, JapaneseWord japaneseWord,
			InputGoal inputGoal, MainPanel rowPanel);

}

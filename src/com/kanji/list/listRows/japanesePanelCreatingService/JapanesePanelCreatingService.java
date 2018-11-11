package com.kanji.list.listRows.japanesePanelCreatingService;

import com.guimaker.enums.InputGoal;
import com.guimaker.panels.MainPanel;
import com.guimaker.model.CommonListElements;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;

import javax.swing.*;

public interface JapanesePanelCreatingService {

	public JComponent[] addWritingsRow(JapaneseWriting japaneseWriting,
			CommonListElements commonListElements, JapaneseWord japaneseWord,
			InputGoal inputGoal, MainPanel rowPanel);

}

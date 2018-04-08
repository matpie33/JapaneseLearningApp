package com.kanji.list.listRows.japanesePanelCreator;

import com.guimaker.panels.MainPanel;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;

public interface JapanesePanelRowCreatingService {

	public JComponent[] addWritingsRow(
			JapaneseWriting japaneseWriting,
			CommonListElements commonListElements, MainPanel rowPanel);


}

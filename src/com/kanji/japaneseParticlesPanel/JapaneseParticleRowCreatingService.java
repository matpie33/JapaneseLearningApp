package com.kanji.japaneseParticlesPanel;

import com.kanji.list.listElements.JapaneseWord;
import com.kanji.model.WordParticlesData;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;

public interface JapaneseParticleRowCreatingService {
	public JComponent[] createRowElements(WordParticlesData wordParticlesData,
			JapaneseWord japaneseWord, CommonListElements commonListElements);
}

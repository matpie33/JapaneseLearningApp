package com.kanji.japaneseParticlesPanel;

import com.guimaker.model.CommonListElements;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.WordParticlesData;

import javax.swing.*;

public interface JapaneseParticleRowCreatingService {
	public JComponent[] createRowElements(WordParticlesData wordParticlesData,
			JapaneseWord japaneseWord, CommonListElements<WordParticlesData>
			commonListElements);
}

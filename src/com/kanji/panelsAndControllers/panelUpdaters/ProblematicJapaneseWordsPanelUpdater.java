package com.kanji.panelsAndControllers.panelUpdaters;

import com.kanji.model.KanjiData;
import com.kanji.panelsAndControllers.panels.ProblematicJapaneseWordsPanel;

import java.util.List;

public class ProblematicJapaneseWordsPanelUpdater {

	private ProblematicJapaneseWordsPanel problematicJapaneseWordsPanel;

	public ProblematicJapaneseWordsPanelUpdater(
			ProblematicJapaneseWordsPanel problematicJapaneseWordsPanel) {
		this.problematicJapaneseWordsPanel = problematicJapaneseWordsPanel;
	}

	public void addInformationAboutKanjisForGivenWord(List<KanjiData> kanjiDataList){
		problematicJapaneseWordsPanel.createKanjiInformationPanelForKanjiData
				(kanjiDataList);
	}

}

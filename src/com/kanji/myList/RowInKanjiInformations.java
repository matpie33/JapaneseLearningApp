package com.kanji.myList;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.kanji.Row.KanjiInformation;
import com.kanji.constants.Labels;
import com.kanji.listSearching.KanjiIdChecker;
import com.kanji.listSearching.KanjiKeywordChecker;
import com.kanji.utilities.CommonListElements;
import com.kanji.windows.ApplicationWindow;

public class RowInKanjiInformations implements ListRowMaker<KanjiInformation> {
	private MyList<KanjiInformation> list;
	private ApplicationWindow applicationWindow;

	public RowInKanjiInformations(ApplicationWindow applicationWindow) {
		this.applicationWindow = applicationWindow;
	}

	@Override
	public MainPanel createListRow(KanjiInformation kanji, CommonListElements commonListElements) {
		MainPanel panel = new MainPanel(null);
		JLabel kanjiKeyword = GuiMaker.createLabel(Labels.KANJI_KEYWORD_LABEL,
				BasicColors.OCEAN_BLUE);
		JLabel kanjiId = GuiMaker.createLabel(Labels.KANJI_ID_LABEL, Color.WHITE);
		String text = kanji.getKanjiKeyword();
		int ID = kanji.getKanjiID();
		JTextArea wordTextArea = GuiMaker.createTextArea(text, new ListPropertyChangeHandler<>(list,
				applicationWindow, new KanjiKeywordChecker()));
		JTextArea idTextArea = GuiMaker.createTextArea(5, Integer.toString(ID),
				new ListPropertyChangeHandler<>(list, applicationWindow, new KanjiIdChecker()));
		// TODO this should be consistent with what we allow when creating word
		// - insertWordPanel
		JButton remove = commonListElements.getButtonDelete();
		panel.addElementsInColumnStartingFromColumn(wordTextArea, 0,
				commonListElements.getRowNumberLabel(), kanjiKeyword, wordTextArea);
		panel.addElementsInColumnStartingFromColumn(1, kanjiId, idTextArea);
		panel.addElementsInColumnStartingFromColumn(remove, 1, remove);

		return panel;

	}

}

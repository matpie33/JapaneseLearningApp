package com.kanji.myList;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import com.guimaker.enums.FillType;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRow;
import com.kanji.Row.KanjiInformation;
import com.kanji.listSearching.KanjiIdChecker;
import com.kanji.listSearching.KanjiKeywordChecker;
import com.kanji.windows.ApplicationWindow;

public class RowInKanjiInformations implements ListRowMaker<KanjiInformation> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color wordNumberColor = Color.WHITE;
	private ListWordsController<KanjiInformation> listWordsController;
	private MyList<KanjiInformation> list;
	private ApplicationWindow applicationWindow;

	public RowInKanjiInformations(ApplicationWindow applicationWindow) {
		this.applicationWindow = applicationWindow;
	}

	@Override
	public MainPanel createListRow(KanjiInformation kanji, JLabel rowNumberLabel) {
		MainPanel panel = new MainPanel(null);
		String text = kanji.getKanjiKeyword();
		int ID = kanji.getKanjiID();
		rowNumberLabel.setForeground(wordNumberColor);
		JTextArea wordTextArea = GuiMaker.createTextArea(text, new ListPropertyChangeHandler<>(list,
				applicationWindow, new KanjiKeywordChecker()));
		JTextArea idTextArea = GuiMaker.createTextArea(true, 5, Integer.toString(ID),
				new ListPropertyChangeHandler<>(list, applicationWindow, new KanjiIdChecker()));
		// TODO this should be consistent with what we allow when creating word
		// - insertWordPanel
		JButton remove = list.createButtonRemove(kanji);

		panel.addRow(new SimpleRow(FillType.HORIZONTAL, rowNumberLabel, wordTextArea, idTextArea,
				remove));
		return panel;

	}

	@Override
	public void setList(MyList<KanjiInformation> list) {
		this.list = list;
	}

}

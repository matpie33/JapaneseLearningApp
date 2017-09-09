package com.kanji.myList;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import com.guimaker.enums.FillType;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRow;
import com.kanji.Row.KanjiInformation;
import com.kanji.listSearching.KanjiIdChecker;
import com.kanji.listSearching.KanjiKeywordChecker;
import com.kanji.listSearching.SearchOptions;
import com.kanji.listSearching.SearchingDirection;

public class RowInKanjiInformations implements ListRowMaker<KanjiInformation> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color wordNumberColor = Color.WHITE;
	private ListWordsController<KanjiInformation> listWordsController;

	private String wordBeingModified;
	private int idBeingModified;
	private MyList<KanjiInformation> list;

	@Override
	public MainPanel createListRow(KanjiInformation kanji, JLabel rowNumberLabel) {
		MainPanel panel = new MainPanel(null);
		String text = kanji.getKanjiKeyword();
		int ID = kanji.getKanjiID();
		rowNumberLabel.setForeground(wordNumberColor);
		// TODO looks fishy to pass class type as argument to create text area
		JTextArea wordTextArea = createTextArea(text, String.class);
		JTextArea idTextArea = createTextArea(Integer.toString(ID), Integer.class);
		JButton remove = list.createButtonRemove(kanji);

		panel.addRow(new SimpleRow(FillType.HORIZONTAL, rowNumberLabel, wordTextArea, idTextArea,
				remove));
		return panel;

	}

	private JTextArea createTextArea(String text, Class type) {
		JTextArea elem = new JTextArea(text, 1, 3);
		FocusListener f;
		if (type == Integer.class)
			f = createIdChangeListener(elem);
		else
			f = createWordChangeListener(elem);
		elem.addFocusListener(f);
		elem.setLineWrap(true);
		elem.setWrapStyleWord(true);
		elem.setOpaque(true);
		return elem;
	}

	private FocusListener createWordChangeListener(final JTextArea elem) {
		FocusListener focusListener = new FocusListener() {
			public void focusGained(FocusEvent e) {
				wordBeingModified = elem.getText();
			}

			public void focusLost(FocusEvent e) {
				if (wordBeingModified.equals(elem.getText())) {
					return;
				}
				KanjiInformation kanjiToChange = list
						.findRowBasedOnPropertyStartingFromHighlightedWord(
								new KanjiKeywordChecker(SearchOptions.BY_FULL_EXPRESSION),
								wordBeingModified, SearchingDirection.FORWARD, list.getParent());
				KanjiInformation newKanji = new KanjiInformation(elem.getText(),
						kanjiToChange.getKanjiID());
				listWordsController.replace(kanjiToChange, newKanji);
				wordBeingModified = "";
				list.save();
			}
		};
		return focusListener;
	}

	private FocusListener createIdChangeListener(final JTextArea elem) {
		FocusListener focusListener = new FocusListener() {
			public void focusGained(FocusEvent e) {
				if (elem.getText().matches("\\d+"))
					idBeingModified = Integer.parseInt(elem.getText());
				// TODO else exception

			}

			public void focusLost(FocusEvent e) {
				int newID;
				if (elem.getText().matches("\\d+")) {
					newID = Integer.parseInt(elem.getText());
				}
				else
					return;
				// TODO this method and above could be generic - for replacing a
				// word
				// TODO else exception

				if (idBeingModified == Integer.parseInt(elem.getText())) {
					return;
				}
				KanjiInformation kanjiToChange = list
						.findRowBasedOnPropertyStartingFromHighlightedWord(new KanjiIdChecker(),
								idBeingModified, SearchingDirection.FORWARD, list.getParent());
				KanjiInformation newKanji = new KanjiInformation(kanjiToChange.getKanjiKeyword(),
						newID);
				listWordsController.replace(kanjiToChange, newKanji);
				list.save();
				idBeingModified = -1;

			}
		};
		return focusListener;
	}

	@Override
	public void setList(MyList<KanjiInformation> list) {
		this.list = list;
	}

}

package com.kanji.myList;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.guimaker.row.Anchor;
import com.guimaker.row.RowMaker;
import com.kanji.Row.KanjiInformation;
import com.kanji.constants.Prompts;
import com.kanji.constants.Titles;
import com.kanji.listSearching.KanjiIdChecker;
import com.kanji.listSearching.KanjiKeywordChecker;
import com.kanji.listSearching.SearchOptions;
import com.kanji.listSearching.SearchingDirection;

public class RowInKanjiInformations extends RowsCreator<KanjiInformation> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color wordNumberColor = Color.WHITE;

	private String wordBeingModified;
	private int idBeingModified;

	// TODO should be common for kanji and repeating list: panel color, border
	// at the bottom,
	public RowInKanjiInformations() {
		JLabel title = new JLabel(Titles.KANJIS_LIST);
		title.setForeground(Color.white);
		panel.addRow(RowMaker.createUnfilledRow(Anchor.CENTER, title));
	}

	@Override
	public JPanel createRow(KanjiInformation kanji) {
		createNewRow(kanji);
		return panel.getPanel();
	}

	private void createNewRow(KanjiInformation kanji) {
		String text = kanji.getKanjiKeyword();
		int ID = kanji.getKanjiID();
		JLabel number = new JLabel("" + (panel.getNumberOfRows()));
		number.setForeground(wordNumberColor);

		// TODO looks fishy to pass class type as argument to create text area
		JTextArea wordTextArea = createTextArea(text, String.class);
		JTextArea idTextArea = createTextArea(Integer.toString(ID), Integer.class);
		JButton remove = createButtonRemove();

		JPanel createdPanel = panel.addRow(
				RowMaker.createHorizontallyFilledRow(number, wordTextArea, idTextArea, remove));
		addRemovingActionToButton(remove, createdPanel, kanji);

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
				System.out.println(list);
				KanjiInformation kanjiToChange = list.findRowBasedOnProperty(
						new KanjiKeywordChecker(SearchOptions.BY_FULL_EXPRESSION),
						wordBeingModified, SearchingDirection.FORWARD, list.getParent());
				KanjiInformation newKanji = new KanjiInformation(elem.getText(),
						kanjiToChange.getKanjiID());
				kanjiWords.replace(kanjiToChange, newKanji);
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
				// TODO else exception

				if (idBeingModified == Integer.parseInt(elem.getText())) {
					return;
				}
				KanjiInformation kanjiToChange = list.findRowBasedOnProperty(new KanjiIdChecker(),
						idBeingModified, SearchingDirection.FORWARD, list.getParent());
				KanjiInformation newKanji = new KanjiInformation(kanjiToChange.getKanjiKeyword(),
						newID);
				kanjiWords.replace(kanjiToChange, newKanji);
				list.save();
				idBeingModified = -1;

			}
		};
		return focusListener;
	}

	private JButton createButtonRemove() {
		JButton remove = new JButton("-");
		return remove;
	}

	private void addRemovingActionToButton(JButton button, JPanel row, KanjiInformation kanji) {
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!list.showMessage(
						String.format(Prompts.deleteElementPrompt, Prompts.kanjiElementPrompt))) {
					return;
				}
				removeRow(row);
				list.getWords().remove(kanji);
				list.save();
			}
		});
	}

	private void removeRow(JPanel row) {
		panel.removeRow(row);
	}

}

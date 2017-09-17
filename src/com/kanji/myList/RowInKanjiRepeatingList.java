package com.kanji.myList;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.kanji.Row.KanjiInformation;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.Labels;
import com.kanji.controllers.ProblematicKanjisController;
import com.kanji.model.KanjiRow;

public class RowInKanjiRepeatingList implements ListRowMaker<KanjiInformation> {

	private ProblematicKanjisController controller;

	public RowInKanjiRepeatingList(ProblematicKanjisController controller) {
		this.controller = controller;
	}

	@Override
	public MainPanel createListRow(KanjiInformation row, JLabel rowNumberLabel) {
		MainPanel panel = new MainPanel(null);
		JLabel id = new JLabel("" + row.getKanjiID());
		id.setForeground(Color.white);
		JTextArea kanjiTextArea = GuiMaker.createTextArea(false, true);
		kanjiTextArea.setText(row.getKanjiKeyword());
		JLabel kanjiKeyword = GuiMaker.createLabel(Labels.KANJI_KEYWORD_LABEL,
				BasicColors.OCEAN_BLUE);
		JLabel kanjiId = GuiMaker.createLabel(Labels.KANJI_ID_LABEL, Color.WHITE);
		int rowNumber = controller.getNumberOfRows();
		controller.addKanjiRow(row.getKanjiID());

		JButton buttonGoToSource = createButtonGoToSource(rowNumber, row.getKanjiID());
		panel.addElementsInColumnStartingFromColumn(kanjiTextArea, 0, rowNumberLabel, kanjiKeyword,
				kanjiTextArea);
		panel.addElementsInColumnStartingFromColumn(1, kanjiId, id);
		panel.addElementsInColumnStartingFromColumn(buttonGoToSource, 1, buttonGoToSource);
		return panel;
	}

	private JButton createButtonGoToSource(int rowNumber, int kanjiId) {
		JButton button = new JButton(ButtonsNames.GO_TO_SOURCE);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.goToSpecifiedResource(new KanjiRow(kanjiId, rowNumber));
			}
		});
		button.setFocusable(false);
		return button;
	}

	public void setList(MyList<KanjiInformation> list) {
		;
	}

}

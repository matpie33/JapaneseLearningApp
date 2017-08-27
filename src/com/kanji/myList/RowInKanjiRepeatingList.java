package com.kanji.myList;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.guimaker.panels.GuiMaker;
import com.guimaker.row.RowMaker;
import com.kanji.Row.KanjiInformation;
import com.kanji.constants.ButtonsNames;
import com.kanji.controllers.ProblematicKanjisController;
import com.kanji.model.KanjiRow;

public class RowInKanjiRepeatingList extends RowsCreator<KanjiInformation> {

	private ProblematicKanjisController controller;

	public RowInKanjiRepeatingList(ProblematicKanjisController controller) {
		this.controller = controller;
	}

	@Override
	public JPanel createRow(KanjiInformation row) {
		JLabel id = new JLabel("" + row.getKanjiID());
		id.setForeground(Color.white);
		JTextArea kanjiTextArea = GuiMaker.createTextArea(false, false);
		kanjiTextArea.setText(row.getKanjiKeyword());
		kanjiTextArea.setForeground(Color.white);
		controller.addKanjiRow(rowsPanel.getNumberOfRows(), row.getKanjiID());
		JButton buttonGoToSource = createButtonGoToSource(rowsPanel.getNumberOfRows(),
				row.getKanjiID());
		rowsPanel.addRow(RowMaker.createHorizontallyFilledRow(kanjiTextArea, id, buttonGoToSource)
				.fillHorizontallySomeElements(kanjiTextArea));
		return this.rowsPanel.getPanel();
	}

	private JButton createButtonGoToSource(int rowNumber, int kanjiId) {
		JButton button = new JButton(ButtonsNames.buttonGoToSource);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.goToSpecifiedResource(new KanjiRow(kanjiId, rowNumber));
			}
		});
		button.setFocusable(false);
		return button;
	}

}

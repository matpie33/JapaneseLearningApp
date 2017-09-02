package com.kanji.myList;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import com.guimaker.enums.FillType;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRow;
import com.kanji.Row.KanjiInformation;
import com.kanji.constants.ButtonsNames;
import com.kanji.controllers.ProblematicKanjisController;
import com.kanji.model.KanjiRow;

public class RowInKanjiRepeatingList implements ListRow<KanjiInformation> {

	private ProblematicKanjisController controller;

	public RowInKanjiRepeatingList(ProblematicKanjisController controller) {
		this.controller = controller;
	}

	@Override
	public MainPanel listRow(KanjiInformation row) {
		MainPanel panel = new MainPanel(null);
		JLabel id = new JLabel("" + row.getKanjiID());
		id.setForeground(Color.white);
		JTextArea kanjiTextArea = GuiMaker.createTextArea(false, false);
		kanjiTextArea.setText(row.getKanjiKeyword());
		kanjiTextArea.setForeground(Color.white);
		controller.addKanjiRow(panel.getNumberOfRows(), row.getKanjiID());
		JButton buttonGoToSource = createButtonGoToSource(panel.getNumberOfRows(),
				row.getKanjiID());
		panel.addRow(new SimpleRow(FillType.HORIZONTAL, kanjiTextArea, id, buttonGoToSource)
				.fillHorizontallySomeElements(kanjiTextArea));
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

}

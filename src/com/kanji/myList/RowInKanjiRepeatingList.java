package com.kanji.myList;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

import com.guimaker.colors.BasicColors;
import com.guimaker.options.ComponentOptions;
import com.guimaker.options.TextAreaOptions;
import com.guimaker.options.TextComponentOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.kanji.Row.KanjiInformation;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.Labels;
import com.kanji.controllers.ProblematicKanjisController;
import com.kanji.model.KanjiRow;
import com.kanji.utilities.CommonListElements;

public class RowInKanjiRepeatingList implements ListRowMaker<KanjiInformation> {

	private ProblematicKanjisController controller;

	public RowInKanjiRepeatingList(ProblematicKanjisController controller) {
		this.controller = controller;
	}

	@Override
	public MainPanel createListRow(KanjiInformation row, CommonListElements commonListElements) {
		MainPanel panel = new MainPanel(null);
		JLabel id = new JLabel("" + row.getKanjiID());
		id.setForeground(Color.white);
		JTextComponent kanjiTextArea = GuiMaker
				.createTextArea(new TextAreaOptions().editable(false).opaque(true));
		kanjiTextArea.setText(row.getKanjiKeyword());
		JLabel kanjiKeyword = GuiMaker.createLabel(new ComponentOptions()
				.text(Labels.KANJI_KEYWORD_LABEL).foregroundColor(BasicColors.OCEAN_BLUE));
		JLabel kanjiId = GuiMaker.createLabel(new ComponentOptions().text(Labels.KANJI_ID_LABEL)
				.foregroundColor(Color.WHITE));
		int rowNumber = controller.getNumberOfRows();
		controller.addKanjiRow(row.getKanjiID());

		JButton buttonGoToSource = createButtonGoToSource(rowNumber, row.getKanjiID());
		panel.addElementsInColumnStartingFromColumn(kanjiTextArea, 0,
				commonListElements.getRowNumberLabel(), kanjiKeyword, kanjiTextArea);
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

}

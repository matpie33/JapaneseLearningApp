package com.kanji.list.listRows;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.FillType;
import com.guimaker.options.ComponentOptions;
import com.guimaker.options.TextAreaOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.Labels;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.myList.ListRowData;
import com.kanji.list.myList.ListRowMaker;
import com.kanji.model.WordRow;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RowInKanjiRepeatingList implements ListRowMaker<Kanji> {

	private ProblematicWordsController controller;

	public RowInKanjiRepeatingList(ProblematicWordsController controller) {
		this.controller = controller;
	}

	@Override
	public MainPanel createListRow(Kanji kanji,
			CommonListElements commonListElements, boolean forSearchPanel) {
		MainPanel panel = new MainPanel(null);
		JLabel id = new JLabel("" + kanji.getId());
		id.setForeground(Color.white);
		JTextComponent kanjiTextArea = GuiMaker.createTextArea(
				new TextAreaOptions().editable(false).opaque(true)
						.rowsAndColumns(2, 5));
		kanjiTextArea.setText(kanji.getKeyword());
		JLabel kanjiKeyword = GuiMaker.createLabel(
				new ComponentOptions().text(Labels.KANJI_KEYWORD_LABEL)
						.foregroundColor(BasicColors.OCEAN_BLUE));
		JLabel kanjiId = GuiMaker.createLabel(
				new ComponentOptions().text(Labels.KANJI_ID_LABEL)
						.foregroundColor(Color.WHITE));
		int rowNumber = controller.getNumberOfRows();

		JButton buttonGoToSource = createButtonGoToSource(rowNumber, kanji);
		panel.addRowsOfElementsInColumnStartingFromColumn(SimpleRowBuilder
				.createRowStartingFromColumn(0, FillType.HORIZONTAL,
						commonListElements.getRowNumberLabel(), kanjiKeyword,
						kanjiTextArea)
				.fillHorizontallySomeElements(kanjiTextArea)
				.nextRow(kanjiId, id).setColumnToPutRowInto(1)
				.nextRow(buttonGoToSource).fillHorizontallyEqually());

		if (forSearchPanel) {
			//TODO to be merged
		}

		return panel;
	}

	//TODO merge this class with rowInKanjiInformations

	private JButton createButtonGoToSource(int rowNumber, Kanji kanji) {
		JButton button = new JButton(ButtonsNames.GO_TO_SOURCE);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.goToSpecifiedResource(new WordRow(kanji, rowNumber));
			}
		});
		button.setFocusable(false);
		return button;
	}

	@Override
	public ListRowData getRowData() {
		return null;
	}
}

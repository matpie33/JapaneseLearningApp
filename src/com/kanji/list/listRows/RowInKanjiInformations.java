package com.kanji.list.listRows;

import com.guimaker.enums.FillType;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.constants.strings.Labels;
import com.kanji.list.listElementPropertyManagers.KanjiIdChecker;
import com.kanji.list.listElementPropertyManagers.KanjiKeywordChecker;
import com.kanji.list.listElements.KanjiInformation;
import com.kanji.list.myList.ListPropertyChangeHandler;
import com.kanji.list.myList.ListRowMaker;
import com.kanji.utilities.CommonGuiElementsMaker;
import com.kanji.utilities.CommonListElements;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class RowInKanjiInformations implements ListRowMaker<KanjiInformation> {
	private ApplicationWindow applicationWindow;

	public RowInKanjiInformations(ApplicationWindow applicationWindow) {
		this.applicationWindow = applicationWindow;
	}

	@Override
	public MainPanel createListRow(KanjiInformation kanji,
			CommonListElements commonListElements) {
		MainPanel panel = new MainPanel(null);
		Color labelsColor = commonListElements.getLabelsColor();
		JLabel kanjiKeyword = GuiMaker.createLabel(
				new ComponentOptions().text(Labels.KANJI_KEYWORD_LABEL)
						.foregroundColor(labelsColor));
		JLabel kanjiId = GuiMaker.createLabel(
				new ComponentOptions().text(Labels.KANJI_ID_LABEL)
						.foregroundColor(labelsColor));
		String text = kanji.getKanjiKeyword();
		int ID = kanji.getKanjiID();
		JTextComponent wordTextArea = CommonGuiElementsMaker
				.createKanjiWordInput(text);

		wordTextArea.addFocusListener(new ListPropertyChangeHandler<>(kanji,
				applicationWindow.getApplicationController().getKanjiList(),
				applicationWindow, new KanjiKeywordChecker(),
				ExceptionsMessages.KANJI_KEYWORD_ALREADY_DEFINED_EXCEPTION,
				true));
		JTextComponent idTextArea = CommonGuiElementsMaker.createKanjiIdInput();
		idTextArea.setText(ID > 0 ? Integer.toString(ID) : "");
		idTextArea.addFocusListener(new ListPropertyChangeHandler<>(kanji,
				applicationWindow.getApplicationController().getKanjiList(),
				applicationWindow, new KanjiIdChecker(),
				ExceptionsMessages.ID_ALREADY_DEFINED_EXCEPTION, true));
		AbstractButton remove = commonListElements.getButtonDelete();
		JLabel rowNumberLabel = commonListElements.getRowNumberLabel();
		panel.addRowsOfElementsInColumnStartingFromColumn(SimpleRowBuilder
				.createRowStartingFromColumn(0, FillType.HORIZONTAL,
						rowNumberLabel, kanjiKeyword, wordTextArea)
				.fillHorizontallySomeElements(wordTextArea)
				.nextRow(kanjiId, idTextArea).setColumnToPutRowInto(1)
				.nextRow(remove));

		return panel;

	}

}

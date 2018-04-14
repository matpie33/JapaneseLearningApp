package com.kanji.list.listRows;

import com.guimaker.enums.FillType;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.NextRow;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.constants.strings.Labels;
import com.kanji.constants.strings.ListPropertiesNames;
import com.kanji.list.listElementPropertyManagers.KanjiIdChecker;
import com.kanji.list.listElementPropertyManagers.KanjiKeywordChecker;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.myList.ListPropertyChangeHandler;
import com.kanji.list.myList.ListPropertyInformation;
import com.kanji.list.myList.ListRowData;
import com.kanji.list.myList.ListRowMaker;
import com.kanji.utilities.CommonGuiElementsMaker;
import com.kanji.utilities.CommonListElements;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class RowInKanjiInformations implements ListRowMaker<Kanji> {
	private ApplicationWindow applicationWindow;

	public RowInKanjiInformations(ApplicationWindow applicationWindow) {
		this.applicationWindow = applicationWindow;
	}

	@Override
	public ListRowData createListRow(Kanji kanji,
			CommonListElements commonListElements, boolean forSearchPanel) {
		MainPanel panel = new MainPanel(null);
		//TODO do it like in rowInJapaneseWordInformations
		Color labelsColor = commonListElements.getLabelsColor();
		JLabel kanjiKeyword = GuiMaker.createLabel(
				new ComponentOptions().text(Labels.KANJI_KEYWORD_LABEL)
						.foregroundColor(labelsColor));
		JLabel kanjiId = GuiMaker.createLabel(
				new ComponentOptions().text(Labels.KANJI_ID_LABEL)
						.foregroundColor(labelsColor));
		String text = kanji.getKeyword();
		int ID = kanji.getId();
		JTextComponent wordTextArea = CommonGuiElementsMaker
				.createKanjiWordInput(text);
		KanjiKeywordChecker keywordChecker = new KanjiKeywordChecker();
		wordTextArea.addFocusListener(new ListPropertyChangeHandler<>(kanji,
				applicationWindow.getApplicationController().getKanjiList(),
				applicationWindow, keywordChecker,
				ExceptionsMessages.KANJI_KEYWORD_ALREADY_DEFINED_EXCEPTION,
				true));
		JTextComponent idTextArea = CommonGuiElementsMaker.createKanjiIdInput();
		idTextArea.setText(ID > 0 ? Integer.toString(ID) : "");
		KanjiIdChecker idChecker = new KanjiIdChecker();
		idTextArea.addFocusListener(new ListPropertyChangeHandler<>(kanji,
				applicationWindow.getApplicationController().getKanjiList(),
				applicationWindow, idChecker,
				ExceptionsMessages.ID_ALREADY_DEFINED_EXCEPTION, true));
		AbstractButton remove = commonListElements.getButtonDelete();
		JLabel rowNumberLabel = commonListElements.getRowNumberLabel();
		NextRow rows = SimpleRowBuilder
				.createRowStartingFromColumn(0, FillType.HORIZONTAL,
						rowNumberLabel, kanjiKeyword, wordTextArea)
				.fillHorizontallySomeElements(wordTextArea)
				.nextRow(kanjiId, idTextArea).setColumnToPutRowInto(1)
				.nextRow(remove);
		panel.addRowsOfElementsInColumnStartingFromColumn(rows);
		ListRowData rowData = new ListRowData(panel);

		if (forSearchPanel) {

			Map<String, ListPropertyInformation> propertyInformations = new HashMap<>();

			Map<JTextComponent, ListElementPropertyManager<?, Kanji>> propertyManagerOfTextFieldsKeyword = new HashMap<>();
			propertyManagerOfTextFieldsKeyword
					.put(wordTextArea, keywordChecker);
			propertyInformations.put(ListPropertiesNames.KANJI_KEYWORD,
					new ListPropertyInformation(rows.getAllRows().get(0),
							propertyManagerOfTextFieldsKeyword));

			Map<JTextComponent, ListElementPropertyManager<?, Kanji>> propertyManagerOfTextFieldsKanjiId = new HashMap<>();

			propertyManagerOfTextFieldsKanjiId.put(idTextArea, idChecker);
			propertyInformations.put(ListPropertiesNames.KANJI_ID,
					new ListPropertyInformation(rows.getAllRows().get(1),
							propertyManagerOfTextFieldsKanjiId));

			rowData.setRowPropertiesData(propertyInformations);
		}

		return rowData;

	}

}

package com.kanji.list.listRows;

import com.guimaker.enums.FillType;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.ComplexRow;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.strings.Labels;
import com.kanji.constants.strings.ListPropertiesNames;
import com.kanji.list.listElementPropertyManagers.KanjiIdChecker;
import com.kanji.list.listElementPropertyManagers.KanjiKeywordChecker;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.myList.ListPropertyChangeHandler;
import com.kanji.list.myList.ListPropertyInformation;
import com.kanji.list.myList.ListRowData;
import com.kanji.list.myList.ListRowCreator;
import com.kanji.utilities.CommonGuiElementsCreator;
import com.kanji.utilities.CommonListElements;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class RowInKanjiInformations implements ListRowCreator<Kanji> {
	private ApplicationWindow applicationWindow;
	private JTextComponent keywordInput;
	private KanjiKeywordChecker keywordChecker;
	private JTextComponent idInput;
	private KanjiIdChecker idChecker;
	private ComplexRow lastPanelMade;

	public RowInKanjiInformations(ApplicationWindow applicationWindow) {
		this.applicationWindow = applicationWindow;
	}

	@Override
	public MainPanel createListRow(Kanji kanji,
			CommonListElements commonListElements, boolean forSearchPanel) {
		MainPanel panel = new MainPanel(null);
		//TODO do it like in rowInJapaneseWordInformations
		Color labelsColor = commonListElements.getLabelsColor();
		JLabel kanjiKeyword = GuiElementsCreator.createLabel(
				new ComponentOptions().text(Labels.KANJI_KEYWORD_LABEL)
						.foregroundColor(labelsColor));
		JLabel kanjiId = GuiElementsCreator.createLabel(
				new ComponentOptions().text(Labels.KANJI_ID_LABEL)
						.foregroundColor(labelsColor));
		String text = kanji.getKeyword();
		int ID = kanji.getId();
		keywordInput = CommonGuiElementsCreator.createKanjiWordInput(text);
		keywordChecker = new KanjiKeywordChecker();
		keywordInput.addFocusListener(new ListPropertyChangeHandler<>(kanji,
				applicationWindow.getApplicationController().getKanjiList(),
				applicationWindow, keywordChecker, true, !forSearchPanel));
		idInput = CommonGuiElementsCreator.createKanjiIdInput();
		idInput.setText(ID > 0 ? Integer.toString(ID) : "");
		idChecker = new KanjiIdChecker();
		idInput.addFocusListener(new ListPropertyChangeHandler<>(kanji,
				applicationWindow.getApplicationController().getKanjiList(),
				applicationWindow, idChecker, true, !forSearchPanel));
		AbstractButton remove = commonListElements.getButtonDelete();
		JLabel rowNumberLabel = commonListElements.getRowNumberLabel();
		lastPanelMade = SimpleRowBuilder
				.createRowStartingFromColumn(0, FillType.HORIZONTAL,
						rowNumberLabel, kanjiKeyword, keywordInput)
				.fillHorizontallySomeElements(keywordInput)
				.nextRow(kanjiId, idInput).setColumnToPutRowInto(1)
				.nextRow(remove);
		panel.addRowsOfElementsInColumnStartingFromColumn(lastPanelMade);

		return panel;

	}

	@Override
	public ListRowData getRowData() {
		ListRowData rowData = new ListRowData();
		Map<String, ListPropertyInformation> propertyInformations = new HashMap<>();

		Map<JTextComponent, ListElementPropertyManager<?, Kanji>> propertyManagerOfKeywordInput = new HashMap<>();
		propertyManagerOfKeywordInput.put(keywordInput, keywordChecker);
		propertyInformations.put(ListPropertiesNames.KANJI_KEYWORD,
				new ListPropertyInformation(lastPanelMade.getAllRows().get(0),
						propertyManagerOfKeywordInput));

		Map<JTextComponent, ListElementPropertyManager<?, Kanji>> propertyManagerOfIdInput = new HashMap<>();

		propertyManagerOfIdInput.put(idInput, idChecker);
		propertyInformations.put(ListPropertiesNames.KANJI_ID,
				new ListPropertyInformation(lastPanelMade.getAllRows().get(1),
						propertyManagerOfIdInput));

		rowData.setRowPropertiesData(propertyInformations);
		return rowData;
	}
}

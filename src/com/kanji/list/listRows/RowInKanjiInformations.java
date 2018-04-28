package com.kanji.list.listRows;

import com.guimaker.enums.FillType;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.ComplexRow;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.InputGoal;
import com.kanji.constants.enums.WordSearchOptions;
import com.kanji.constants.strings.Labels;
import com.kanji.constants.strings.ListPropertiesNames;
import com.kanji.list.listElementPropertyManagers.KanjiIdChecker;
import com.kanji.list.listElementPropertyManagers.KanjiKeywordChecker;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.myList.*;
import com.kanji.utilities.CommonGuiElementsCreator;
import com.kanji.utilities.CommonListElements;
import com.kanji.utilities.Pair;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class RowInKanjiInformations implements ListRowCreator<Kanji> {
	private ApplicationWindow applicationWindow;
	private Set<InputValidationListener<Kanji>> validationListeners = new HashSet<>();

	public RowInKanjiInformations(ApplicationWindow applicationWindow) {
		this.applicationWindow = applicationWindow;
	}

	@Override
	public ListRowData createListRow(Kanji kanji,
			CommonListElements commonListElements, InputGoal inputGoal) {
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
		JTextComponent keywordInput = CommonGuiElementsCreator
				.createKanjiWordInput(text);
		KanjiKeywordChecker keywordChecker = new KanjiKeywordChecker();
		keywordChecker.setWordSearchOptions(WordSearchOptions.BY_WORD_FRAGMENT);
		ListPropertyChangeHandler<String, Kanji> keywordChangeHandler = new ListPropertyChangeHandler<>(
				kanji,
				applicationWindow.getApplicationController().getKanjiList(),
				applicationWindow, keywordChecker, true, inputGoal);
		validationListeners.forEach(keywordChangeHandler::addValidationListener);
		keywordInput.addFocusListener(keywordChangeHandler);
		JTextComponent idInput = CommonGuiElementsCreator.createKanjiIdInput();
		idInput.setText(ID > 0 ? Integer.toString(ID) : "");
		KanjiIdChecker idChecker = new KanjiIdChecker();
		ListPropertyChangeHandler<Integer, Kanji> idChangeListener = new ListPropertyChangeHandler<>(
				kanji,
				applicationWindow.getApplicationController().getKanjiList(),
				applicationWindow, idChecker, true, inputGoal);
		validationListeners.forEach(idChangeListener::addValidationListener);
		idInput.addFocusListener(idChangeListener);
		AbstractButton remove = commonListElements.getButtonDelete();
		JLabel rowNumberLabel = commonListElements.getRowNumberLabel();
		ComplexRow panelRows = SimpleRowBuilder
				.createRowStartingFromColumn(0, FillType.HORIZONTAL,
						rowNumberLabel, kanjiKeyword, keywordInput)
				.fillHorizontallySomeElements(keywordInput)
				.nextRow(kanjiId, idInput).setColumnToPutRowInto(1)
				.nextRow(remove);
		panel.addRowsOfElementsInColumn(panelRows);
		ListRowDataCreator<Kanji> rowDataCreator = new ListRowDataCreator<>(
				panel);

		if (inputGoal.equals(InputGoal.ADD) || inputGoal.equals(InputGoal.SEARCH)) {
			rowDataCreator.addPropertyData(ListPropertiesNames.KANJI_KEYWORD,
					panelRows.getAllRows().get(0),
					Pair.of(keywordInput, keywordChecker));
			rowDataCreator.addPropertyData(ListPropertiesNames.KANJI_ID,
					panelRows.getAllRows().get(1), Pair.of(idInput, idChecker));
		}

		return rowDataCreator.getListRowData();

	}

	@Override
	public void addValidationListener(
			InputValidationListener<Kanji> inputValidationListener) {
		validationListeners.add(inputValidationListener);
	}
}

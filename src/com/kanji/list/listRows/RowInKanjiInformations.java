package com.kanji.list.listRows;

import com.guimaker.enums.FillType;
import com.guimaker.enums.InputGoal;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.list.ListRowData;
import com.guimaker.list.myList.ListRowCreator;
import com.guimaker.list.myList.ListRowDataCreator;
import com.guimaker.model.CommonListElements;
import com.guimaker.panels.mainPanel.MainPanel;
import com.guimaker.row.ComplexRow;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.strings.Labels;
import com.kanji.constants.strings.ListPropertiesNames;
import com.kanji.kanjiListRow.KanjiActionsCreator;
import com.kanji.kanjiListRow.KanjiElementsCreator;
import com.kanji.list.listElements.Kanji;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.util.Optional;
import java.util.function.Supplier;

public class RowInKanjiInformations implements ListRowCreator<Kanji> {
	private KanjiElementsCreator elementsCreator;
	private KanjiActionsCreator actionsCreator;
	private PanelDisplayMode displayMode;
	private Optional<ProblematicWordsController<Kanji>> problematicWordsController;
	//TODO try to remove the dependency on problematic words controller

	public RowInKanjiInformations(ApplicationController applicationController,
			PanelDisplayMode displayMode) {
		this.displayMode = displayMode;
		elementsCreator = new KanjiElementsCreator();
		actionsCreator = new KanjiActionsCreator(applicationController.getApplicationWindow());
	}

	public void setProblematicWordsController(
			ProblematicWordsController<Kanji> problematicWordsController) {
		this.problematicWordsController = Optional.of(
				problematicWordsController);
	}

	@Override
	public ListRowData createListRow(Kanji kanji,
			CommonListElements<Kanji> commonListElements, InputGoal inputGoal) {
		elementsCreator.setLabelsColor(commonListElements.getLabelsColor());
		MainPanel panel = new MainPanel();
		JLabel keywordLabel = elementsCreator.createLabel(
				Labels.KANJI_KEYWORD_LABEL);
		JLabel idLabel = elementsCreator.createLabel(Labels.KANJI_ID_LABEL);
		boolean enabled = inputGoal.equals(InputGoal.EDIT) || inputGoal.equals(
				InputGoal.EDIT_TEMPORARILY)
				//TODO this should be handled automagically
				|| commonListElements.isForSingleRowOnly();
		JTextComponent keywordInput = actionsCreator.withKeywordValidation(
				elementsCreator.createKanjiKeywordInput(kanji.getKeyword(),
						enabled), kanji, inputGoal, commonListElements);
		JTextComponent idInput = actionsCreator.withKanjiIdValidation(
				elementsCreator.createKanjiIdInput(kanji.getId(), enabled),
				kanji, inputGoal, commonListElements);
		AbstractButton buttonDependingOnInputGoal = getButtonDependingOnInputGoal(
				kanji, commonListElements);
		ComplexRow panelRows = SimpleRowBuilder.createRowStartingFromColumn(0,
				FillType.HORIZONTAL, commonListElements.getRowNumberLabel(),
				keywordLabel, keywordInput)
											   .fillHorizontallySomeElements(
													   keywordInput)
											   .nextRow(idLabel, idInput)
											   .setColumnToPutRowInto(1)
											   .nextRow(
													   commonListElements.getButtonEdit())
											   .onlyAddIf(inputGoal.equals(
													   InputGoal.NO_INPUT))
											   .nextRow(
													   commonListElements.getFinishEditing())
											   .onlyAddIf(inputGoal.equals(
													   InputGoal.EDIT_TEMPORARILY))
											   .nextRow(
													   buttonDependingOnInputGoal);
		panel.addRowsOfElementsInColumn(panelRows);
		ListRowDataCreator<Kanji> rowDataCreator = new ListRowDataCreator<>(
				panel);

		rowDataCreator.addPropertyData(ListPropertiesNames.KANJI_KEYWORD,
				keywordInput, actionsCreator.getKeywordChecker());
		rowDataCreator.addPropertyData(ListPropertiesNames.KANJI_ID, idInput,
				actionsCreator.getIdChecker());

		return rowDataCreator.getListRowData();

	}

	private AbstractButton getButtonDependingOnInputGoal(Kanji kanji,
			CommonListElements commonListElements) {
		return displayMode.equals(PanelDisplayMode.EDIT) ?
				commonListElements.getButtonDelete() :
				actionsCreator.withActionShowKanjiStories(
						elementsCreator.createButtonShowKanjiStories(),
						problematicWordsController.orElseThrow(
								throwIllegalStateException()), kanji);
	}

	private Supplier<IllegalStateException> throwIllegalStateException() {
		return () -> new IllegalStateException(
				"Problematic words controller was not initialized for view mode");
	}

}

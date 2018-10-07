package com.kanji.panelsAndControllers.panels;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.ButtonType;
import com.guimaker.enums.FillType;
import com.guimaker.options.ButtonOptions;
import com.guimaker.options.ComponentOptions;
import com.guimaker.options.ScrollPaneOptions;
import com.guimaker.options.TextComponentOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.AbstractSimpleRow;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.strings.*;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.panelsAndControllers.controllers.LearningStartController;
import com.kanji.utilities.CommonGuiElementsCreator;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LearningStartPanel extends AbstractPanelWithHotkeysInfo {

	private JScrollPane rangesScrollPane;
	private JLabel sumOfWordsLabel;
	private AbstractButton problematicWordsCheckbox;
	private LearningStartController controller;
	private MainPanel rangesPanel;
	private JTextComponent firstTextFieldInRow;
	private AbstractButton problematicCheckbox;
	private JLabel problematicWordsLabel;

	public LearningStartPanel(ApplicationController applicationController,
			int numberOfWords) {
		controller = new LearningStartController(numberOfWords,
				applicationController, this);
	}

	@Override
	public void createElements() {

		JLabel title = GuiElementsCreator.createLabel(
				new ComponentOptions().text(Titles.LEARNING_START_DIALOG));
		problematicWordsCheckbox = createProblematicKanjiCheckbox();
		JLabel numberOfProblematicKanjis = GuiElementsCreator.createLabel(
				new ComponentOptions()
						.text(Prompts.PROBLEMATIC_KANJI + controller
								.getProblematicWordsNumber()));

		rangesPanel = new MainPanel(null, true);
		rangesScrollPane = GuiElementsCreator.createScrollPane(
				new ScrollPaneOptions().opaque(false)
						.componentToWrap(rangesPanel.getPanel()).border(null)
						.preferredSize(new Dimension(350, 200)));
		addRowToRangesPanel();
		AbstractButton buttonAddRow = GuiElementsCreator
				.createButtonlikeComponent(new ButtonOptions(ButtonType.BUTTON)
								.text(ButtonsNames.ADD_ROW),
						controller.createActionAddRow());
		sumOfWordsLabel = GuiElementsCreator.createLabel(new ComponentOptions()
				.text(createNumberOfSelectedWordsText(0)));
		AbstractButton buttonCancel = createButtonClose();
		AbstractButton buttonApprove = createButtonWithHotkey(KeyEvent.VK_ENTER,
				controller.createActionStartLearning(),
				ButtonsNames.START_LEARNING,
				HotkeysDescriptions.START_LEARNING);

		MainPanel panelIncludeProblematicWords = new MainPanel(null);
		panelIncludeProblematicWords.addRows(
				SimpleRowBuilder.createRow(FillType.NONE, Anchor.CENTER, title)
						.disableBorder()
						.nextRow(FillType.HORIZONTAL, problematicWordsCheckbox)
						.nextRow(numberOfProblematicKanjis));

		MainPanel panelChooseWordRanges = new MainPanel(null);
		panelChooseWordRanges.addRows(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.CENTER, GuiElementsCreator
						.createLabel(new ComponentOptions()
								.text(Prompts.LEARNING_START)))
				.nextRow(FillType.BOTH, rangesScrollPane)
				.useAllExtraVerticalSpace()
				.nextRow(FillType.HORIZONTAL, buttonAddRow, sumOfWordsLabel)
				.fillHorizontallySomeElements(sumOfWordsLabel));

		mainPanel.addRow(SimpleRowBuilder.createRow(FillType.BOTH,
				panelIncludeProblematicWords.getPanel()));
		mainPanel.addRow(SimpleRowBuilder
				.createRow(FillType.BOTH, panelChooseWordRanges.getPanel())
				.useAllExtraVerticalSpace());

		setNavigationButtons(buttonCancel, buttonApprove);
		addHotkeys();
	}

	private String createNumberOfSelectedWordsText(int numberOfWords) {
		return Prompts.RANGE_SUM + numberOfWords;
	}

	private AbstractButton createProblematicKanjiCheckbox() {
		problematicCheckbox = GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.CHECKBOX)
						.text(Labels.PROBLEMATIC_WORDS_OPTION).opaque(false),
				null);
		problematicCheckbox.addItemListener(controller
				.createListenerAddProblematicWords(problematicCheckbox));
		controller.enableOrDisableProblematicCheckbox();
		return problematicCheckbox;

	}

	private void addHotkeys() {
		addHotkey(KeyEvent.VK_P,
				controller.createActionSelectProblematicCheckbox(),
				mainPanel.getPanel(),
				HotkeysDescriptions.ADD_PROBLEMATIC_KANJIS);
	}

	public void addLabelWithProblematicWords() {
		problematicWordsLabel = GuiElementsCreator.createLabel(
				new ComponentOptions().text(Prompts.PROBLEMATIC_KANJIS_ADDED));
		rangesPanel.addRow(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.NORTH, problematicWordsLabel));
	}

	public JLabel getProblematicWordsLabel() {
		return problematicWordsLabel;
	}

	public MainPanel getRangesPanel() {
		return rangesPanel;
	}

	public void addRowToRangesPanel() {
		JTextComponent inputRangeFrom = createInputForWordRangeComponent();
		JTextComponent inputRangeTo = createInputForWordRangeComponent();

		KeyAdapter keyAdapter = controller
				.createListenerForKeyTyped(inputRangeFrom, inputRangeTo);
		inputRangeFrom.addKeyListener(keyAdapter);
		inputRangeTo.addKeyListener(keyAdapter);
		firstTextFieldInRow = inputRangeFrom;

		AbstractButton removeRow = GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON)
						.text(ButtonsNames.REMOVE_ROW), controller
						.createActionDeleteRow(problematicWordsCheckbox,
								inputRangeFrom, inputRangeTo));

		JLabel labelFrom = GuiElementsCreator.createLabel(
				new ComponentOptions().text(Labels.RANGE_FROM_LABEL));
		JLabel labelTo = GuiElementsCreator.createLabel(
				new ComponentOptions().text(Labels.RANGE_TO_LABEL));

		AbstractSimpleRow newRow = SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.NORTH, labelFrom,
						inputRangeFrom, labelTo, inputRangeTo, removeRow);

		controller.addRowToRangesPanel(inputRangeFrom, inputRangeTo, newRow);
		controller.updateAfterAddingRangesRow(rangesScrollPane, inputRangeFrom);

	}

	public AbstractButton getProblematicWordsCheckbox() {
		return problematicWordsCheckbox;
	}

	private JTextComponent createInputForWordRangeComponent() {
		return GuiElementsCreator.createTextField(new TextComponentOptions().
				maximumCharacters(5).rowsAndColumns(1, 5));
	}

	public void showErrorOnThePanel(String message, int rowNumber) {
		rangesPanel.insertRow(rowNumber, SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.NORTH,
						CommonGuiElementsCreator.createErrorLabel(message))
				.fillAllVertically());
	}

	public void updateSumOfWordsLabel(int sumOfWords) {
		sumOfWordsLabel.setText(createNumberOfSelectedWordsText(sumOfWords));
	}

	@Override
	public void afterVisible() {
		firstTextFieldInRow.requestFocusInWindow();
	}

}

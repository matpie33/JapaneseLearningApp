package com.kanji.panelsAndControllers.panels;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.ButtonType;
import com.guimaker.enums.FillType;
import com.guimaker.options.*;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.AbstractSimpleRow;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.strings.*;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.panelsAndControllers.controllers.LearningStartController;
import com.kanji.utilities.CommonGuiElementsCreator;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LearningStartPanel extends AbstractPanelWithHotkeysInfo {
	//TODO refactor needed - too much of controller in this panel

	private JScrollPane scrollPane;
	private JTextComponent sumRangeField;
	private AbstractButton problematicCheckbox;
	private LearningStartController controller;
	private MainPanel rangesPanel;
	private JTextComponent firstTextField;

	public LearningStartPanel(ApplicationController applicationController,
			int numberOfWords) {
		controller = new LearningStartController(numberOfWords,
				applicationController, this);
	}

	@Override
	public void createElements() {

		JTextComponent prompt = GuiElementsCreator.createTextArea(
				new TextAreaOptions().editable(false).opaque(false)
						.text(Prompts.LEARNING_START).border(null));
		problematicCheckbox = createProblematicKanjiCheckbox();
		rangesPanel = new MainPanel(null, true);
		scrollPane = createRangesPanelScrollPane();
		addRowToRangesPanel();

		JTextComponent problematicKanjis = createProblematicRangeField(
				Prompts.PROBLEMATIC_KANJI);
		AbstractButton newRow = createButtonAddRow(ButtonsNames.ADD_ROW);
		sumRangeField = GuiElementsCreator.createTextField(
				new TextComponentOptions().text(Prompts.RANGE_SUM)
						.editable(false));
		AbstractButton cancel = createButtonClose();
		AbstractButton approve = createButtonStartLearning(
				ButtonsNames.START_LEARNING);

		MainPanel problematicPanel = new MainPanel(null);
		problematicPanel.addRows(
				(SimpleRowBuilder.createRow(FillType.BOTH, prompt)
						.disableBorder()
						.nextRow(FillType.HORIZONTAL, problematicCheckbox)
						.nextRow(problematicKanjis)
						.nextRow(FillType.NONE, Anchor.CENTER,
								GuiElementsCreator.createLabel(
										new ComponentOptions()
												.text(Titles.KANJI_RANGES)))
						.nextRow(FillType.BOTH, scrollPane)
						.useAllExtraVerticalSpace()
						.nextRow(FillType.HORIZONTAL, newRow, sumRangeField)
						.fillVertically(sumRangeField)
						.fillHorizontallySomeElements(sumRangeField)));
		mainPanel.addRow(SimpleRowBuilder
				.createRow(FillType.BOTH, problematicPanel.getPanel())
				.useAllExtraVerticalSpace());
		setNavigationButtons(cancel, approve);
	}

	private JScrollPane createRangesPanelScrollPane() {
		Border b = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
		return GuiElementsCreator.createScrollPane(
				new ScrollPaneOptions().opaque(false)
						.componentToWrap(rangesPanel.getPanel()).border(null)
						.preferredSize(new Dimension(350, 200)));
	}

	private AbstractButton createProblematicKanjiCheckbox() {
		AbstractButton problematicCheckbox = GuiElementsCreator
				.createButtonlikeComponent(
						new ButtonOptions(ButtonType.CHECKBOX)
								.text(Labels.PROBLEMATIC_KANJI_OPTION)
								.opaque(false), null);
		if (controller.getProblematicWordsNumber() == 0) {
			problematicCheckbox.setEnabled(false);
		}
		AbstractAction action2 = controller
				.createActionSelectProblematicCheckbox(problematicCheckbox);
		problematicCheckbox.addItemListener(controller
				.createListenerAddProblematicKanjis(problematicCheckbox));
		addHotkey(KeyEvent.VK_P, action2, mainPanel.getPanel(),
				HotkeysDescriptions.ADD_PROBLEMATIC_KANJIS);

		return problematicCheckbox;

	}

	public int showLabelWithProblematicKanjis() {
		Component c = parentDialog.getContainer().getFocusOwner();
		JLabel label = GuiElementsCreator.createLabel(
				new ComponentOptions().text(Prompts.PROBLEMATIC_KANJIS_ADDED));
		int rowNumber = rangesPanel.getNumberOfRows();
		rangesPanel.addRow(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.NORTH, label));
		rangesPanel.updateView();
		c.requestFocusInWindow();
		return rowNumber;

	}

	public void hideLabelWithProblematicKanjis(int rowNumber) {
		rangesPanel.removeRow(rowNumber);
	}

	public void addRowToRangesPanel() {

		boolean problematicCheckboxSelected = problematicCheckbox.isSelected();
		int nextRowNumber = rangesPanel.getNumberOfRows();
		if (problematicCheckboxSelected) {
			nextRowNumber -= 1;
		}
		JTextComponent fieldFrom = createRangeTextComponent();
		JTextComponent fieldTo = createRangeTextComponent();
		controller.addRow(nextRowNumber, fieldFrom, fieldTo);
		KeyAdapter keyAdapter = controller
				.createListenerForKeyTyped(problematicCheckbox, fieldFrom,
						fieldTo);

		fieldFrom.addKeyListener(keyAdapter);
		fieldTo.addKeyListener(keyAdapter);

		firstTextField = fieldFrom;

		AbstractButton delete = createDeleteButton(fieldFrom, fieldTo);
		if (controller.getNumberOfRangesRows() == 1) {
			delete.setVisible(false);
		}

		JLabel from = GuiElementsCreator.createLabel(
				new ComponentOptions().text(Labels.RANGE_FROM_LABEL));
		JLabel labelTo = GuiElementsCreator.createLabel(
				new ComponentOptions().text(Labels.RANGE_TO_LABEL));

		AbstractSimpleRow newRow = SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.NORTH, from, fieldFrom,
						labelTo, fieldTo, delete);

		if (problematicCheckboxSelected) {
			controller.increaseProblematicLabelRowNumber();
			rangesPanel.insertRow(nextRowNumber, newRow);
		}
		else {
			rangesPanel.addRow(newRow);
		}
		rangesPanel.updateView();

		if (controller.getNumberOfRangesRows() == 2) {
			changeVisibilityOfDeleteButtonInFirstRow(true);
		}

		scrollToBottom();
		fieldFrom.requestFocusInWindow();

	}

	private JTextComponent createRangeTextComponent() {
		return GuiElementsCreator.createTextField(new TextComponentOptions().
				maximumCharacters(5).rowsAndColumns(1, 5));
	}

	public void showErrorOnThePanel(String message, int rowNumber) {
		rangesPanel.insertRow(rowNumber, SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.NORTH,
						CommonGuiElementsCreator.createErrorLabel(message))
				.fillAllVertically());
		SwingUtilities.invokeLater(new Runnable() {
			// TODO swing utilities
			@Override
			public void run() {
				rangesPanel.getPanel().scrollRectToVisible(
						rangesPanel.getRows().get(rowNumber).getBounds());
			}
		});
	}

	private void scrollToBottom() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// TODO swing utilities
				scrollPane.getVerticalScrollBar().setValue(
						scrollPane.getVerticalScrollBar().getMaximum());
			}
		});

	}

	public void updateSumOfWordsLabel(int sumOfWords) {
		sumRangeField.setText(Prompts.RANGE_SUM + sumOfWords);
	}

	private AbstractButton createDeleteButton(JTextComponent from, JTextComponent to) {
		AbstractButton delete = GuiElementsCreator.createButtonlikeComponent(new ButtonOptions(ButtonType.BUTTON).text(
				ButtonsNames.REMOVE_ROW),controller
				.createActionDeleteRow(problematicCheckbox, from, to));
		return delete;
	}

	public void removeRow(int rowNumber) {
		rangesPanel.removeRow(rowNumber);
		getDialog().getContainer().getMostRecentFocusOwner()
				.requestFocusInWindow();
	}

	private AbstractButton createButtonAddRow(String text) {
		AbstractButton button = GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON).text(text),
				controller.createActionAddRow());
		return button;
	}

	private JTextComponent createProblematicRangeField(String text) {
		JTextComponent sumRange = GuiElementsCreator.createTextField(
				new TextComponentOptions()
						.text(text + controller.getProblematicWordsNumber())
						.editable(false));
		new JTextField(text);
		return sumRange;
	}

	private AbstractButton createButtonStartLearning(String text) {
		return createButtonWithHotkey(KeyEvent.VK_ENTER,
				controller.createActionStartLearning(problematicCheckbox), text,
				HotkeysDescriptions.START_LEARNING);
	}

	public void switchToRepeatingPanel() {
		parentDialog.getContainer().dispose();
		controller.switchPanelAndSetWordsRangesToRepeat(
				problematicCheckbox.isSelected());
	}

	public void showErrorDialog(String message) {
		parentDialog.showMessageDialog(message);
	}

	public void changeVisibilityOfDeleteButtonInFirstRow(boolean visibility) {
		rangesPanel.changeVisibilityOfLastElementInRow(0, visibility);
	}

	@Override
	public void afterVisible() {
		firstTextField.requestFocusInWindow();
	}

}

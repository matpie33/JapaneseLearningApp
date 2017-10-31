package com.kanji.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.options.ScrollPaneOptions;
import com.guimaker.options.TextAreaOptions;
import com.guimaker.options.TextComponentOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRow;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.Row.RepeatingInformation;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.HotkeysDescriptions;
import com.kanji.constants.Labels;
import com.kanji.constants.NumberValues;
import com.kanji.constants.Prompts;
import com.kanji.constants.Titles;
import com.kanji.controllers.ApplicationController;
import com.kanji.controllers.LearningStartController;
import com.kanji.myList.MyList;
import com.kanji.utilities.CommonGuiElementsMaker;
import com.kanji.utilities.LimitDocumentFilter;

public class LearningStartPanel extends AbstractPanelWithHotkeysInfo {

	private JScrollPane scrollPane;
	private JTextComponent sumRangeField;
	private JCheckBox problematicCheckbox;
	private LearningStartController controller;
	private MainPanel rangesPanel;
	private JTextComponent firstTextField;

	public LearningStartPanel(ApplicationController applicationController, int numberOfWords,
			MyList<RepeatingInformation> list) {
		controller = new LearningStartController(list, numberOfWords, applicationController, this);
	}

	@Override
	void createElements() {

		JTextComponent prompt = GuiMaker.createTextArea(new TextAreaOptions().editable(false)
				.opaque(false).text(Prompts.LEARNING_START).border(null));
		problematicCheckbox = createProblematicKanjiCheckbox();
		rangesPanel = new MainPanel(BasicColors.VERY_LIGHT_BLUE, true);
		scrollPane = createRangesPanelScrollPane();
		addRowToRangesPanel();

		JTextComponent problematicKanjis = createProblematicRangeField(Prompts.PROBLEMATIC_KANJI);
		JButton newRow = createButtonAddRow(ButtonsNames.ADD_ROW);
		sumRangeField = GuiMaker.createTextField(new TextComponentOptions().text(Prompts.RANGE_SUM).editable(false));
		AbstractButton cancel = createButtonClose();
		AbstractButton approve = createButtonStartLearning(ButtonsNames.START_LEARNING);

		MainPanel problematicPanel = new MainPanel(null);
		problematicPanel.addRows((SimpleRowBuilder.createRow(FillType.BOTH, prompt).disableBorder()
				.nextRow(FillType.HORIZONTAL, problematicCheckbox).nextRow(problematicKanjis)
				.nextRow(FillType.NONE, Anchor.CENTER, new JLabel(Titles.KANJI_RANGES))
				.nextRow(FillType.BOTH, scrollPane).useAllExtraVerticalSpace()
				.nextRow(FillType.HORIZONTAL, newRow, sumRangeField).fillVertically(sumRangeField)
				.fillHorizontallySomeElements(sumRangeField)));
		mainPanel.addRows( SimpleRowBuilder.createRow(FillType.BOTH, problematicPanel.getPanel())
				.useAllExtraVerticalSpace());
		setNavigationButtons(cancel, approve);
	}

	private JScrollPane createRangesPanelScrollPane() {
		Border b = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
		return GuiMaker.createScrollPane(new ScrollPaneOptions()
				.componentToWrap(rangesPanel.getPanel()).backgroundColor(BasicColors.DARK_BLUE)
				.border(b).preferredSize(new Dimension(350, 200)));
	}

	private JCheckBox createProblematicKanjiCheckbox() {
		final JCheckBox problematicCheckbox = new JCheckBox(Labels.PROBLEMATIC_KANJI_OPTION);
		if (controller.getProblematicKanjiNumber() == 0) {
			problematicCheckbox.setEnabled(false);
		}
		AbstractAction action2 = controller.createActionSelectProblematicCheckbox(problematicCheckbox);
		problematicCheckbox.addItemListener(controller.createListenerAddProblematicKanjis(
				problematicCheckbox));
		addHotkey(KeyEvent.VK_P, action2, mainPanel.getPanel(),
				HotkeysDescriptions.ADD_PROBLEMATIC_KANJIS);

		return problematicCheckbox;

	}

	public int showLabelWithProblematicKanjis() {
		Component c = parentDialog.getContainer().getFocusOwner();
		JLabel label = new JLabel(Prompts.PROBLEMATIC_KANJIS_ADDED);
		label.setForeground(BasicColors.NAVY_BLUE);
		int rowNumber = rangesPanel.getNumberOfRows();
		rangesPanel.addRows(SimpleRowBuilder.createRow(FillType.NONE, Anchor.NORTH, label));
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
		KeyAdapter keyAdapter = controller.createListenerForKeyTyped(problematicCheckbox, fieldFrom, fieldTo);

		fieldFrom.addKeyListener(keyAdapter);
		fieldTo.addKeyListener(keyAdapter);

		firstTextField = fieldFrom;

		JButton delete = createDeleteButton(fieldFrom, fieldTo);
		if (controller.getNumberOfRangesRows() == 1) {
			delete.setVisible(false);
		}

		JLabel from = new JLabel(Labels.RANGE_FROM_LABEL);
		JLabel labelTo = new JLabel(Labels.RANGE_TO_LABEL);

		SimpleRow newRow = SimpleRowBuilder.createRow(FillType.NONE, Anchor.NORTH, from, fieldFrom, labelTo,
				fieldTo, delete);

		if (problematicCheckboxSelected) {
			controller.increaseProblematicLabelRowNumber();
			rangesPanel.insertRow(nextRowNumber, newRow);
		}
		else {
			rangesPanel.addRows(newRow);
		}
		rangesPanel.updateView();

		if (controller.getNumberOfRangesRows() == 2) {
			changeVisibilityOfDeleteButtonInFirstRow(true);
		}

		scrollToBottom();
		fieldFrom.requestFocusInWindow();

	}

	private JTextComponent createRangeTextComponent (){
		return GuiMaker.createTextField(new TextComponentOptions().
				maximumCharacters(5).rowsAndColumns(1,5));
	}

	public void showErrorOnThePanel(String message, int rowNumber) {
		rangesPanel
				.insertRow(rowNumber,
						SimpleRowBuilder.createRow(FillType.NONE, Anchor.NORTH,
								CommonGuiElementsMaker.createErrorLabel(message))
										.fillAllVertically());
		SwingUtilities.invokeLater(new Runnable() {
			// TODO swing utilities
			@Override
			public void run() {
				rangesPanel.getPanel()
						.scrollRectToVisible(rangesPanel.getRows().get(rowNumber).getBounds());
			}
		});
	}

	private void scrollToBottom() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// TODO swing utilities
				scrollPane.getVerticalScrollBar()
						.setValue(scrollPane.getVerticalScrollBar().getMaximum());
			}
		});

	}

	public void removeRowFromPanel(int rowNumber) {
		rangesPanel.removeRow(rowNumber);
	}

	public void updateSumOfWordsLabel(int sumOfWords) {
		sumRangeField.setText(Prompts.RANGE_SUM + sumOfWords);
	}

	private JButton createDeleteButton(JTextComponent from, JTextComponent to) {
		JButton delete = new JButton(ButtonsNames.REMOVE_ROW);
		delete.addActionListener(controller.createActionDeleteRow(problematicCheckbox, from, to));
		return delete;
	}

	public void removeRow(int rowNumber) {
		rangesPanel.removeRow(rowNumber);
	}

	private JButton createButtonAddRow(String text) {
		JButton button = new JButton(text);
		button.addActionListener(controller.createActionAddRow());
		return button;
	}

	private JTextComponent createProblematicRangeField(String text) {
		JTextComponent sumRange = new JTextField(text);
		sumRange.setEditable(false);
		sumRange.setText(sumRange.getText() + controller.getProblematicKanjiNumber());
		return sumRange;
	}

	private AbstractButton createButtonStartLearning(String text) {
		return createButtonWithHotkey(KeyEvent.VK_ENTER,
				controller.createActionStartLearning(problematicCheckbox), text,
				HotkeysDescriptions.START_LEARNING);
	}

	public void switchToRepeatingPanel() {
		parentDialog.getContainer().dispose();
		controller.switchPanelAndSetWordsRangesToRepeat(problematicCheckbox.isSelected());
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

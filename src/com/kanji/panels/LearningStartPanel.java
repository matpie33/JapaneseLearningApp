package com.kanji.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.AbstractDocument;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
import com.kanji.Row.RepeatingList;
import com.kanji.actions.CommonActionsMaker;
import com.kanji.actions.GuiElementsMaker;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.ExceptionsMessages;
import com.kanji.constants.NumberValues;
import com.kanji.constants.Options;
import com.kanji.constants.Prompts;
import com.kanji.controllers.LearningStartController;
import com.kanji.myList.MyList;
import com.kanji.utilities.LimitDocumentFilter;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;

public class LearningStartPanel implements PanelCreator {

	private MainPanel main;
	private JScrollPane scrollPane;
	private JTextField sumRangeField;
	private JCheckBox problematicCheckbox;
	private DialogWindow parentDialog;
	private String error = "";
	private LearningStartController controller;
	private MainPanel rangesPanel;

	public LearningStartPanel(ApplicationWindow parentOfParent, int numberOfWords,
			MyList<RepeatingList> list) {
		controller = new LearningStartController(list, numberOfWords, parentOfParent, this);
		main = new MainPanel(BasicColors.OCEAN_BLUE, false);
	}

	// TODO when typing range that contains problematic kanjis, adjust the total
	// kanji to repeat properly i.e. do not show more than it actually is

	@Override
	public void setParentDialog(DialogWindow parent) {
		parentDialog = parent;
	}

	@Override
	public JPanel createPanel() { // TODO add focus to textfield from

		JTextArea prompt = createPrompt();
		problematicCheckbox = createProblematicKanjiCheckbox();
		rangesPanel = new MainPanel(BasicColors.VERY_LIGHT_BLUE, true);
		scrollPane = createRangesPanelScrollPane();
		addRowToRangesPanel();

		JTextField problematicKanjis = createProblematicRangeField(Prompts.problematicKanjiPrompt);
		JButton newRow = createButtonAddRow(ButtonsNames.buttonAddRowText, rangesPanel);
		sumRangeField = GuiMaker.createTextField(1, Prompts.sumRangePrompt);
		JButton cancel = GuiElementsMaker.createButton(ButtonsNames.buttonCancelText,
				CommonActionsMaker.createDisposeAction(parentDialog));
		JButton approve = createButtonStartLearning(ButtonsNames.buttonApproveText,
				rangesPanel.getPanel());

		main.addRow(RowMaker.createHorizontallyFilledRow(prompt));
		main.addRow(RowMaker.createHorizontallyFilledRow(problematicCheckbox));
		main.addRow(RowMaker.createHorizontallyFilledRow(problematicKanjis));
		main.addRow(RowMaker.createBothSidesFilledRow(scrollPane));
		main.addRow(RowMaker.createHorizontallyFilledRow(newRow, sumRangeField)
				.fillHorizontallySomeElements(sumRangeField));
		main.addRow(RowMaker.createUnfilledRow(GridBagConstraints.EAST, cancel, approve));
		return main.getPanel();
	}

	private JScrollPane createRangesPanelScrollPane() {
		Border b = BorderFactory.createLineBorder(BasicColors.VERY_BLUE);
		return GuiMaker.createScrollPane(BasicColors.DARK_BLUE, b, rangesPanel.getPanel(),
				new Dimension(300, 200));
	}

	private JTextArea createPrompt() {
		JTextArea prompt = GuiMaker.createTextArea(false);
		prompt.setOpaque(false);
		prompt.setText(Prompts.learnStartPrompt);
		return prompt;
	}

	private JCheckBox createProblematicKanjiCheckbox() {
		final JCheckBox problematicCheckbox = new JCheckBox(Options.problematicKanjiOption);
		problematicCheckbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.updateProblematicKanjiNumber(problematicCheckbox.isSelected());
			}
		});
		if (controller.getProblematicKanjiNumber() == 0) {
			problematicCheckbox.setEnabled(false);
		}

		AbstractAction action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (problematicCheckbox.isEnabled()) {
					problematicCheckbox.setSelected(!problematicCheckbox.isSelected());
					controller.updateProblematicKanjiNumber(problematicCheckbox.isSelected());
				}

			}
		};

		CommonActionsMaker.addHotkey(KeyEvent.VK_P, action, main.getPanel());

		return problematicCheckbox;

	}

	private void addRowToRangesPanel() {

		JLabel from = new JLabel("od");
		JTextField[] textFields = createTextFieldsForRangeInput(rangesPanel.getNumberOfRows());
		JTextField fieldFrom = textFields[0];
		JLabel labelTo = new JLabel("do");
		JTextField fieldTo = textFields[1];

		JPanel container = rangesPanel
				.addRow(RowMaker.createHorizontallyFilledRow(from, fieldFrom, labelTo, fieldTo));

		if (rangesPanel.getNumberOfRows() > 1) {
			System.out.println("hererer");
			JButton delete = createDeleteButton(rangesPanel, container);
			rangesPanel.addElementsToRow(container, delete);
		}
		if (rangesPanel.getNumberOfRows() == 2) {
			System.out.println("here");
			JPanel firstRow = (JPanel) rangesPanel.getRows().get(0);
			JButton delete = createDeleteButton(rangesPanel, firstRow);
			rangesPanel.addElementsToRow(firstRow, delete);
		}

		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
	}

	private JTextField[] createTextFieldsForRangeInput(final int rowNumber) {
		JTextField[] textFields = new JTextField[2];
		for (int i = 0; i < 2; i++) {
			textFields[i] = new JTextField(5);
			((AbstractDocument) textFields[i].getDocument()).setDocumentFilter(
					new LimitDocumentFilter(NumberValues.INTEGER_MAX_VALUE_DIGITS_AMOUNT));
		}
		final JTextField from = textFields[0];
		final JTextField to = textFields[1];

		KeyAdapter keyAdapter = new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				controller.handleKeyTyped(e, rangesPanel.getPanel(),
						problematicCheckbox.isSelected(), rowNumber);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				controller.handleKeyReleased(e, to, from, rangesPanel,
						problematicCheckbox.isSelected(), rowNumber);
			}

		};

		from.addKeyListener(keyAdapter);
		to.addKeyListener(keyAdapter);

		textFields[0] = from;
		textFields[1] = to;
		return textFields;
	}

	public void showErrorIfNotExists(String message, int rowNumber) {
		if (error.equals(message))
			return;
		else
			removeErrorIfExists(rowNumber);

		rangesPanel.addElementsToRow(rowNumber, new JLabel(message));
		error = message;
	}

	public void removeErrorIfExists(int rowNumber) {
		if (error.isEmpty())
			return;
		error = "";
		JPanel row = rangesPanel.getRows().get(rowNumber);
		for (Component c : row.getComponents()) {
			if (c instanceof JLabel && ((JLabel) c).getText()
					.matches(ExceptionsMessages.rangeToValueLessThanRangeFromValue + "|"
							+ ExceptionsMessages.valueIsNotNumber + "|"
							+ ExceptionsMessages.rangeValueTooHigh)) {
				System.out.println("remove ing");
				rangesPanel.removeLastElementFromRow(rowNumber);
			}
		}
	}

	public void updateSumOfWords(int sumOfWords) {
		sumRangeField.setText(Prompts.sumRangePrompt + sumOfWords);
	}

	private JButton createDeleteButton(final MainPanel container, final JPanel panelToRemove) {
		final JButton delete = new JButton(ButtonsNames.buttonRemoveRowText);
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				container.removeRow(panelToRemove);
				if (container.getNumberOfRows() == 1) {
					container.removeLastElementFromRow(0);
				}
				controller.recalculateSumOfKanji(rangesPanel.getPanel(),
						problematicCheckbox.isSelected());
				updateSumOfWords(controller.getSumOfWords());
				// deleteRow(container, panelToRemove);
			}
		});
		return delete;
	}

	private JButton createButtonAddRow(String text, final MainPanel panel) {
		JButton button = new JButton(text);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addRowToRangesPanel();

			}
		});
		return button;
	}

	private JTextField createProblematicRangeField(String text) {
		JTextField sumRange = new JTextField(text);
		sumRange.setEditable(false);
		sumRange.setText(sumRange.getText() + controller.getProblematicKanjiNumber());
		return sumRange;
	}

	private JButton createButtonStartLearning(String text, final JPanel panel) {
		JButton button = new JButton(text);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					controller.validateAndStart(panel, problematicCheckbox.isSelected());
				}
				catch (Exception e1) {
					parentDialog.showMsgDialog(e1.getMessage());
				}
			}
		});
		return button;
	}

	public void switchToRepeatingPanel() {
		System.out.println("swiiiiiiiiiiiiitch");
		parentDialog.getContainer().dispose();
		controller.switchPanels(problematicCheckbox.isSelected());
	}

	public void showErrorDialog(String message) {
		parentDialog.showMsgDialog(message);
	}

}

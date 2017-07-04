package com.kanji.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
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
	private List<String> error;
	private LearningStartController controller;
	private MainPanel rangesPanel;
	private List<Integer> rows;

	public LearningStartPanel(ApplicationWindow parentOfParent, int numberOfWords,
			MyList<RepeatingList> list) {
		controller = new LearningStartController(list, numberOfWords, parentOfParent, this);
		main = new MainPanel(BasicColors.OCEAN_BLUE, false);
		rows = new ArrayList<>();
		error = new ArrayList<>();
	}

	// TODO when typing range that contains problematic kanjis, adjust the total
	// kanji to repeat properly i.e. do not show more than it actually is

	@Override
	public void setParentDialog(DialogWindow parent) {
		parentDialog = parent;
	}

	@Override
	public JPanel createPanel() {

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
		if (controller.getProblematicKanjiNumber() == 0) {
			problematicCheckbox.setEnabled(false);
		}
		ItemListener action = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				controller.updateProblematicKanjiNumber(problematicCheckbox.isSelected());
			}
		};

		AbstractAction action2 = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (problematicCheckbox.isEnabled()) {
					problematicCheckbox.setSelected(!problematicCheckbox.isSelected());
				}
			}
		};

		problematicCheckbox.addItemListener(action);
		CommonActionsMaker.addHotkey(KeyEvent.VK_P, action2, main.getPanel());

		return problematicCheckbox;

	}

	private void addRowToRangesPanel() {

		controller.addRangesRow();
		error.add("");
		JLabel from = new JLabel("od");
		JTextField[] textFields = createTextFieldsForRangeInput(rows.size());
		JTextField fieldFrom = textFields[0];
		SwingUtilities.invokeLater(new Runnable() { // TODO not nice to put
													// swing utilities here
			@Override
			public void run() {
				fieldFrom.requestFocusInWindow();
			}
		});

		JLabel labelTo = new JLabel("do");
		JTextField fieldTo = textFields[1];

		JPanel container = rangesPanel
				.addRow(RowMaker.createHorizontallyFilledRow(from, fieldFrom, labelTo, fieldTo));

		if (rangesPanel.getNumberOfRows() > 1) {
			System.out.println("hererer");
			JButton delete = createDeleteButton(rangesPanel.getNumberOfRows() - 1);
			rangesPanel.addElementsToRow(container, delete);
			// TODO needs refactoring - duplicated code
		}

		if (rangesPanel.getNumberOfRows() == 2) {
			System.out.println("here");
			JPanel firstRow = rangesPanel.getRows().get(0);
			JButton delete = createDeleteButton(0);
			rangesPanel.addElementsToRow(firstRow, delete);
		}

		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
	}

	private JTextField[] createTextFieldsForRangeInput(int rowNumber) {
		JTextField[] textFields = new JTextField[2];
		for (int i = 0; i < 2; i++) {
			textFields[i] = new JTextField(5);
			((AbstractDocument) textFields[i].getDocument()).setDocumentFilter(
					new LimitDocumentFilter(NumberValues.INTEGER_MAX_VALUE_DIGITS_AMOUNT));
		}
		final JTextField from = textFields[0];
		final JTextField to = textFields[1];
		rows.add(rowNumber);
		KeyAdapter keyAdapter = new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				controller.handleKeyTyped(e, problematicCheckbox.isSelected(),
						rows.get(rows.size() - 1));
			}

			@Override
			public void keyReleased(KeyEvent e) {
				controller.handleKeyReleased(e, to, from, problematicCheckbox.isSelected(),
						rows.get(rows.size() - 1));
			}

		};

		from.addKeyListener(keyAdapter);
		to.addKeyListener(keyAdapter);

		textFields[0] = from;
		textFields[1] = to;
		return textFields;
	}

	public void showErrorIfNotExists(String message, int rowNumber) {
		if (error.get(rowNumber).equals(message))
			return;
		// TODO when pressing start, show more detailed info: add row number to
		// the error information
		removeErrorIfExists(rowNumber);
		rangesPanel.insertRow(rowNumber + 1, RowMaker.createUnfilledRow(GridBagConstraints.CENTER,
				GuiElementsMaker.createErrorLabel(message)).fillAllVertically());
		error.set(rowNumber, message);
	}

	public void removeErrorIfExists(int rowNumber) {
		if (error.get(rowNumber).isEmpty()) {
			return;
		}

		error.set(rowNumber, "");
		rangesPanel.removeRow(rowNumber + 1);
	}

	public void updateSumOfWords(int sumOfWords) {
		sumRangeField.setText(Prompts.sumRangePrompt + sumOfWords);
	}

	private JButton createDeleteButton(final int rowNumber) {
		final JButton delete = new JButton(ButtonsNames.buttonRemoveRowText);
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rangesPanel.removeRow(rowNumber);
				error.remove(rows.get(rowNumber));
				removeRowIndex(rowNumber);
				updateRowsIndexes(rowNumber);

				// container.removeRow(panelToRemove);
				if (rangesPanel.getNumberOfRows() == 1) {
					rangesPanel.removeLastElementFromRow(0);
				}
				controller.removeRange(rowNumber, problematicCheckbox.isSelected());
			}
		});
		return delete;
	}

	private void removeRowIndex(int rowNumber) {
		rows.remove(rowNumber);
	}

	private void updateRowsIndexes(int rowNumber) {
		for (int i = rowNumber; i < rows.size(); i++) {
			rows.set(i, rows.get(i) - 1);
		}
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
		AbstractAction a = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (gotErrors()) {
					parentDialog.showMsgDialog(
							controller.concatenateErrors(error.toArray(new String[] {})));
				}
				else {
					controller.validateAndStart(problematicCheckbox.isSelected());
				}

			}
		};
		button.addActionListener(a);
		parentDialog.addHotkeyToWindow(KeyEvent.VK_ENTER, a);
		return button;
	}

	private boolean gotErrors() {
		for (int i = 0; i < error.size(); i++) {
			if (!error.get(i).isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public void switchToRepeatingPanel() {
		parentDialog.getContainer().dispose();
		controller.switchPanels(problematicCheckbox.isSelected());
	}

	public void showErrorDialog(String message) {
		parentDialog.showMsgDialog(message);
	}

}

package com.kanji.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

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
import com.guimaker.row.SimpleRow;
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

	private MainPanel mainPanel;
	private JScrollPane scrollPane;
	private JTextField sumRangeField;
	private JCheckBox problematicCheckbox;
	private DialogWindow parentDialog;
	private LearningStartController controller;
	private MainPanel rangesPanel;

	public LearningStartPanel(ApplicationWindow parentOfParent, int numberOfWords,
			MyList<RepeatingList> list) {
		controller = new LearningStartController(list, numberOfWords, parentOfParent, this);
		mainPanel = new MainPanel(BasicColors.OCEAN_BLUE, false);
		new ArrayList<>();
	}

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

		mainPanel.addRow(RowMaker.createHorizontallyFilledRow(prompt));
		mainPanel.addRow(RowMaker.createHorizontallyFilledRow(problematicCheckbox));
		mainPanel.addRow(RowMaker.createHorizontallyFilledRow(problematicKanjis));
		mainPanel.addRow(RowMaker.createBothSidesFilledRow(scrollPane));
		mainPanel.addRow(RowMaker.createHorizontallyFilledRow(newRow, sumRangeField)
				.fillHorizontallySomeElements(sumRangeField));
		mainPanel.addRow(RowMaker.createUnfilledRow(GridBagConstraints.EAST, cancel, approve));
		return mainPanel.getPanel();
	}

	private JScrollPane createRangesPanelScrollPane() {
		Border b = BorderFactory.createLineBorder(BasicColors.VERY_BLUE);
		return GuiMaker.createScrollPane(BasicColors.DARK_BLUE, b, rangesPanel.getPanel(),
				new Dimension(350, 200));
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
				boolean problematicCheckboxSelected = problematicCheckbox.isSelected();
				controller.updateProblematicKanjiNumber(problematicCheckboxSelected);

			}
		};

		@SuppressWarnings("serial")
		AbstractAction action2 = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (problematicCheckbox.isEnabled()) {
					problematicCheckbox.setSelected(!problematicCheckbox.isSelected());
				}
			}
		};

		problematicCheckbox.addItemListener(action);
		CommonActionsMaker.addHotkey(KeyEvent.VK_P, action2, mainPanel.getPanel());

		return problematicCheckbox;

	}

	public int showLabelWithProblematicKanjis() {
		Component c = parentDialog.getContainer().getFocusOwner();
		JLabel label = new JLabel("+ problematyczne");
		label.setForeground(BasicColors.NAVY_BLUE);
		int rowNumber = rangesPanel.getNumberOfRows();
		rangesPanel.addRow(RowMaker.createUnfilledRow(GridBagConstraints.CENTER, label)); // TODO
																							// move
																							// to
																							// strings
		c.requestFocusInWindow();
		return rowNumber;

	}

	public void hideLabelWithProblematicKanjis(int rowNumber) {
		rangesPanel.removeRow(rowNumber);
	}

	private void addRowToRangesPanel() {

		JLabel from = new JLabel("od"); // TODO separate it into constants;
		boolean problematicCheckboxSelected = problematicCheckbox.isSelected();
		int nextRowNumber = rangesPanel.getNumberOfRows();
		if (problematicCheckboxSelected) {
			nextRowNumber -= 1;
		}
		JTextField[] textFields = createTextFieldsForRangeInput(nextRowNumber);
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
		JButton delete = createDeleteButton(fieldFrom, fieldTo);
		if (controller.getNumberOfRangesRows() == 1) {
			delete.setVisible(false);
		}
		SimpleRow newRow = RowMaker.createHorizontallyFilledRow(from, fieldFrom, labelTo, fieldTo,
				delete);
		if (problematicCheckboxSelected) {
			controller.increaseProblematicLabelRowNumber();
			rangesPanel.insertRow(nextRowNumber, newRow);
		}
		else {
			rangesPanel.addRow(newRow);
		}

		if (controller.getNumberOfRangesRows() == 2) {
			changeVisibilityOfDeleteButtonInFirstRow(true);
		}

		scrollToBottom();

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
		controller.addRow(rowNumber, from, to);
		KeyAdapter keyAdapter = new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				controller.handleKeyTyped(e, to, from, problematicCheckbox.isSelected());
			}

			@Override
			public void keyReleased(KeyEvent e) {
				controller.handleKeyReleased(e, to, from, problematicCheckbox.isSelected());
			}

		};

		from.addKeyListener(keyAdapter);
		to.addKeyListener(keyAdapter);

		textFields[0] = from;
		textFields[1] = to;
		return textFields;
	}

	public boolean showErrorOnThePanel(String message, int rowNumber) {
		// TODO when pressing start, show more detailed info: add row number to
		// the error information
		rangesPanel.insertRow(rowNumber, RowMaker.createUnfilledRow(GridBagConstraints.CENTER,
				GuiElementsMaker.createErrorLabel(message)).fillAllVertically());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				rangesPanel.getPanel()
						.scrollRectToVisible(rangesPanel.getRows().get(rowNumber).getBounds());
			}
		});
		return true;
	}

	private void scrollToBottom() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				scrollPane.getVerticalScrollBar()
						.setValue(scrollPane.getVerticalScrollBar().getMaximum());
			}
		});

	}

	public boolean removeRowFromPanel(int rowNumber) {
		rangesPanel.removeRow(rowNumber);
		return true;
	}

	public void updateSumOfWords(int sumOfWords) {
		sumRangeField.setText(Prompts.sumRangePrompt + sumOfWords);
	}

	private JButton createDeleteButton(JTextField from, JTextField to) {
		JButton delete = new JButton(ButtonsNames.buttonRemoveRowText);
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.removeRangeRow(from, to, problematicCheckbox.isSelected());
			}
		});
		return delete;
	}

	public void removeRow(int rowNumber) {
		rangesPanel.removeRow(rowNumber);
	}

	public void removeDeleteButtonFromFirstRow() {
		rangesPanel.removeLastElementFromRow(0);
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
		@SuppressWarnings("serial")
		AbstractAction a = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.showErrorsOrStart(problematicCheckbox.isSelected());

			}
		};
		button.addActionListener(a);
		parentDialog.addHotkeyToWindow(KeyEvent.VK_ENTER, a);
		return button;
	}

	public void switchToRepeatingPanel() {
		parentDialog.getContainer().dispose();
		controller.switchPanels(problematicCheckbox.isSelected());
	}

	public void showErrorDialog(String message) {
		parentDialog.showMsgDialog(message);
	}

	public void changeVisibilityOfDeleteButtonInFirstRow(boolean visibility) {
		rangesPanel.changeVisibilityOfLastElementInRow(0, visibility);
	}

}

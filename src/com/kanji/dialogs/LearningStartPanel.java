package com.kanji.dialogs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Calendar;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.text.AbstractDocument;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
import com.kanji.Row.RepeatingInformation;
import com.kanji.Row.RepeatingList;
import com.kanji.actions.CommonActionsMaker;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.ExceptionsMessages;
import com.kanji.constants.NumberValues;
import com.kanji.constants.Options;
import com.kanji.constants.Prompts;
import com.kanji.myList.MyList;
import com.kanji.range.Range;
import com.kanji.range.SetOfRanges;
import com.kanji.window.ApplicationWindow;
import com.kanji.window.LimitDocumentFilter;

public class LearningStartPanel implements PanelCreator {

	private MainPanel main;
	private JScrollPane scrollPane;
	private JTextField sumRangeField;
	private JCheckBox problematicCheckbox;
	private int rowsNumber;
	private DialogWindow parentDialog;
	private MyList<RepeatingList> repeatsList;
	private ApplicationWindow parentFrame;
	private SetOfRanges rangesToRepeat;
	private int numberOfWords;
	private int sumOfWords;
	private MainPanel rangesPanel;

	public LearningStartPanel(ApplicationWindow parentOfParent, int numberOfWords, MyList list) {
		repeatsList = list;
		this.numberOfWords = numberOfWords;
		this.parentFrame = parentOfParent;
		main = new MainPanel(BasicColors.OCEAN_BLUE, false);
	}

	@Override
	public void setParentDialog(DialogWindow parent) {
		parentDialog = parent;
	}

	@Override
	public JPanel createPanel() { // TODO add focus to textfield from

		int level = 0;
		JTextArea prompt = GuiMaker.createTextArea(false);
		prompt.setOpaque(false);
		prompt.setText(Prompts.learnStartPrompt);

		problematicCheckbox = createProblematicKanjiCheckbox();

		rangesPanel = new MainPanel(BasicColors.VERY_LIGHT_BLUE, true);
		Border b = BorderFactory.createLineBorder(BasicColors.VERY_BLUE);
		// scrollPane = new JScrollPane(rangesPanel.getPanel());
		scrollPane = GuiMaker.createScrollPane(BasicColors.DARK_BLUE, b, rangesPanel.getPanel(),
				new Dimension(300, 200));
		addRowToPanel();

		JTextField problematicKanjis = createProblematicRangeField(Prompts.problematicKanjiPrompt);

		JButton newRow = createButtonAddRow(ButtonsNames.buttonAddRowText, rangesPanel);
		sumRangeField = GuiMaker.createTextField(1, Prompts.sumRangePrompt);
		// addComponentsAtLevel(level, new JComponent[] { newRow, sumRangeField
		// });

		JButton cancel = CommonActionsMaker.createButtonDispose(ButtonsNames.buttonCancelText,
				java.awt.event.KeyEvent.VK_ESCAPE, parentDialog);
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

	private JCheckBox createProblematicKanjiCheckbox() {
		final JCheckBox problematicCheckbox = new JCheckBox(Options.problematicKanjiOption);
		problematicCheckbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateProblematicKanjiNumber();

			}
		});
		if (getProblematicKanjiNumber() == 0) {
			problematicCheckbox.setEnabled(false);
		}

		AbstractAction action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (problematicCheckbox.isEnabled()) {
					problematicCheckbox.setSelected(!problematicCheckbox.isSelected());
					updateProblematicKanjiNumber();
				}

			}
		};

		main.getPanel().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), "close");
		main.getPanel().getActionMap().put("close", action);

		return problematicCheckbox;

	}

	private void updateProblematicKanjiNumber() {
		int problematicKanjis = getProblematicKanjiNumber();
		if (problematicCheckbox.isSelected())
			sumOfWords += problematicKanjis;
		else
			sumOfWords -= problematicKanjis;
		System.out.println("should update");
		updateSumOfWords();
	}

	private void addRowToPanel() {

		JLabel from = new JLabel("od");
		JTextField[] textFields = createTextFieldsForRangeInput(rangesPanel, rangesPanel.getPanel(),
				rangesPanel.getNumberOfRows());
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

	private JTextField[] createTextFieldsForRangeInput(final MainPanel container,
			final JPanel otherPanel, final int rowNumber) {
		JTextField[] textFields = new JTextField[2];
		for (int i = 0; i < 2; i++) {
			textFields[i] = new JTextField(5);
			((AbstractDocument) textFields[i].getDocument()).setDocumentFilter(
					new LimitDocumentFilter(NumberValues.INTEGER_MAX_VALUE_DIGITS_AMOUNT));
		}
		final JTextField from = textFields[0];
		final JTextField to = textFields[1];

		KeyAdapter keyAdapter = new KeyAdapter() {

			private String error = "";

			@Override
			public void keyTyped(KeyEvent e) {
				if ((e.getKeyChar() == KeyEvent.VK_ENTER)) {
					validateAndStart(otherPanel);
				}
				else if (!(e.getKeyChar() + "").matches("\\d")) {
					showErrorIfNotExists(ExceptionsMessages.valueIsNotNumber);
					e.consume();
					return;
				}

			}

			@Override
			public void keyReleased(KeyEvent e) {

				int valueFrom = 0;
				int valueTo = 0;
				if (to.getText().isEmpty() || from.getText().isEmpty())
					return;
				else {
					valueFrom = Integer.parseInt(from.getText());
					valueTo = Integer.parseInt(to.getText());
				}

				if (valueTo <= valueFrom) {
					showErrorIfNotExists(ExceptionsMessages.rangeToValueLessThanRangeFromValue);
				}
				else if (isNumberHigherThanMaximum(valueFrom) || isNumberHigherThanMaximum(valueTo))
					showErrorIfNotExists(ExceptionsMessages.rangeValueTooHigh);
				else {
					removeErrorIfExists();
					recalculateSumOfKanji(container.getPanel());
				}

			}

			private boolean isNumberHigherThanMaximum(int number) {
				return number > numberOfWords;
			}

			private void showErrorIfNotExists(String message) {
				if (error.equals(message))
					return;
				else
					removeErrorIfExists();

				container.addElementsToRow(rowNumber, new JLabel(message));
				error = message;
			}

			private void removeErrorIfExists() {
				if (error.isEmpty())
					return;
				error = "";
				JPanel row = container.getRows().get(rowNumber);
				for (Component c : row.getComponents()) {
					if (c instanceof JLabel && ((JLabel) c).getText()
							.matches(ExceptionsMessages.rangeToValueLessThanRangeFromValue + "|"
									+ ExceptionsMessages.valueIsNotNumber + "|"
									+ ExceptionsMessages.rangeValueTooHigh)) {
						System.out.println("remove ing");
						container.removeLastElementFromRow(rowNumber);
					}
				}
			}

		};

		from.addKeyListener(keyAdapter);
		to.addKeyListener(keyAdapter);

		textFields[0] = from;
		textFields[1] = to;
		return textFields;
	}

	private void recalculateSumOfKanji(JPanel container) {
		try {
			SetOfRanges s = validateInputs(container);
			sumOfWords = 0;
			if (problematicCheckbox.isSelected()) {
				sumOfWords += getProblematicKanjiNumber(); // TODO duplicated
															// code
			}
			this.sumOfWords += s.sumRangeInclusive();
			updateSumOfWords();

		}
		catch (IllegalArgumentException ex) {
			// We keep the message for untill approve button is clicked
		}

	}

	private void updateSumOfWords() {
		sumRangeField.setText(Prompts.sumRangePrompt + sumOfWords);
	}

	private int getProblematicKanjiNumber() {
		if (parentFrame instanceof ApplicationWindow) {
			ApplicationWindow p = (ApplicationWindow) parentFrame;
			return p.getProblematicKanjis().size();
		}
		else
			return 0;
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
				// deleteRow(container, panelToRemove);
			}
		});
		return delete;
	}

	private void deleteRow(JPanel container, JPanel panelToDelete) {

		removeRowAndUpdateOtherRows(panelToDelete, container);
		if (container.getComponentCount() == 1) {
			JPanel firstRow = (JPanel) container.getComponent(0);
			for (Component c : firstRow.getComponents()) {
				// if (c instanceof JButton)
				// firstRow.remove(c);
			}
		}
		recalculateSumOfKanji(container);
		container.repaint();
		container.revalidate();
		rowsNumber--;

	}

	private void removeRowAndUpdateOtherRows(JPanel rowToDelete, JPanel panel) {
		boolean found = false;
		for (int i = 0; i < panel.getComponentCount(); i++) {
			if (panel.getComponent(i) == rowToDelete) {
				panel.remove(i);
				found = true;
				i--;
				continue;
			}
			if (!found)
				continue;

			JPanel row = (JPanel) panel.getComponent(i);
			GridBagLayout g = (GridBagLayout) panel.getLayout();
			GridBagConstraints c = g.getConstraints(row);
			c.gridy--;
			g.setConstraints(row, c);
		}
	}

	private JButton createButtonAddRow(String text, final MainPanel panel) {
		JButton button = new JButton(text);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addRowToPanel();
				// parentDialog.repaint();
				// parentDialog.revalidate();
				scrollPane.getVerticalScrollBar()
						.setValue(scrollPane.getVerticalScrollBar().getMaximum());

			}
		});
		return button;
	}

	private JTextField createSumRangeField(String text) {
		JTextField sumRange = new JTextField(text, 30);
		sumRange.setEditable(false);
		return sumRange;

	}

	private JTextField createProblematicRangeField(String text) {
		JTextField sumRange = new JTextField(text);
		sumRange.setEditable(false);
		if (parentFrame instanceof ApplicationWindow) {
			ApplicationWindow b = (ApplicationWindow) parentFrame;
			sumRange.setText(sumRange.getText() + b.getProblematicKanjis().size());
		}

		return sumRange;
	}

	private JButton createButtonStartLearning(String text, final JPanel panel) {
		JButton button = new JButton(text);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				validateAndStart(panel);
			}
		});
		return button;
	}

	private void validateAndStart(JPanel panel) {
		try {
			rangesToRepeat = validateInputs(panel);
			addToRepeatsListOrShowError(rangesToRepeat);
			switchToRepeatingPanel();

		}
		catch (Exception ex) {
			ex.printStackTrace();
			parentDialog.showMsgDialog(ex.getMessage());
		}
	}

	private void addToRepeatsListOrShowError(SetOfRanges setOfRanges) throws Exception {
		if (setOfRanges.getRangesAsString().isEmpty() && !problematicCheckbox.isSelected())
			throw new Exception(ExceptionsMessages.noInputSupplied);

		Calendar calendar = Calendar.getInstance();

		RepeatingList l = (RepeatingList) repeatsList.getWords();
		System.out.println("L: " + l);
		String repeatingInfo = "";
		if (problematicCheckbox.isSelected()) {
			repeatingInfo = Options.problematicKanjiOption;
			if (setOfRanges.getRangesAsList().size() > 0) {
				repeatingInfo += ", ";
			}
			else {
				repeatingInfo += ".";
			}
		}

		repeatingInfo += setOfRanges.getRangesAsString();
		if (parentFrame instanceof ApplicationWindow) {
			ApplicationWindow parent = (ApplicationWindow) parentFrame;
			parent.setRepeatingInformation(
					new RepeatingInformation(repeatingInfo, calendar.getTime(), false));
		}
		// repeatsList.addWord(((RowAsJLabel)repeatsList.getRowCreator()).addWord(rep,
		// rowsNumber));
		repeatsList.scrollToBottom();

	}

	private void switchPanels(SetOfRanges wordsToLearn) {
		if (parentFrame instanceof ApplicationWindow) {
			ApplicationWindow parent = (ApplicationWindow) parentFrame;
			parent.showCardPanel(ApplicationWindow.LEARNING_PANEL);
			parent.setWordsRangeToRepeat(wordsToLearn, problematicCheckbox.isSelected());
		}
	}

	private void waitUntillExcelLoads() {
		switchToRepeatingPanel();

	}

	private void switchToRepeatingPanel() {
		parentDialog.getContainer().dispose();
		switchPanels(rangesToRepeat);
	}

	private SetOfRanges validateInputs(JPanel panel) {

		SetOfRanges setOfRanges = new SetOfRanges();
		boolean wasSetModifiedTotally = false;
		for (Component p : panel.getComponents()) {
			JPanel row;
			if (p instanceof JPanel) {
				row = (JPanel) p;
			}
			else
				continue;

			boolean wasSetModifiedInInteration = getRangeFromRowAndAddToSet(row, setOfRanges);
			wasSetModifiedTotally = wasSetModifiedTotally || wasSetModifiedInInteration;

		}

		return setOfRanges;

	}

	private boolean getRangeFromRowAndAddToSet(JPanel row, SetOfRanges set)
			throws IllegalArgumentException {
		boolean alteredSet = false;
		int textFieldsCounter = 1;
		int rangeStart = 0;
		int rangeEnd = 0;

		for (Component c : row.getComponents()) {
			if (c instanceof JTextField) {
				if (((JTextField) c).getText().isEmpty())
					break;
				if (textFieldsCounter == 1)
					rangeStart = getValueFromTextField((JTextField) c);
				else
					rangeEnd = getValueFromTextField((JTextField) c);
				textFieldsCounter++;
			}
			if (textFieldsCounter > 2) {
				if (rangeEnd > numberOfWords) {
					throw new IllegalArgumentException("Too much"); // TODO
																	// clean it
				}
				Range r = new Range(rangeStart, rangeEnd);
				alteredSet = set.addRange(r);
				textFieldsCounter = 1;
			}
		}
		return alteredSet;

	}

	private int getValueFromTextField(JTextField textField) {
		return Integer.parseInt(textField.getText());
	}

}

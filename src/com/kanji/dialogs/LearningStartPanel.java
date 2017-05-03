package com.kanji.dialogs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;

import com.kanji.Row.RepeatingList;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.ExceptionsMessages;
import com.kanji.constants.NumberValues;
import com.kanji.constants.Options;
import com.kanji.constants.Prompts;
import com.kanji.myList.MyList;
import com.kanji.range.Range;
import com.kanji.range.SetOfRanges;
import com.kanji.window.BaseWindow;
import com.kanji.window.LimitDocumentFilter;

public class LearningStartPanel {

	private JPanel mainPanel;
	private JScrollPane scrollPane;
	private JTextField sumRangeField;
	private JCheckBox problematicCheckbox;
	private int rowsNumber;
	private MyDialog parentDialog;
	private MyList<RepeatingList> repeatsList;
	private Window parentFrame;
	private SetOfRanges rangesToRepeat;
	private int numberOfWords;
	private int sumOfWords;

	public LearningStartPanel(JPanel panel, MyDialog parent, Window parentOfParent,
			int numberOfWords) {
		this.numberOfWords = numberOfWords;
		mainPanel = panel;
		parentDialog = parent;
		this.parentFrame = parentOfParent;
	}

	public JPanel createPanel(MyList list) { // TODO add focus to textfield from
		if (!excelReaderIsLoaded())
			loadExcel();
		repeatsList = list;
		int level = 0;
		addPromptAtLevel(level, createPrompt(Prompts.learnStartPrompt));

		level++;
		problematicCheckbox = createProblematicKanjiCheckbox();
		addPromptAtLevel(level, problematicCheckbox);

		level++;
		JPanel panel = addTextFieldsForRange(level);
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 1;
		scrollPane = createScrollPane(panel, level);

		level++;
		JTextField problematicKanjis = createProblematicRangeField(Prompts.problematicKanjiPrompt);
		addComponentsAtLevel(level, new JComponent[] { problematicKanjis });

		level++;
		JButton newRow = createButtonAddRow(ButtonsNames.buttonAddRowText, panel);
		sumRangeField = createSumRangeField(Prompts.sumRangePrompt);
		addComponentsAtLevel(level, new JComponent[] { newRow, sumRangeField });

		level++;
		JButton cancel = parentDialog.createButtonDispose(ButtonsNames.buttonCancelText,
				javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
		JButton approve = createButtonStartLearning(ButtonsNames.buttonApproveText, panel);

		addComponentsAtLevel(level, new JButton[] { cancel, approve });
		return mainPanel;
	}

	private JCheckBox createProblematicKanjiCheckbox() {
		final JCheckBox problematicCheckbox = new JCheckBox(Options.problematicKanjiOption);
		problematicCheckbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int problematicKanjis = getProblematicKanjiNumber();
				if (problematicCheckbox.isSelected())
					sumOfWords += problematicKanjis;
				else
					sumOfWords -= problematicKanjis;
				updateSumOfWords();
			}
		});
		if (getProblematicKanjiNumber() == 0) {
			problematicCheckbox.setEnabled(false);
		}

		return problematicCheckbox;

	}

	private void loadExcel() {
		if (parentFrame instanceof BaseWindow) {
			BaseWindow p = (BaseWindow) parentFrame;
			p.loadExcelReader();
		}
	}

	private GridBagConstraints createDefaultConstraints() {
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		return c;
	}

	private void addPromptAtLevel(int level, Component component) { // TODO it
																	// is in
																	// different
																	// classes
		GridBagConstraints layoutConstraints = createDefaultConstraints();
		layoutConstraints.gridy = level;
		layoutConstraints.anchor = GridBagConstraints.CENTER;
		layoutConstraints.weightx = 1;
		layoutConstraints.fill = GridBagConstraints.HORIZONTAL;

		mainPanel.add(component, layoutConstraints);
	}

	private JTextArea createPrompt(String message) {
		JTextArea elem = new JTextArea(message);
		elem.setLineWrap(true);
		elem.setWrapStyleWord(true);
		elem.setOpaque(false);
		elem.setEditable(false);
		return elem;
	}

	private JPanel addTextFieldsForRange(int level) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		addRowToPanel(panel);
		GridBagConstraints layoutConstraints = createDefaultConstraints();
		layoutConstraints.gridy = level;
		layoutConstraints.anchor = GridBagConstraints.WEST;
		return panel;

	}

	private JScrollPane createScrollPane(JPanel panel, int level) {
		JScrollPane scrollPane = new JScrollPane(panel);

		GridBagConstraints layoutConstraints = createDefaultConstraints();
		layoutConstraints.gridy = level;
		layoutConstraints.weighty = 1;
		layoutConstraints.fill = GridBagConstraints.BOTH;

		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		scrollPane.setPreferredSize(new Dimension(500, 100));
		mainPanel.add(scrollPane, layoutConstraints);
		return scrollPane;
	}

	private void addRowToPanel(final JPanel panel) {

		final JPanel rowPanel = new JPanel();

		JLabel from = new JLabel("od");
		JTextField[] textFields = createTextFieldsForRangeInput(rowPanel);
		JTextField fieldFrom = textFields[0];
		JLabel labelTo = new JLabel("do");
		JTextField fieldTo = textFields[1];
		int singleInset = 5;
		Insets insets = new Insets(singleInset, singleInset, singleInset, singleInset);

		rowPanel.add(from);
		rowPanel.add(fieldFrom);
		rowPanel.add(labelTo);
		rowPanel.add(fieldTo);

		if (rowsNumber > 0) {
			JButton delete = createDeleteButton(panel, rowPanel);
			rowPanel.add(delete);
		}
		if (rowsNumber == 1) {
			JPanel firstRow = (JPanel) panel.getComponent(0);
			JButton delete = createDeleteButton(panel, firstRow);
			firstRow.add(delete);
		}

		GridBagConstraints c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 0;
		c.gridy = rowsNumber;

		panel.add(rowPanel, c);
		rowsNumber++;
	}

	private JTextField[] createTextFieldsForRangeInput(final JPanel container) {
		JTextField[] textFields = new JTextField[2];
		for (int i = 0; i < 2; i++) {
			textFields[i] = new JTextField(10);
			((AbstractDocument) textFields[i].getDocument()).setDocumentFilter(
					new LimitDocumentFilter(NumberValues.INTEGER_MAX_VALUE_DIGITS_AMOUNT));
		}
		final JTextField from = textFields[0];
		final JTextField to = textFields[1];

		KeyAdapter keyAdapter = new KeyAdapter() {

			private String error = "";

			@Override
			public void keyTyped(KeyEvent e) {
				if (!(e.getKeyChar() + "").matches("\\d")) {
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
					recalculateSumOfKanji((JPanel) container.getParent());
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

				container.add(new JLabel(message));
				container.repaint();
				container.revalidate();
				error = message;
			}

			private void removeErrorIfExists() {
				if (error.isEmpty())
					return;
				error = "";

				for (Component c : container.getComponents()) {
					if (c instanceof JLabel && ((JLabel) c).getText()
							.matches(ExceptionsMessages.rangeToValueLessThanRangeFromValue + "|"
									+ ExceptionsMessages.valueIsNotNumber + "|"
									+ ExceptionsMessages.rangeValueTooHigh)) {
						container.remove(c);
						container.repaint();
						container.revalidate();
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
		if (parentFrame instanceof BaseWindow) {
			BaseWindow p = (BaseWindow) parentFrame;
			return p.getProblematicKanjis().size();
		}
		else
			return 0;
	}

	private JButton createDeleteButton(final JPanel container, final JPanel panelToRemove) {
		JButton delete = new JButton(ButtonsNames.buttonRemoveRowText);
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteRow(container, panelToRemove);
			}
		});
		return delete;
	}

	private void deleteRow(JPanel container, JPanel panelToDelete) {

		removeRowAndUpdateOtherRows(panelToDelete, container);
		if (container.getComponentCount() == 1) {
			JPanel firstRow = (JPanel) container.getComponent(0);
			for (Component c : firstRow.getComponents()) {
				if (c instanceof JButton)
					firstRow.remove(c);
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

	private JButton createButtonAddRow(String text, final JPanel panel) {
		JButton button = new JButton(text);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addRowToPanel(panel);
				parentDialog.repaint();
				parentDialog.revalidate();
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
		JTextField sumRange = new JTextField(text, 30);
		sumRange.setEditable(false);
		if (parentFrame instanceof BaseWindow) {
			BaseWindow b = (BaseWindow) parentFrame;
			sumRange.setText(sumRange.getText() + b.getProblematicKanjis().size());
		}

		return sumRange;
	}

	private JButton createButtonStartLearning(String text, final JPanel panel) {
		JButton button = new JButton(text);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					rangesToRepeat = validateInputs(panel);
					addToRepeatsListOrShowError(rangesToRepeat);

					if (!excelReaderIsLoaded()) {
						parentDialog.showErrorDialogInNewWindow(ExceptionsMessages.excelNotLoaded);
						waitUntillExcelLoads();
					}
					else
						switchToRepeatingPanel();

				}
				catch (Exception ex) {
					ex.printStackTrace();
					parentDialog.showErrorDialogInNewWindow(ex.getMessage());
				}

			}
		});
		return button;
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
		repeatsList.getWords().add(repeatingInfo, calendar.getTime(), false);
		// repeatsList.addWord(((RowAsJLabel)repeatsList.getRowCreator()).addWord(rep,
		// rowsNumber));
		repeatsList.scrollToBottom();

	}

	private void switchPanels(SetOfRanges wordsToLearn) {
		if (parentFrame instanceof BaseWindow) {
			BaseWindow parent = (BaseWindow) parentFrame;
			parent.showCardPanel(BaseWindow.LEARNING_PANEL);
			parent.setWordsRangeToRepeat(wordsToLearn, problematicCheckbox.isSelected());
		}
	}

	public boolean excelReaderIsLoaded() {
		if (parentFrame instanceof BaseWindow) {
			BaseWindow parent = (BaseWindow) parentFrame;
			return parent.isExcelLoaded();
		}
		else
			return false; // TODO or throw exception
	}

	private void waitUntillExcelLoads() {
		switchToRepeatingPanel();

	}

	private void switchToRepeatingPanel() {
		parentDialog.dispose();
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

	private void addComponentsAtLevel(int level, JComponent[] components) { // TODO
																			// this
																			// method
																			// occurs
																			// in
																			// multiple
																			// classes
		JPanel panel = new JPanel();
		for (JComponent button : components)
			panel.add(button);

		GridBagConstraints layoutConstraints = createDefaultConstraints();
		layoutConstraints.gridy = level;

		mainPanel.add(panel, layoutConstraints);
	}

}

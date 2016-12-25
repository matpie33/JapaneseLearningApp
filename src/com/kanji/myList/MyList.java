package com.kanji.myList;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.text.Normalizer;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Scrollable;

import com.kanji.constants.ExceptionsMessages;
import com.kanji.window.ClassWithDialog;
import com.kanji.window.ElementMaker;

public class MyList<Parameters> extends JPanel implements Scrollable {
	private static final long serialVersionUID = -5024951383001795390L;
	private List<JPanel> panels;
	private int highlightedRowNumber;
	private Color defaultRowColor = Color.RED;
	private Color highlightedRowColor = Color.BLUE;
	private Color bgColor = Color.GREEN;
	private JScrollPane parentScrollPane;
	private Parameters words;
	private ClassWithDialog parent;
	private String title;
	private ElementMaker elementsMaker;

	public Dimension getPreferredScrollableViewportSize() {
		return super.getPreferredSize();
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 16;
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 16;
	}

	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public MyList(ClassWithDialog parentDialog, String title, RowsCreator rowsCreator, ElementMaker element) {

		this.elementsMaker = element;
		rowsCreator.setList(this);
		this.title = title;
		this.parent = parentDialog;
		createDefaultScrollPane();
		initiate();
	}

	private void createDefaultScrollPane() {
		this.parentScrollPane = new JScrollPane();
	}

	private void initiate() {
		// this.wordsAndID = new LinkedHashMap<Integer,String>();
		this.highlightedRowNumber = 0;
		this.panels = new LinkedList();
		setLayout(new GridBagLayout());
		setBackground(this.bgColor);
		createTitle();
	}

	private void createTitle() {
		add(new JLabel(this.title));
	}

	public void addWord(JPanel row) {
		// words.add(parameters);
		// this.wordsAndID.put(Integer.valueOf(number), word);
		this.panels.add(row);
		GridBagConstraints c = createConstraintsForNewRow();
		add(row, c);
		// repaint(row.getLocation().x, row.getLocation().y,
		// row.getSize().width, row.getSize().height);
		repaint();

	}

	private GridBagConstraints createConstraintsForNewRow() {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = 13;
		c.gridx = 0;
		c.gridy = (this.panels.size() + 1);
		int a = 5;
		c.insets = new Insets(a, a, a, a);
		c.fill = 2;
		c.weightx = 1;
		return c;
	}

	public boolean findAndHighlightNextOccurence(String searchedWord, int searchDirection, SearchOptions options)
			throws Exception {
		searchedWord = removeDiacritics(searchedWord);
		int lastRowToSearch = this.highlightedRowNumber;
		String highlightedWord = getHighlightedWord();
		for (int rowNumber = this.highlightedRowNumber
				+ searchDirection; rowNumber != lastRowToSearch; rowNumber += searchDirection) {
			if (isRowNumberOutOfRange(rowNumber)) {
				rowNumber = setRowNumberToTheOtherEndOfList(rowNumber);
			} else {
				String word = findWordInRow(rowNumber);
				word = removeDiacritics(word);
				if (doesWordContainSearchedWord(word, searchedWord, options)) {
					highlightAndScrollToRow(rowNumber);
					return true;
				}
			}
		}
		if (doesWordContainSearchedWord(highlightedWord, searchedWord, options)) {
			throw new Exception(ExceptionsMessages.wordAlreadyHighlightedException);
		}
		return false;
	}

	private String getHighlightedWord() {
		String word = findWordInRow(this.highlightedRowNumber);
		return removeDiacritics(word);
	}

	private String removeDiacritics(String word) {
		word = Normalizer.normalize(word, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		word = word.replace("ł", "l").replace("Ł", "L");
		return word;
	}

	private boolean isRowNumberOutOfRange(int rowNumber) {
		return (rowNumber < 0) || (rowNumber > this.panels.size() - 1);
	}

	private int setRowNumberToTheOtherEndOfList(int rowNumber) {
		if (rowNumber < 0) {
			return this.panels.size();
		}
		if (rowNumber >= this.panels.size()) {
			return -1;
		}
		return rowNumber;
	}

	public String findWordInRow(int rowNumber) {
		JPanel panel = (JPanel) this.panels.get(rowNumber);
		JTextArea textArea = new JTextArea();
		try {
			textArea = (JTextArea) findElementInsideOrCreate(panel, JTextArea.class);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			sendErrorToParent(e);
		}
		return textArea.getText();
	}

	private boolean doesWordContainSearchedWord(String word, String searched, SearchOptions options) {
		if (options.isMatchByWordEnabled()) {
			return doesPhraseContainSearchedWords(word, searched);
		}
		if (options.isMatchByExpressionEnabled()) {
			return doesPhraseEqualToSearchedWords(word, searched);
		}
		return doesPhraseContainSearchedCharacterChain(word, searched);
	}

	private boolean doesPhraseContainSearchedWords(String phrase, String searched) {
		return phrase.matches(".*\\b" + searched + "\\b.*");
	}

	private boolean doesPhraseEqualToSearchedWords(String phrase, String searched) {
		return phrase.equals(searched);
	}

	private boolean doesPhraseContainSearchedCharacterChain(String phrase, String characterChain) {
		return phrase.contains(characterChain);
	}

	private void highlightAndScrollToRow(int rowNumber) {
		removeHighlightedPanelIfThereIs();
		highlightPanelAndScrollTo(rowNumber);
	}

	private void removeHighlightedPanelIfThereIs() {
		if (this.highlightedRowNumber >= 0) {
			((JPanel) this.panels.get(this.highlightedRowNumber)).setBackground(this.defaultRowColor);
		}
	}

	private void highlightPanelAndScrollTo(int rowNumber) {
		JPanel panel = (JPanel) this.panels.get(rowNumber);
		panel.setBackground(this.highlightedRowColor);
		this.highlightedRowNumber = rowNumber;
		scrollTo(panel);
		repaint();
	}

	public void removeRowContainingTheWord(JPanel word) {
		try {
			int rowNumber = removeRowContainingWordAndReturnRowNumber(word);
			updateRowNumbersAfterThatRow(rowNumber);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			sendErrorToParent(e);
		}
		revalidate();
		repaint();
	}

	private int removeRowContainingWordAndReturnRowNumber(JPanel word)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		// int rowNumber = 0;
		// while (rowNumber < this.panels.size())
		// {
		// JPanel panel = (JPanel)this.panels.get(rowNumber);
		// JTextArea text = (JTextArea)findElementInsideOrCreate(panel,
		// JTextArea.class);
		// if (text.getText().equals(word))
		// {
		// remove(panel);
		// this.panels.remove(panel);
		// break;
		// }
		// rowNumber++;
		// }
		remove(word);
		int rowNumber = panels.indexOf(word);
		panels.remove(word);
		return rowNumber;
	}

	private Object findElementInsideOrCreate(JPanel panel, Class classTemp)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Component[] arrayOfComponent;
		int j = (arrayOfComponent = panel.getComponents()).length;
		for (int i = 0; i < j; i++) {
			Component com = arrayOfComponent[i];
			if (classTemp.isInstance(com)) {
				return classTemp.cast(com);
			}
		}
		return classTemp.newInstance();
	}

	private void updateRowNumbersAfterThatRow(int rowNumber)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		while (rowNumber < this.panels.size()) {
			JPanel panel = (JPanel) this.panels.get(rowNumber);
			JLabel label = (JLabel) findElementInsideOrCreate(panel, JLabel.class);

			Integer newValue = Integer.valueOf(Integer.parseInt(label.getText()) - 1);
			label.setText(newValue.toString());
			rowNumber++;
		}
	}

	public void scrollTo(JPanel panel) {
		int r = panel.getY();
		this.parentScrollPane.getViewport().setViewPosition(new Point(0, r));
	}

	public void setScrollPane(JScrollPane scr) {
		this.parentScrollPane = scr;
	}

	public JScrollPane returnMe(JScrollPane scrollPane) {
		this.parentScrollPane = scrollPane;
		return this.parentScrollPane;
	}

	public void setWords(Parameters parameters) {
		this.words = parameters;
		updateWords();
		scrollToBottom();
	}

	public void updateWords() {
		cleanAll();
		createTitle();
		System.out.println("update");
		// for (Parameters word : this.words) {
		// addWord(word);
		// }
	}

	private void cleanAll() {
		removeAll();
		this.panels.clear();
		System.out.println("clean");
	}

	public void scrollToBottom() {
		this.parent.revalidate();
		revalidate();
		this.parentScrollPane.revalidate();
		System.out.println(this.panels.size() + "panels size");
		JScrollBar scrollBar = this.parentScrollPane.getVerticalScrollBar();
		scrollBar.setValue(scrollBar.getMaximum());
	}

	private void sendErrorToParent(Exception e) {
		this.parent.showMessageDialog(e.getMessage(), true);
	}

	public Parameters getWords() {
		return this.words;
	}

	public void save() {
		this.elementsMaker.save();
	}
	
	public boolean showMessage (String message){
	    return parent.showConfirmDialog(message);
	}
	
}

package com.kanji.myList;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.text.Normalizer;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.kanji.constants.ExceptionsMessages;
import com.kanji.window.BaseWindow;
import com.kanji.window.ElementMaker;

public class MyList<Parameters> extends JPanel {
	private static final long serialVersionUID = -5024951383001795390L;
//	private List<JPanel> panels;
	private int highlightedRowNumber;
	private Color defaultRowColor = Color.RED;
	private Color highlightedRowColor = Color.BLUE;
	private Color bgColor = Color.GREEN;
	private JScrollPane parentScrollPane;
	private BaseWindow parent;
	private String title;
	private ElementMaker elementsMaker;
	private ListContentsManager <Parameters> contentManager;	

	public MyList(BaseWindow parentDialog, String title, ElementMaker element) {
		
		this.elementsMaker = element;
		
//		add(rowsCreator.getPanel(), BorderLayout.CENTER);
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
//		this.panels = new LinkedList();
	}


	
	public void addWord(JPanel row, Parameters info) {
		
		contentManager.addRow(info);
		// repaint(row.getLocation().x, row.getLocation().y,
		// row.getSize().width, row.getSize().height);

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
		return (rowNumber < 0) || (rowNumber > contentManager.getNumberOfWords() - 1);
	}

	private int setRowNumberToTheOtherEndOfList(int rowNumber) {
		if (rowNumber < 0) {
			return contentManager.getNumberOfWords();
		}
		if (rowNumber >= contentManager.getNumberOfWords()) {
			return -1;
		}
		return rowNumber;
	}

	public String findWordInRow(int rowNumber) {
		JPanel panel = contentManager.getRowsCreator().getRow(rowNumber);
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
			contentManager.getRowsCreator().getRow(this.highlightedRowNumber).
			setBackground(this.defaultRowColor);
		}
	}

	private void highlightPanelAndScrollTo(int rowNumber) {
		JPanel panel = contentManager.getRowsCreator().getRow(rowNumber);
		panel.setBackground(this.highlightedRowColor);
		this.highlightedRowNumber = rowNumber;
		scrollTo(panel);
		repaint();
	}


	public int removeRowContainingWordAndReturnRowNumber(Parameters contents){
		
		
		try {
			contentManager.remove(contents);
		} 
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		repaint();
		revalidate();
		return 1;
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

	public void setWords(ListContentsManager <Parameters> parameters) {
		this.contentManager = parameters;
		updateWords();
		scrollToBottom();
	}

	public void updateWords() {
		cleanAll();
		System.out.println("update");
		// for (Parameters word : this.words) {
		// addWord(word);
		// }
	}

	private void cleanAll() {
		removeAll();
//		contentManager.getRowsCreator().clear();
		System.out.println("clean");
	}

	public void scrollToBottom() {
		this.parent.getWindow().revalidate();
		revalidate();
		this.parentScrollPane.revalidate();
//		System.out.println(this.panels.size() + "panels size");
		JScrollBar scrollBar = this.parentScrollPane.getVerticalScrollBar();
		scrollBar.setValue(scrollBar.getMaximum());
	}

	private void sendErrorToParent(Exception e) {
		this.parent.showMessageDialog(e.getMessage(), true);
	}


	public void save() {
		this.elementsMaker.save();
	}
	
	public boolean showMessage (String message){
	    return parent.showConfirmDialog(message);
	}
	
	
	public ListContentsManager<Parameters> getContentManager(){
		return contentManager;		
	}
	
}

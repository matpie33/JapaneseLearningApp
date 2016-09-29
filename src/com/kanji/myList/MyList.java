package com.kanji.myList;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Scrollable;
import com.kanji.constants.TextValues;
import com.kanji.window.ClassWithDialog;

@SuppressWarnings("serial")
public class MyList extends JPanel implements Scrollable{
	
	private List <JPanel> panels;
	private int highlightedRowNumber;
	private Color defaultRowColor = Color.GREEN;
	private Color highlightedRowColor = Color.BLUE;
	private Color bgColor = Color.GREEN;
	private JScrollPane parentScrollPane;	
	private Map <String, Integer> wordsAndID;
	private ClassWithDialog parent;
	private String title;
	private RowsCreator rowsCreator;
	
	
	@Override
	public Dimension getPreferredScrollableViewportSize() {
        return super.getPreferredSize(); 
    }
	
	@Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 16;
    }
	
	@Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 16;
    }

	@Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

	@Override
    public boolean getScrollableTracksViewportHeight() {
        return false; 
    }
	
	public MyList(ClassWithDialog parentDialog, String title, RowsCreator rowsCreator){	
		this.rowsCreator=rowsCreator;
		rowsCreator.setList(this);
		this.title=title;
		parent=parentDialog;
		createDefaultScrollPane();			
		initiate();				
	}
	
	private void createDefaultScrollPane(){
		parentScrollPane = new JScrollPane();
	}
	
	private void initiate(){
		wordsAndID = new HashMap <String, Integer> ();
		highlightedRowNumber=0;
		panels = new LinkedList <JPanel>();	
		setLayout(new GridBagLayout());
		setBackground(bgColor);
		createTitle();	
	}
	
	private void createTitle(){
		add(new JLabel (title));
	}
	
	public void addWord (String word, int number){
		wordsAndID.put(word,number);
		JPanel row = rowsCreator.addWord(word, panels.size()+1);
		panels.add(row);
		GridBagConstraints c = createConstraintsForNewRow();
		add(row,c);
	}
	
	private GridBagConstraints createConstraintsForNewRow(){
		GridBagConstraints c = new GridBagConstraints ();
		c.anchor=GridBagConstraints.EAST;
		c.gridx=0;
		c.gridy=panels.size()+1;
		int a =5;
		c.insets= new Insets(a,a,a,a);
		c.fill=GridBagConstraints.HORIZONTAL;
		c.weightx=1;
		return c;
	}
	

	
	public void findAndHighlightNextOccurence(String searched, int searchDirection, 
			Set<Integer> options) throws Exception{
				
		searched=removeDiacritics(searched);
		int lastRowToSearch=highlightedRowNumber;	
		String highlightedWord = getHighlightedWord();		
		
		for (int rowNumber=highlightedRowNumber+searchDirection; rowNumber!=lastRowToSearch; 
				rowNumber+=searchDirection){
		
			if (isRowNumberOutOfRange(rowNumber)){
				rowNumber=setRowNumberToTheOtherEndOfList(rowNumber);
				continue; //to check if its not the last row to search
			}				
			
			String word = findWordInRow(rowNumber);
			word=removeDiacritics(word);						
			
			if (doesWordContainSearchedWord(word,searched,options)){
				highlightAndScrollToRow(rowNumber);				
				return;
			}
						
		}
		
		if (doesWordContainSearchedWord(highlightedWord,searched,options))
			throw new Exception (TextValues.wordAlreadyHighlightedException);		
		else throw new Exception (TextValues.wordSearchExceptionWordNotFound);
	}
	
	private String getHighlightedWord(){
		String word = findWordInRow(highlightedRowNumber);
		return removeDiacritics(word);
	}
	
	private String removeDiacritics(String word){
		word=Normalizer.normalize(word, Normalizer.Form.NFD)
	            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		word=word.replace("³", "l").replace("£", "L");
		return word;
	}
		
	private boolean isRowNumberOutOfRange (int rowNumber){
		return rowNumber<0 || rowNumber>panels.size()-1;
	}
	
	private int setRowNumberToTheOtherEndOfList (int rowNumber){
		
		if (rowNumber < 0)
			return panels.size();
		else if (rowNumber>=panels.size())
			return -1;
		else return rowNumber;
	}
	
	private String findWordInRow (int rowNumber){
		JPanel panel = panels.get(rowNumber);
		JTextArea textArea = new JTextArea();
		try {
			textArea = (JTextArea) findElementInsideOrCreate(panel, JTextArea.class);
		} 
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			sendErrorToParent(e);
		}
		return textArea.getText();
	}
	
	private boolean doesWordContainSearchedWord(String word, String searched, Set<Integer> options){
		if (options.contains(new Integer(1))){
			return doesPhraseContainSearchedWords(word,searched);
		}
		else if (options.contains(new Integer(2))){
			return doesPhraseEqualToSearchedWords(word,searched);
		}
		else{
			return doesPhraseContainSearchedCharacterChain(word,searched);
		}
	}
	
	private boolean doesPhraseContainSearchedWords(String phrase, String searched){
		return phrase.matches(".*\\b"+searched+"\\b.*");
	}
	
	private boolean doesPhraseEqualToSearchedWords(String phrase, String searched){
		return phrase.equals(searched);
	}
	
	private boolean doesPhraseContainSearchedCharacterChain(String phrase, String characterChain){
		return phrase.contains(characterChain);		
	}
	
	private void highlightAndScrollToRow(int rowNumber){			
		removeHighlightedPanelIfThereIs();
		highlightPanelAndScrollTo (rowNumber);	
	}
	
	private void removeHighlightedPanelIfThereIs(){
		if (highlightedRowNumber>=0)
			panels.get(highlightedRowNumber).setBackground(defaultRowColor);
	}
	
	private void highlightPanelAndScrollTo (int rowNumber){
		JPanel panel = panels.get(rowNumber);
		panel.setBackground(highlightedRowColor);
		highlightedRowNumber=rowNumber;
		scrollTo(panel);
		repaint();
	}	
	
	public void removeRowContainingTheWord(String word) { 

		int rowNumber;
		try {
			rowNumber = removeRowContainingWordAndReturnRowNumber(word);
			updateRowNumbersAfterThatRow(rowNumber);
		}
		catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException e) {
			sendErrorToParent(e);
		}
				
		
		revalidate();
		repaint();
	}

	private int removeRowContainingWordAndReturnRowNumber(String word) throws ClassNotFoundException, 
		InstantiationException, IllegalAccessException{
		int rowNumber=0;
		while (rowNumber<panels.size()){
			JPanel panel = panels.get(rowNumber);
			JTextArea text = (JTextArea)findElementInsideOrCreate(panel, JTextArea.class);
			
			if (text.getText().equals(word)){				
				remove(panel);
				panels.remove(panel);				
				break;
			}
			rowNumber++;				
		}
		return rowNumber;
	}

	private Object findElementInsideOrCreate(JPanel panel, Class classTemp) throws ClassNotFoundException, 
		InstantiationException, IllegalAccessException{		
		for (Component com: panel.getComponents()){
			if (classTemp.isInstance(com)){
				return classTemp.cast(com);
			}
		}
		return classTemp.newInstance();
	}


	private void updateRowNumbersAfterThatRow(int rowNumber) throws ClassNotFoundException, 
											InstantiationException, IllegalAccessException{
		while (rowNumber<panels.size()){
			JPanel panel = panels.get(rowNumber);
			JLabel label = (JLabel)findElementInsideOrCreate(panel, JLabel.class);
			
			Integer newValue = Integer.parseInt(label.getText())-1;
			label.setText(newValue.toString());
			rowNumber++;
		}
	}
	
	public void scrollTo (JPanel panel){		
		int r = panel.getY();
		parentScrollPane.getViewport().setViewPosition(new Point(0,r));
	}
	
				
		
	public void setScrollPane (JScrollPane scr){
		parentScrollPane=scr;
	}
	
	public JScrollPane returnMe (JScrollPane scrollPane){
		parentScrollPane=scrollPane;
		return parentScrollPane;
	}		
	
	public void setWords(Map <String, Integer> words){
		this.wordsAndID=words;
		updateWords();
		scrollToBottom();
	}
	
	private void updateWords (){
		cleanAll();
		createTitle();
		for (String word: wordsAndID.keySet())
			addWord(word, wordsAndID.get(word));

	}
	
	private void cleanAll(){
		removeAll();
		panels.clear();
	}
	
	public boolean isWordIdUndefinedYet(int number){
		return !wordsAndID.containsValue(number);
	}
	
	public boolean isWordUndefinedYet(String searched){
		for (String word: wordsAndID.keySet()){
			if (removeDiacritics(word).equals(removeDiacritics(searched)))
				return false;
			
		}
		return true;
	}
	
	public void scrollToBottom(){
		parent.revalidate();
		JScrollBar scrollBar = parentScrollPane.getVerticalScrollBar();
		scrollBar.setValue(scrollBar.getMaximum());
	}
	
	public int getWordsCount (){
		return wordsAndID.size();
	}
	
	public void sendErrorToParent (Exception e){
		parent.showMessageDialog(e.getMessage());
	}
	
	
	
	
}

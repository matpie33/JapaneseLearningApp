package com.kanji.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Scrollable;
import javax.swing.text.DefaultCaret;

import com.kanji.textValues.TextValues;

@SuppressWarnings("serial")
public class MyList extends JPanel implements Scrollable{
	
	private List <JPanel> panels;
	private int highlightedPanel;
	private Color defaultColor = Color.RED;
	private Color highlightedColor = Color.BLUE;
	private JScrollPane scroll;
	private Color bgColor = Color.pink;
	private Map <String, Integer> words;
	
	public Dimension getPreferredScrollableViewportSize() {
        return super.getPreferredSize(); //tell the JScrollPane that we want to be our 'preferredSize' - but later, we'll say that vertically, it should scroll.
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 16;//set to 16 because that's what you had in your code.
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 16;//set to 16 because that's what you had set in your code.
    }

    public boolean getScrollableTracksViewportWidth() {
        return true;//track the width, and re-size as needed.
    }

    public boolean getScrollableTracksViewportHeight() {
        return false; //we don't want to track the height, because we want to scroll vertically.
    }
	
	public MyList(){
	
		highlightedPanel=0;
		panels = new LinkedList <JPanel>();		
		initLayout();
		scroll = new JScrollPane(this);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getVerticalScrollBar().setUnitIncrement(20);
		setBackground(bgColor);
	}
	
	private void initLayout(){
		setLayout(new GridBagLayout());
	}

	private void addElement (final String text){
				
		JPanel row = new JPanel ();	
		row.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints ();
		JLabel number = new JLabel (""+(panels.size()+1));
		number.setForeground(Color.white);
		
		GridBagConstraints cd = new GridBagConstraints();
		cd.gridx=0;
		cd.gridy=0;
		cd.weightx=0;
		cd.anchor=GridBagConstraints.CENTER;
		cd.insets=new Insets(5,5,5,5);
		
		final JTextArea elem = new JTextArea(text);
		elem.setLineWrap(true);
		elem.setWrapStyleWord(true);
		elem.setOpaque(true);
		
		elem.addFocusListener(new FocusAdapter (){
			@Override
			public void focusGained (FocusEvent e){
				System.out.println("numerek: "+words.get(elem.getText()));
			}
		});
		
		row.add(number,cd);
		
		cd.gridx=1;
		cd.weightx=1;
		cd.fill=GridBagConstraints.HORIZONTAL;
		row.add(elem,cd);
		
		JButton remove = new JButton("-");
		remove.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed(ActionEvent e){
				removeElement(text);
			}
		});
		
		cd.gridx=2;
		cd.weightx=0;
		row.add(remove,cd);
		panels.add(row);
		row.setBackground(defaultColor);
		c.anchor=GridBagConstraints.EAST;
		c.gridy=panels.size();
		c.fill=GridBagConstraints.HORIZONTAL;
		c.weightx=1;
		add(row,c);		
		
		revalidate();
		repaint();
		
	}
	
	
	public void search(String searched, int direction, Set<Integer> options) throws Exception{
		
		int stop;
		searched=removeDiacritics(searched);
		stop=highlightedPanel;
		
		
		for (int i=highlightedPanel+direction; i!=stop; i+=direction){
			if (i<0){
				i=panels.size();
				continue;
			}
				
			else if (i>=panels.size()){
				i=-1;
				continue;
				}
			JPanel panel = panels.get(i);
			JTextArea tarea = findTextArea(panel);
			String word = tarea.getText();
			word=Normalizer.normalize(word, Normalizer.Form.NFD)
		            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
			word=removeDiacritics(word);
			boolean success=false;
			
			if (options.contains(new Integer(1))){
				success=searchFullWord(word,searched);
			}
			else if (options.contains(new Integer(2))){
				success=searchFullPhrase(word,searched);
			}
			else{
				success=searchDefault(word,searched);
			}
			if (success){
				highlight(i);
				scrollTo(panel);
				return;
			}
						
		}
		throw new Exception (TextValues.wordSearchExceptionWordNotFound);
	}
	
	
	private boolean searchFullWord(String bigWord, String searched){
		return bigWord.matches(".*\\b"+searched+"\\b.*");
	}
	
	private boolean searchFullPhrase(String bigWord, String searched){
		return bigWord.equals(searched);
	}
	
	private boolean searchDefault(String word, String searched){
		return word.contains(searched);
		
	}
	
	private void removeElement(String elem){ 
		int i=0;
		while (i<panels.size()){
			JPanel panel = panels.get(i);
			JTextArea text = findTextArea(panel);
				if (text.getText().equals(elem)){				
					remove(panel);
					panels.remove(panel);				
					break;
				}
			i++;				
		}
		
		while (i<panels.size()){
			JPanel panel = panels.get(i);
			JLabel label = findLabel(panel);
			Integer a = Integer.parseInt(label.getText());
			a=a-1;
			label.setText(a.toString());
			i++;
		}
		revalidate();
		repaint();
	}
	
	private void highlight(int i){
		if (highlightedPanel>=0)
			panels.get(highlightedPanel).setBackground(defaultColor);
		panels.get(i).setBackground(highlightedColor);
		highlightedPanel=i;
		repaint();
			
	}
	
	public void cleanAll(){
		removeAll();
		panels.clear();
	}
	
	private JTextArea findTextArea(JPanel panel){
		for (Component com: panel.getComponents()){
			if (com instanceof JTextArea){
				return (JTextArea)com;
			}
		}
		return new JTextArea();
	}
	
	private JLabel findLabel (JPanel panel){
		for (Component com: panel.getComponents()){
			if (com instanceof JLabel){
				return (JLabel)com;
			}
		}
		return new JLabel();
	}
	
	private String removeDiacritics(String word){
		word=Normalizer.normalize(word, Normalizer.Form.NFD)
	            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		word=word.replace("³", "l").replace("£", "L");
		return word;
	}
	
	private void scrollTo (JPanel panel){						
		int r = panel.getY();
		scroll.getViewport().setViewPosition(new Point(0,r));
	}
	
	public JScrollPane returnMe (){
		return scroll;
	}
	
	public void addWord (String word, int number){
		words.put(word,number);
		addElement(word);
	}
	
	public void setWords(Map <String, Integer> words){
		this.words=words;
		updateWords();
	}
	
	private void updateWords (){
		for (String word: words.keySet())
			addElement(word);
			
	}
	
	public boolean isWordIdUndefinedYet(int number){
		return !words.containsValue(number);
	}
	
	public boolean isWordUndefinedYet(String searched){
		for (String word: words.keySet()){
			if (removeDiacritics(word).equals(removeDiacritics(searched)))
				return false;
			
		}
		return true;
	}
	
	
}

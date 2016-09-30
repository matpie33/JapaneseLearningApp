package com.kanji.window;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.kanji.myList.MyList;
import com.kanji.range.Range;
import com.kanji.range.SetOfRanges;

public class RepeatingWordsPanel extends JPanel{
	private MyList words;
	private List <String> wordsToRepeat; //TODO add to repeating list information about whether specified row
										// contains words that were learned completely or aborted/paused
	private BaseWindow parent;
	
	public RepeatingWordsPanel (BaseWindow parent){
		wordsToRepeat = new LinkedList <String> ();
		setLayout(new GridBagLayout());
		this.parent=parent;		
		createPanel();
		
	}
	
	private void createPanel(){
		int level=0;
		addTitle("Repeating words",level);
		if (!wordsToRepeat.isEmpty()){
			level++;
			addRepeatingPanel(level);
		}
		level++;
		addButtons(level);
	}
	
	private void addTitle(String title, int level){
		GridBagConstraints c = createDefaultConstraints();
		c.gridy=level;
		c.anchor=GridBagConstraints.CENTER;
		
		add (new JLabel(title),c);
		
	}
	
	private void addRepeatingPanel(int level){
		
		JPanel panel = new JPanel (new GridBagLayout());
		JButton pause = new JButton ("pauza");
		JButton easyWord = new JButton ("Znam");
		JButton hardWord = new JButton ("Nie pamietam");
		
		JButton [] buttons = new JButton [] {pause, easyWord, hardWord};
		
		GridBagConstraints c = createDefaultConstraints();		
		c.gridx=0;
		c.gridy=0;
		c.gridwidth = buttons.length;
		c.anchor=GridBagConstraints.CENTER;
		
		JTextArea j = new JTextArea(wordsToRepeat.get(0));
		j.setLineWrap(true);
		j.setWrapStyleWord(true);
		j.setEditable(false);
		j.setOpaque(false);		
		
		panel.add(j,c);
		
		c.gridwidth = 1;
		c.gridy++;
		
		for (JButton button: buttons){
			panel.add(button,c);
			c.gridx++;
		}		
				
		GridBagConstraints d = new GridBagConstraints();
		d.gridy=level;
		d.anchor=GridBagConstraints.CENTER;
		d.weighty=1;
		
		add(panel,d);
		
	}
	
	private GridBagConstraints createDefaultConstraints(){
		GridBagConstraints c = new GridBagConstraints();
		c.weightx=1;
		int a = 5;
		c.insets = new Insets(a,a,a,a);
		return c;
	}
	
	private void addButtons(int level){
		JButton returnButton = new JButton ("Powrot");
		returnButton.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){
				parent.showCardPanel(BaseWindow.LIST_PANEL);
			}
		});
		
		
		GridBagConstraints c = createDefaultConstraints();
		c.gridy=level;
		c.anchor= GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		
		add(returnButton,c);
		
	}
	
	public void setRepeatingWords(MyList wordsList){
		wordsToRepeat = new LinkedList <String> ();
		words = wordsList;	
	}
	
	public void setRangesToRepeat(SetOfRanges ranges){
		for (Range range: ranges.getRangesAsList()){
			for (int i=range.getRangeStart(); i<=range.getRangeEnd(); i++){
				wordsToRepeat.add(words.findWordInRow(i-1));
			}
		}
		removeAll();
		createPanel();
		System.out.println(wordsToRepeat);
	}
	

}

package com.kanji.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import com.kanji.constants.TextValues;
import com.kanji.myList.MyList;

@SuppressWarnings("serial")
public class BaseWindow extends ClassWithDialog {
	
	private Insets insets = new Insets (20,20,20,20);
	private ElementMaker maker;
	private JScrollPane listScrollWords;
	private JScrollPane listScrollRepeated;
	private final Dimension scrollPanesSize = new Dimension (300,300);
	
	public BaseWindow (){
		
		maker = new ElementMaker(this);
		
		Container upper = createUpperPanel();	
		Container lower = createButtonsPanel(maker.getButtons());		// i wstawiac go np. jako poziom
												// 0, poziom 1, poziom 2 itd wtedy mamy elastyczny kod
		
		putPanelsTogetherAndSetContentPane(upper,lower);
		setWindowProperties();		
		
	}
	
	private JSplitPane createUpperPanel(){	
		
//		JPanel panel = new JPanel();
//		panel.setLayout(new BorderLayout());
//		panel.setBackground(Color.BLUE);
		
		MyList wordsList = maker.getWordsList();
		MyList repeatsList = maker.getRepeatsList();		
		listScrollWords = createScrollPaneForList(wordsList);
		listScrollRepeated = createScrollPaneForList(repeatsList);	
//		panel.add(,BorderLayout.WEST);
//		panel.add(listScrollRepeated,BorderLayout.CENTER);
		JSplitPane j = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT, listScrollWords,listScrollRepeated);
				
		return j;		
	}	
	
	private void createAndAddKanjiArea(JPanel panel){
		Border raisedBevel = BorderFactory.createLineBorder(Color.BLUE,6);			
		JTextArea kanjiArea = createKanjiArea(raisedBevel);		
		panel.add(kanjiArea,BorderLayout.WEST);		
	}	
	
	private JTextArea createKanjiArea (Border border){
		
		JTextArea kanjiArea = new JTextArea();
		kanjiArea.setBackground(Color.GREEN);
		kanjiArea.setPreferredSize(scrollPanesSize);
		kanjiArea.setEditable(false);	
		kanjiArea.setBorder(border);
		return kanjiArea;
		
	}
	
	private JScrollPane createScrollPaneForList(MyList list){
		
		Border raisedBevel = BorderFactory.createLineBorder(Color.BLUE,6);		
		
		JScrollPane listScrollWords = createScrollPane(Color.GREEN,raisedBevel, list);			
		list.setScrollPane(listScrollWords);
		
		return listScrollWords;
		
	}	
	
	private JScrollPane createScrollPane (Color bgColor, Border border, Component component){
		
		JScrollPane scroll = new JScrollPane(component);
		scroll.getViewport().setBackground(bgColor);
		scroll.setBorder(border);
		scroll.getVerticalScrollBar().setUnitIncrement(20);		
		scroll.setPreferredSize(scrollPanesSize);		
		return scroll;
		
	}
	
	private JPanel createButtonsPanel(List <JButton> list){
		
		JPanel panel  = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBackground(Color.RED);
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.insets=insets;
		c.gridx=0;
		c.anchor=GridBagConstraints.WEST;
		c.weightx=1;
			
		for (int i=0; i<list.size();i++){
			if (indexIsHigherThanHalfOfSize(i,list.size()))
				c.anchor=GridBagConstraints.EAST;
			panel.add(list.get(i),c);
			c.gridx++;
		}				
						
		return panel;
	}
	
	private boolean indexIsHigherThanHalfOfSize(int i, int size){
		return i>(size-1)/2;
	}
	
	private void putPanelsTogetherAndSetContentPane(Container up, Container down){
		
		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		main.setBackground(Color.RED);
				
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridy=0;		
		main.add(up, BorderLayout.CENTER);		
		
		c.gridy=1;
		main.add(down, BorderLayout.SOUTH);		
		
		setContentPane(main);
		
	}
	
	private void setWindowProperties(){
		pack();
		setMinimumSize(getSize());
		setTitle(TextValues.appTitle);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	
}

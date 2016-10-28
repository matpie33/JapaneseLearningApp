package com.kanji.myList;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class RowAsJLabel extends RowsCreator{

	private Color defaultColor = Color.RED; 
	private MyList list;
		
	@Override
	public JPanel addWord(String word, int rowsNumber) {
		JPanel rowPanel = createPanel();
		
		JLabel number = createNumberLabel(rowsNumber);
		JTextArea repeatedWords = createTextArea(word);
		JTextArea date = createDateArea();
		
		Component [] components = {number, repeatedWords, date};
		addComponentsToPanel(rowPanel, components);		

		return rowPanel;
		
	}
	
	private JPanel createPanel(){
		JPanel panel = new JPanel(new GridBagLayout());		
		panel.setBackground(defaultColor);
		return panel;
	}
	
	private JLabel createNumberLabel (int rowsNumber){
		return new JLabel(""+rowsNumber);
	}
	
	private JTextArea createTextArea(String text){
	
		JTextArea elem = new JTextArea(text);
		elem.addKeyListener(new KeyAdapter(){
			@Override
			public void keyReleased (KeyEvent e){
				try {
					FileOutputStream fout = new FileOutputStream ("hi.txt");
					ObjectOutputStream oos = new ObjectOutputStream(fout);
					oos.writeObject(list.getWordsWithIds());
					fout.close();
					System.out.println("save");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		elem.setLineWrap(true);
		elem.setWrapStyleWord(true);
		elem.setOpaque(true);
		return elem;
	}
	
	private JTextArea createDateArea(){
		JTextArea textArea = createTextArea("");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();		
		textArea.setText(sdf.format(calendar.getTime()));
		textArea.setEditable(false);
		
		return textArea;
	}
	
	private void addComponentsToPanel (JPanel panel, Component [] components){		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=0;
		c.gridy=0;
		int a = 5;
		c.insets=new Insets(a,a,a,a);
		c.weightx=1;
		c.weighty=1;
		c.fill= GridBagConstraints.HORIZONTAL;
		
		int componentNumber = 1;
		for (Component component: components){
			c.anchor = setPosition(componentNumber, components.length);
			panel.add(component,c);
			c.gridx++;
			componentNumber++;			
		}		
	}
	
	private int setPosition(int componentNumber, int numberOfComponents){
		int anchor=0;
		if (componentNumber==1)
			anchor=GridBagConstraints.WEST;
		else if (componentNumber == numberOfComponents)
			anchor = GridBagConstraints.EAST;
		else anchor = GridBagConstraints.CENTER;			
		return anchor;
	}

	@Override
	public void setList(MyList list) {
		this.list=list;		
	}
	


}

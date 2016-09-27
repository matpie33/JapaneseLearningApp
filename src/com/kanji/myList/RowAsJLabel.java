package com.kanji.myList;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class RowAsJLabel implements RowsCreator {

	private List <JTextArea> rows;
	private MyList list;
	private Color defaultColor = Color.RED; 
	
	public RowAsJLabel (){
		rows = new LinkedList <JTextArea> ();
	}
	
	@Override
	public void addWord(String word) {
		JPanel rowPanel = createPanel();
		
		JLabel number = createNumberLabel();
		JTextArea repeatedWords = createTextArea(word);
		JTextArea date = createDateArea();
		
		Component [] components = {number, repeatedWords, date};
		addComponentsToPanel(rowPanel, components);		

		GridBagConstraints c = createConstraints();
		rows.add(repeatedWords);
		list.add(rowPanel,c);
		
	}
	
	private JPanel createPanel(){
		JPanel panel = new JPanel(new GridBagLayout());		
		panel.setBackground(defaultColor);
		return panel;
	}
	
	private JLabel createNumberLabel (){
		return new JLabel(rows.size()+1 +" ");
	}
	
	private JTextArea createTextArea(String text){
	
		JTextArea elem = new JTextArea(text);
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
	
	private GridBagConstraints createConstraints(){
		GridBagConstraints c = new GridBagConstraints ();
		c.anchor=GridBagConstraints.EAST;
		c.gridx=0;
		c.gridy=rows.size()+1;
		int a =5;
		c.insets= new Insets(a,a,a,a);
		c.fill=GridBagConstraints.HORIZONTAL;
		c.weightx=1;
		return c;
	}

	@Override @Deprecated
	public Object findElementInsideOrCreate(JPanel panel, Class classs)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setList(MyList list) {
		this.list=list;
		
	}

}

package com.kanji.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.kanji.textValues.TextValues;

@SuppressWarnings("serial")
public class BaseWindow extends ClassWithDialog {
	
	private GridBagLayout g;
	private Insets insets = new Insets (20,20,20,20);
	private ElementMaker maker;
	private JScrollPane listScroll;
	
	public BaseWindow (){
		
		maker = new ElementMaker(this);
		
		JPanel upper = createUpperPanel();	//TODO nie upper i lower, tylko poziomy panel
		JPanel lower = createLowerPanel(maker.getButtons());		// i wstawiac go np. jako poziom
												// 0, poziom 1, poziom 2 itd wtedy mamy elastyczny kod
		
		putPanelsTogether(upper,lower);
		finishSetup();		
		
	}
	
	private JPanel createUpperPanel(){
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		Dimension areaSize = new Dimension(300,300);
		
		JTextArea j = new JTextArea();
		j.setBackground(Color.green);
		j.setPreferredSize(areaSize);
		j.setEditable(false);
		
		Border raisedbevel = BorderFactory.createLineBorder(Color.black,6);		
		
		TitledBorder title= BorderFactory.createTitledBorder(raisedbevel,"Kanji");
		title.setTitleJustification(TitledBorder.CENTER);
		
		j.setBorder(raisedbevel);
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=0;
		c.insets=insets;		
		panel.add(j,BorderLayout.WEST);		
		
		listScroll = maker.getMyList();
		
		TitledBorder titled2= BorderFactory.createTitledBorder(raisedbevel,"Kanji");
		titled2.setTitleJustification(TitledBorder.CENTER);
		listScroll.setBorder(raisedbevel);
		listScroll.setPreferredSize(areaSize);
		
		c.gridx=1;		
		panel.add(listScroll,BorderLayout.CENTER);
		
		return panel;
		
	}
	
	private JPanel createLowerPanel(List <JButton> list){
		
		JPanel p  = new JPanel();
		p.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.insets=insets;
		c.gridx=0;
		c.anchor=GridBagConstraints.WEST;
		c.weightx=1;
			
		for (int i=0; i<list.size();i++){
			if (i>(list.size()-1)/2)
				c.anchor=GridBagConstraints.EAST;
			p.add(list.get(i),c);
			c.gridx++;
		}				
						
		return p;
	}
	
	private void putPanelsTogether(JPanel up, JPanel down){
		
		JPanel main = new JPanel();
		g = new GridBagLayout();
		main.setLayout(new BorderLayout());
		main.setBackground(Color.RED);
				
		GridBagConstraints c = new GridBagConstraints();
		c.gridy=0;		
		main.add(up, BorderLayout.CENTER);
		up.setBackground(Color.BLUE);
		
		c.gridy=1;
		c.anchor=GridBagConstraints.WEST;
		c.fill=GridBagConstraints.HORIZONTAL;
		main.add(down, BorderLayout.SOUTH);
		down.setBackground(Color.RED);
		
		setContentPane(main);
		
	}
	
	private void finishSetup(){
		pack();
		setMinimumSize(getSize());
		setTitle(TextValues.appTitle);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	
}

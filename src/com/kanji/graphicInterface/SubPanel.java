package com.kanji.graphicInterface;

import java.awt.GridLayout;

import javax.swing.JPanel;

public class SubPanel {
	
	private MainPanel leftPanel;
	private MainPanel rightPanel;
	private JPanel panel;
	
	public SubPanel (){
		super();
		panel = new JPanel();
		panel.setLayout(new GridLayout(1,2));
		leftPanel = new MainPanel(MyColors.LIGHT_VIOLET);
		rightPanel = new MainPanel(MyColors.LIGHT_BLUE);
		panel.add (leftPanel.getPanel());
		panel.add (rightPanel.getPanel());
	}
	
	public MainPanel getLeft (){
		return leftPanel;
	}
	
	public MainPanel getRight(){
		return rightPanel;
	}
	
	public JPanel getPanel(){
		return panel;
	}

}

package com.kanji.graphicInterface;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

import com.kanji.myList.MyList;

public class GuiMaker {

	private static Color greyColor = new Color(190, 190, 190);
	private static final Dimension scrollPanesSize = new Dimension(300, 300);
	private static final Dimension minimumListSize = new Dimension(200, 100);

	public static JLabel createLabel(String title) {
		JLabel l = new JLabel(title);
		return l;
	}

	public static JTextArea createTextArea(boolean editable) {
		JTextArea j = new JTextArea(5, 10);
		j.setWrapStyleWord(true);
		j.setLineWrap(true);
		j.setEditable(editable);
		if (!editable)
			j.setBackground(greyColor);
		return j;
	}

	public static JButton createButton(String title, ActionListener listener) {
		JButton b = new JButton(title);
		b.addActionListener(listener);
		return b;
	}
	
	public static JScrollPane createScrollPaneForList(MyList list) {
		Border raisedBevel = BorderFactory.createLineBorder(Color.BLUE, 6);

		JScrollPane listScrollWords = createScrollPane(Color.GREEN, raisedBevel, list);
		list.setScrollPane(listScrollWords);
		listScrollWords.setMinimumSize(minimumListSize);

		return listScrollWords;
	}
	
	public static JScrollPane createScrollPane(Color bgColor, Border border, Component component) {
		JScrollPane scroll = new JScrollPane(component);
		scroll.getViewport().setBackground(bgColor);
		scroll.setBorder(border);
		scroll.getVerticalScrollBar().setUnitIncrement(20);
		scroll.setPreferredSize(scrollPanesSize);
		return scroll;
	}
	
	public static JTextField createTextField (int textLength){
		return new JTextField(20);			
	}
	
	public static JRadioButton createRadioButton (String text, ActionListener listener){
		JRadioButton radioButton = new JRadioButton(text);	
		radioButton.addActionListener(listener);
		return radioButton;
	}

}

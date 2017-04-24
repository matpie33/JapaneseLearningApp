package com.kanji.graphicInterface;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;

import com.kanji.myList.MyList;
import com.kanji.window.LimitDocumentFilter;

public class GuiMaker {

	
	private static final Dimension scrollPanesSize = new Dimension(300, 300);
	private static final Dimension minimumListSize = new Dimension(200, 100);
	private static final Dimension paneSize = new Dimension (100,100);

	public static JLabel createLabel(String title) {
		JLabel l = new JLabel(title);
		return l;
	}

	public static JTextArea createTextArea(boolean editable) {
		JTextArea j = new JTextArea();
		j.setWrapStyleWord(true);
		j.setLineWrap(true);
		j.setEditable(editable);
		if (!editable)
			j.setBackground(MyColors.GREY);
		return j;
	}
	
	public static JTextArea createTextArea (boolean editable, int maxCharacters){
		JTextArea j = createTextArea(editable);
		limitCharactersInTextField(j, maxCharacters);
		return j;
	}

	public static JButton createButton(String title, ActionListener listener) {
		JButton b = new JButton(title);
		b.addActionListener(listener);
		return b;
	}
	
	public static JScrollPane createScrollPaneForList(MyList list) {
		Border raisedBevel = BorderFactory.createLineBorder(Color.BLUE, 6);
//		System.out.println("MYLIST: "+list);
		if (list == null){
			return new JScrollPane();
		}
		JScrollPane listScrollWords = createScrollPane(MyColors.DARK_BLUE, raisedBevel, list);
		list.setScrollPane(listScrollWords);
		listScrollWords.setMinimumSize(minimumListSize);

		return listScrollWords;
	}
	
	public static JScrollPane createScrollPane(Color bgColor, Border border, Component component,Dimension size) {
		JScrollPane scroll = null;
		if (component instanceof MyList){
			MyList list = (MyList) component;
			scroll = new JScrollPane(list.getContentManager().getPanel());
		}
		else{
			scroll = new JScrollPane(component);
		}		
		scroll.getViewport().setBackground(bgColor);
		scroll.setBorder(border);
		scroll.getVerticalScrollBar().setUnitIncrement(20);
		scroll.setPreferredSize(size);
		return scroll;
	}
	
	public static JScrollPane createScrollPane(Color bgColor, Border border, Component component) {
		return createScrollPane(bgColor,border,component,scrollPanesSize);
	}
	
	public static JTextField createTextField (int textLength){
		JTextField textField = new JTextField(textLength);
		limitCharactersInTextField(textField, textLength);
		return textField;			
	}
	
	public static JTextField createTextField (int textLength, String text){
		return new JTextField (text,textLength);
	}
	
	private static void limitCharactersInTextField(JTextComponent textField, int maxDigits) {
		((AbstractDocument) textField.getDocument())
				.setDocumentFilter(new LimitDocumentFilter(maxDigits));
	}
	
	public static JRadioButton createRadioButton (String text, ActionListener listener){
		JRadioButton radioButton = new JRadioButton(text);	
		radioButton.addActionListener(listener);
		return radioButton;
	}
	
	public static JCheckBox createCheckBox (String text, ActionListener listener){
		JCheckBox checkbox = new JCheckBox (text);
		checkbox.addActionListener(listener);
		return checkbox;
	}
	
	
	

}

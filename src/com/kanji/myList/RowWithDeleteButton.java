package com.kanji.myList;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class RowWithDeleteButton extends RowsCreator implements Serializable {
	private Color wordNumberColor = Color.WHITE;
	private Color defaultRowColor = Color.RED;
	private MyList list;
	private List <KeyAdapter> adapters;
	private String wordBeingModified;
	
	public RowWithDeleteButton (){
		adapters = new ArrayList <KeyAdapter>();
	}
	
	@Override
	public JPanel addWord (String text, int rowsNumber){				
		JPanel row = createNewRow(text, rowsNumber);	
		return row;
	}	
	
	
	private JPanel createNewRow(String text, int rowsNumber) {
		JPanel row = new JPanel ();	
		row.setLayout(new GridBagLayout());		
	
		JLabel number = new JLabel (""+rowsNumber);
		number.setForeground(wordNumberColor);
		
		GridBagConstraints cd = initiateGridBagConstraints();		
		row.add(number,cd);
		
		JTextArea textArea = createTextArea(text);
		cd.gridx++;
		cd.weightx=1;
		cd.fill=GridBagConstraints.HORIZONTAL;
		row.add(textArea,cd);
		
		JButton remove = createButtonRemove(text);		
		cd.gridx++;
		cd.weightx=0;
		row.add(remove,cd);
		
//		panels.add(row);
		row.setBackground(defaultRowColor);
		
		return row;
	}

	private GridBagConstraints initiateGridBagConstraints(){
		GridBagConstraints cd = new GridBagConstraints();
		cd.gridx=0;
		cd.gridy=0;
		cd.weightx=0;
		cd.anchor=GridBagConstraints.CENTER;
		cd.insets=new Insets(5,5,5,5);
		return cd;
	}
	
	private JTextArea createTextArea(String text){
		final JTextArea elem = new JTextArea(text);
		FocusListener focusListener = new FocusListener()
	    {
	      public void focusGained(FocusEvent e)
	      {
	        RowWithDeleteButton.this.wordBeingModified = elem.getText();
	      }
	      
	      public void focusLost(FocusEvent e)
	      {
	        if (RowWithDeleteButton.this.wordBeingModified.equals(elem.getText())) {
	          return;
	        }
	        RowWithDeleteButton.this.list.changeWord(RowWithDeleteButton.this.wordBeingModified, elem.getText());
	        
	        System.out.println(elem.getText());
	        System.out.println(RowWithDeleteButton.this.list.getWordsWithIds());
	        RowWithDeleteButton.this.wordBeingModified = "";
	        RowWithDeleteButton.this.list.save();
	      }
	    };
	    elem.addFocusListener(focusListener);
		
		elem.setLineWrap(true);
		elem.setWrapStyleWord(true);
		elem.setOpaque(true);
		return elem;
	}
	
	private JButton createButtonRemove(final String text){
		JButton remove = new JButton("-");
		remove.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed(ActionEvent e){				
				list.removeRowContainingTheWord(text);				
			}
		});
		return remove;
	}


	@Override
	public void setList(MyList list) {
		this.list=list;
		
	}
	
	
		
	
	
}

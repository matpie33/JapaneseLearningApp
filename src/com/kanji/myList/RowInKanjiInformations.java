package com.kanji.myList;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.kanji.Row.KanjiInformation;
import com.kanji.Row.KanjiWords;

public class RowInKanjiInformations extends RowsCreator<KanjiInformation> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color wordNumberColor = Color.WHITE;
	private Color defaultRowColor = Color.RED;
	private MyList<KanjiWords> list;
	private List <KeyAdapter> adapters;
	private String wordBeingModified;
	private int idBeingModified;
	
	public RowInKanjiInformations (MyList <KanjiWords> list){
		this.list=list;
		adapters = new ArrayList <KeyAdapter>();
	}
	
	@Override
	public JPanel addWord (KanjiInformation kanji, int rowsNumber){				
		JPanel row = createNewRow(kanji, rowsNumber);	
		return row;
	}	
	
	
	private JPanel createNewRow(KanjiInformation kanji, int rowsNumber) {
		String text = kanji.getKanjiKeyword();
		int ID = kanji.getKanjiID();
		JPanel row = new JPanel ();	
		row.setLayout(new GridBagLayout());		
	
		JLabel number = new JLabel (""+rowsNumber);
		number.setForeground(wordNumberColor);
		
		GridBagConstraints cd = initiateGridBagConstraints();		
		row.add(number,cd);
		
		JTextArea wordTextArea = createTextArea(text, String.class);
		cd.gridx++;
		cd.weightx=1;
		cd.fill=GridBagConstraints.HORIZONTAL;
		row.add(wordTextArea,cd);
		
		JTextArea idTextArea = createTextArea(Integer.toString(ID), Integer.class);
		cd.gridx++;
		cd.weightx=0;
		cd.fill=GridBagConstraints.NONE;
		row.add(idTextArea,cd);
		
		JButton remove = createButtonRemove(row, kanji);		
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
	
	private JTextArea createTextArea(String text, Class type){
		JTextArea elem = new JTextArea(text);
		FocusListener f;
		if (type == Integer.class)
			f = createIdChangeListener(elem);
		else
			f = createWordChangeListener(elem);
	    elem.addFocusListener(f);		
		elem.setLineWrap(true);
		elem.setWrapStyleWord(true);
		elem.setOpaque(true);
		return elem;
	}
	
	private FocusListener createWordChangeListener (final JTextArea elem){
		FocusListener focusListener = new FocusListener()
	    {
	      public void focusGained(FocusEvent e)
	      {
	        wordBeingModified = elem.getText();
	      }
	      
	      public void focusLost(FocusEvent e)
	      {
	        if (wordBeingModified.equals(elem.getText())) {
	          return;
	        }
//	        list.changeWord(wordBeingModified, elem.getText());
	        System.out.println(list);
	        list.getWords().changeWord(wordBeingModified, elem.getText());
	        wordBeingModified = "";
	        list.save();
	      }
	    };
	    return focusListener;
	}
	
	private FocusListener createIdChangeListener (final JTextArea elem){
		FocusListener focusListener = new FocusListener()
	    {
	      public void focusGained(FocusEvent e)
	      {
	    	  if (elem.getText().matches("\\d+"))
	        idBeingModified = Integer.parseInt(elem.getText());
	    	  // TODO else exception
	    		  
	      }
	      
	      public void focusLost(FocusEvent e)
	      {
	    	  int newID;
	    	  if (elem.getText().matches("\\d+")){
	    		  newID = Integer.parseInt(elem.getText());
	    	  }
	    	  else return;	    		  
	  	    	  // TODO else exception
	    	  
	        if (idBeingModified==Integer.parseInt(elem.getText())) {
	          return;	          
	        }
	        list.getWords().changeWord(idBeingModified, newID);
            list.save();
	        idBeingModified = -1;
	        
	      }
	    };
	    return focusListener;
	}
	
	private JButton createButtonRemove(final JPanel text, final KanjiInformation kanji){
		JButton remove = new JButton("-");
		remove.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed(ActionEvent e){	
			    	if (!list.showMessage("Sure?")){
			    	    return;
			    	}
				list.removeRowContainingTheWord(text);	
				list.getWords().remove(kanji);
				list.save();
			}
		});
		return remove;
	}


	@Override
	public void setList(MyList list) {
		this.list=list;
		
	}
	
	
		
	
	
}

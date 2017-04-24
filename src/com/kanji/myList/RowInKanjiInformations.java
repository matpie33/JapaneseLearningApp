package com.kanji.myList;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
import com.kanji.Row.KanjiInformation;
import com.kanji.graphicInterface.GuiMaker;
import com.kanji.listenersAndAdapters.ActionMaker;

public class RowInKanjiInformations implements RowsCreator<KanjiInformation> {
	
	private static final long serialVersionUID = 1L;
	private Color wordNumberColor = Color.WHITE;
	private Color defaultRowColor = Color.RED;
	private MyList<KanjiInformation> list;
	private List <KeyAdapter> adapters;
	private String wordBeingModified;
	private int idBeingModified;
	private MainPanel mainPanel;
	private int firstRow;
	
	public RowInKanjiInformations (MyList <KanjiInformation> list){
		mainPanel = new MainPanel (null,true,false);
		mainPanel.addRow(RowMaker.createUnfilledRow(GridBagConstraints.CENTER, new JLabel("slowka")));		
		this.list=list;
		adapters = new ArrayList <KeyAdapter>();
		firstRow =mainPanel.getNumberOfRows();
	}
	
	@Override
	public JPanel addWord (KanjiInformation kanji){				
		JPanel row = createNewRow(kanji);	
		return row;
	}		
	
	private JPanel createNewRow(KanjiInformation kanji) {
		String text = kanji.getKanjiKeyword();
		int ID = kanji.getKanjiID();	
	
		JLabel number = new JLabel (""+mainPanel.getNumberOfRows());
		number.setForeground(wordNumberColor);	
		
		JTextArea wordTextArea = GuiMaker.createTextArea(true, 100);
		wordTextArea.setText(text);
		JTextField idTextArea = GuiMaker.createTextField(5);
		idTextArea.setText(""+ID);
		JButton remove = GuiMaker.createButton("-",ActionMaker.removeRepeatingListRow(list, kanji));
		return mainPanel.addRow(RowMaker.createHorizontallyFilledRow(number,wordTextArea,idTextArea, 
				remove).fillHorizontallySomeElements(wordTextArea));
	
	
	
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
//	        list.getContentManager().changeWord(wordBeingModified, elem.getText());
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
//	        list.getWords().changeWord(idBeingModified, newID);
            list.save();
	        idBeingModified = -1;
	        
	      }
	    };
	    return focusListener;
	}
	


	@Override
	public void setList(MyList<KanjiInformation> list) {
		this.list=list;		
	}
	
	public JPanel getPanel(){
		return mainPanel.getPanel();
	}

	@Override
	public void removeRow (int rowNumber) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		mainPanel.removeRow(rowNumber+firstRow);
		updateLabelText(rowNumber+firstRow);
		System.out.println("row number: "+rowNumber +" deleted ");
	}
	
	private void updateLabelText(int rowNumber) 
			throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		while (rowNumber < mainPanel.getNumberOfRows()) {
			JPanel panel = mainPanel.getRows().get(rowNumber);
			JLabel label = (JLabel) findElementInsideOrCreate(panel, JLabel.class);
			String oldValue = label.getText();
			int oldInt = Integer.parseInt(oldValue);
			label.setText(""+(oldInt-1));

			rowNumber++;
		}
	}
	
	private Object findElementInsideOrCreate(JPanel panel, Class classTemp)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Component[] arrayOfComponent;
		int j = (arrayOfComponent = panel.getComponents()).length;
		for (int i = 0; i < j; i++) {
			Component com = arrayOfComponent[i];
			if (classTemp.isInstance(com)) {
				return classTemp.cast(com);
			}
		}
		return classTemp.newInstance();
	}

	@Override
	public JPanel getRow(int number) {
		return mainPanel.getRows().get(number);
	}
	
		
	
	
}

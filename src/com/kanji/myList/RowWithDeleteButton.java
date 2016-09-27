package com.kanji.myList;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class RowWithDeleteButton implements RowsCreator {
	private Color wordNumberColor = Color.RED;
	private Color defaultRowColor = Color.GREEN;
	private List <JPanel> panels;
	private MyList list;
	
	public RowWithDeleteButton (){
		panels = new LinkedList <JPanel>();
	}
	
	@Override
	public void setList(MyList list){
		this.list=list;
	}
	
	@Override
	public void addWord (String text){				
		JPanel row = createNewRow(text);
		GridBagConstraints c = createConstraintsForNewRow();		
		list.add(row,c);	
	}	
	
	private GridBagConstraints createConstraintsForNewRow(){
		GridBagConstraints c = new GridBagConstraints ();
		c.anchor=GridBagConstraints.EAST;
		c.gridx=0;
		c.gridy=panels.size()+1;
		c.fill=GridBagConstraints.HORIZONTAL;
		c.weightx=1;
		return c;
	}
	
	
	public JPanel createNewRow(String text) {
		JPanel row = new JPanel ();	
		row.setLayout(new GridBagLayout());		
	
		JLabel number = new JLabel (""+(panels.size()+1));
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
		
		panels.add(row);
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
		JTextArea elem = new JTextArea(text);
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
				try {
					removeRowContainingTheWord(text);
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException ex) {
					list.sendErrorToParent(ex);
				}
			}
		});
		return remove;
	}
	
	private void removeRowContainingTheWord(String word) throws ClassNotFoundException, 
												InstantiationException, IllegalAccessException{ 
		
		int rowNumber=removeRowContainingWordAndReturnRowNumber(word);
		updateRowNumbersAfterThatRow(rowNumber);		
		
		list.revalidate();
		list.repaint();
	}
	
	private int removeRowContainingWordAndReturnRowNumber(String word) throws ClassNotFoundException, 
												InstantiationException, IllegalAccessException{
		int rowNumber=0;
		while (rowNumber<panels.size()){
			JPanel panel = panels.get(rowNumber);
			JTextArea text = (JTextArea)findElementInsideOrCreate(panel, JTextArea.class);
			
				if (text.getText().equals(word)){				
					list.remove(panel);
					panels.remove(panel);				
					break;
				}
			rowNumber++;				
		}
		return rowNumber;
	}
	
	@Override
	public Object findElementInsideOrCreate(JPanel panel, Class classTemp) throws ClassNotFoundException, 
											InstantiationException, IllegalAccessException{		
		for (Component com: panel.getComponents()){
			if (classTemp.isInstance(com)){
				return classTemp.cast(com);
			}
		}
		return classTemp.newInstance();
	}
	
	
	private void updateRowNumbersAfterThatRow(int rowNumber) throws ClassNotFoundException, 
											InstantiationException, IllegalAccessException{
		while (rowNumber<panels.size()){
			JPanel panel = panels.get(rowNumber);
			JLabel label = (JLabel)findElementInsideOrCreate(panel, JLabel.class);
			
			Integer newValue = Integer.parseInt(label.getText())-1;
			label.setText(newValue.toString());
			rowNumber++;
		}
	}
		
	
	
}

package com.kanji.myList;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
import com.kanji.Row.RepeatingInformation;
import com.kanji.graphicInterface.GuiMaker;
import com.kanji.listenersAndAdapters.ActionMaker;
import com.kanji.listenersAndAdapters.AdaptersMaker;

public class RowInRepeatingList implements RowsCreator <RepeatingInformation>{

	private Color defaultColor = Color.RED; 
	private MyList<RepeatingInformation> list;
	private MainPanel mainPanel;
	
	public RowInRepeatingList (MyList<RepeatingInformation> list){
		this.list=list;
		mainPanel = new MainPanel(null,true,false);
		mainPanel.addRow(RowMaker.createUnfilledRow(GridBagConstraints.NORTH, new JLabel("Powtórki")));
	}
		
	@Override
	public JPanel addWord(RepeatingInformation rep) {
		String word = rep.getRepeatingRange();
		Date date1 = rep.getRepeatingDate();
		
		JLabel number = GuiMaker.createLabel(""+mainPanel.getNumberOfRows());				
		JTextArea repeatedWords = GuiMaker.createTextArea(true);
		repeatedWords.addKeyListener(AdaptersMaker.repeatingInformationChanged(list));
		repeatedWords.setText(word);
		JTextArea date = GuiMaker.createTextArea(false);
		date.setText(dateToString(date1));			
		JButton delete =  GuiMaker.createButton("X", ActionMaker.removeRepeatingListRow(list, rep));
		System.out.println("words!!:" +mainPanel.getNumberOfRows());
						
		return mainPanel.addRow(RowMaker.createHorizontallyFilledRow(number,repeatedWords,date,delete)
				.fillHorizontallySomeElements(date));		
	}
	
		
	private String dateToString(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");						
		return sdf.format(date);
	}
	
	@Override
	public void setList(MyList list) {
		this.list=list;		
	}
	
	@Override
	public JPanel getPanel(){
		return mainPanel.getPanel();
	}


	@Override
	public void removeRow(int rowNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public JPanel getRow(int number) {
		return mainPanel.getRows().get(number);
	}
	


}

package com.kanji.panels;

import java.awt.Color;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.kanji.constants.Prompts;
import com.kanji.graphicInterface.GuiMaker;
import com.kanji.graphicInterface.MainPanel;
import com.kanji.graphicInterface.MyColors;
import com.kanji.myList.MyList;
import com.kanji.window.ElementMaker;
import com.kanji.window.SavingStatus;

public class StartingPanel {
	private ElementMaker maker;
	private JScrollPane listScrollWords;
	private JScrollPane listScrollRepeated;
	private JSplitPane listsSplitPane;
	private JPanel infoPanel;
	private JPanel buttonsPanel;
	private JLabel saveInfo;
	private MainPanel panel;
	
	public StartingPanel (ElementMaker maker){
		
		panel = new MainPanel (MyColors.LIGHT_VIOLET);
		this.maker = maker;
		createUpperPanel();
		createInformationsPanel();
		createButtonsPanel(maker.getButtons());
		panel.addRow(panel.createBothSidesFilledRow(listsSplitPane).fillHorizontallyEqually().fillAllVertically());
//		panel.addRow(panel.createHorizontallyFilledRow(maker.getButtons().toArray(new JComponent[]{})).fillHorizontallyEqually().fillAllVertically());
		panel.addRow(panel.createHorizontallyFilledRow(buttonsPanel).fillHorizontallyEqually());
		panel.addRow(panel.createHorizontallyFilledRow(infoPanel).fillHorizontallyEqually());

		
		
		JLabel l = new JLabel();
		JButton b = new JButton();
		JTextField t = new JTextField();
		JTextArea a = new JTextArea();
//		panel.addRow(panel.createBothSidesFilledRow(
//				l,b,t,a).
//				fillHorizontallySomeElements(l,b).fillVertically(b,t,a)
//				);
		
		
		MainPanel panel = new MainPanel (MyColors.DARK_BLUE);
//		RowCreator r =panel.createHorizontallyFilledRow(new JButton("hi")).createVerticallyFilledRow();
		 panel.addRow(panel.createBothSidesFilledRow(new JButton(), new JLabel()));
//		System.out.println(r);
	}
	
	private void createUpperPanel() {
		MyList wordsList = maker.getWordsList();
		MyList repeatsList = maker.getRepeatsList();
		listScrollWords = GuiMaker.createScrollPaneForList(wordsList);
		listScrollRepeated = GuiMaker.createScrollPaneForList(repeatsList);
		listsSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollWords, listScrollRepeated);
		listsSplitPane.setResizeWeight(0.5);		
		panel.getPanel().repaint();
	}

	private void createInformationsPanel() {
		infoPanel = new JPanel();
		infoPanel.setBackground(Color.YELLOW);
		saveInfo = GuiMaker.createLabel("");
		changeSaveStatus(SavingStatus.BrakZmian);
		infoPanel.add(saveInfo);
	}

	private void createButtonsPanel(List<JButton> list) {

		MainPanel p = new MainPanel(MyColors.DARK_BLUE);
		buttonsPanel = p.getPanel();
		p.addRow(p.createHorizontallyFilledRow(list.toArray(new JButton [] {})));

	}
	
	public void changeSaveStatus(SavingStatus savingStatus) {
		saveInfo.setText(Prompts.savingStatusPrompt + savingStatus);
		infoPanel.repaint();
	}
	
	public void updateLeft() {
		listsSplitPane.setLeftComponent(GuiMaker.createScrollPaneForList(maker.getWordsList()));
		panel.getPanel().repaint();
	}
	
	public JPanel getPanel (){
		return panel.getPanel();
	}
	

}

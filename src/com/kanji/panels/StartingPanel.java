package com.kanji.panels;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

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
		panel.createRow(2,listsSplitPane);
		panel.createRow(buttonsPanel);
		panel.createRow(infoPanel);
	}
	
	private void createUpperPanel() {
		MyList wordsList = maker.getWordsList();
		MyList repeatsList = maker.getRepeatsList();
		listScrollWords = GuiMaker.createScrollPaneForList(wordsList);
		listScrollRepeated = GuiMaker.createScrollPaneForList(repeatsList);
		listsSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollWords, listScrollRepeated);
		
		
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
		p.createRow(GridBagConstraints.CENTER, 1, list.toArray(new JButton [list.size()]));

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
